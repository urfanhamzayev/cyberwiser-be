package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.phoenix_sat.phoenix_sat_backend.entity.RegistrationVerificationSession;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.gateway.EmailSenderGateway;
import com.phoenix_sat.phoenix_sat_backend.model.request.HtmlEmailRequest;
import com.phoenix_sat.phoenix_sat_backend.repository.RegistrationVerificationSessionRepository;
import com.phoenix_sat.phoenix_sat_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;
    private final RegistrationVerificationSessionRepository verificationSessionRepository;
    private final AmazonSimpleEmailService amazonSimpleEmailService;



    @Override
    @Transactional
    public void sendRegistrationCompleteEmail(List<? extends User> items) {
        for (User user : items) {
            var request = buildHtmlEmailRequest(user);

            var session = verificationSessionRepository.save(buildRegistrationVerificationSession(user.getId()));

            Map<String, Object> model = new HashMap<>();
            model.put("name", request.name());
            model.put("verificationId", session.getVerificationId());

            htmlSend(request, model);
        }

    }

    @Async
    public void htmlSend(HtmlEmailRequest HTMLRequest, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);

        String templateName = HTMLRequest.templateName();
        String to = HTMLRequest.to();
        String subject = HTMLRequest.subject();
        String body = templateEngine.process(templateName, context);

        log.debug("Processed HTML: {}", body);

        SendEmailRequest sendEmailRequest = getSendEmailRequest(to, subject, body, true);
        amazonSimpleEmailService.sendEmail(sendEmailRequest);

        log.info("Email sent successfully to {}", to);
    }

    private SendEmailRequest getSendEmailRequest(String toEmail, String subject, String body, Boolean isHtml) {
        return new SendEmailRequest()
                .withSource("start@phoenixmild.com")
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(new Message()
                        .withSubject(new Content(subject))
                        .withBody(isHtml ? new Body().withHtml(new Content(body))
                                : new Body().withText(new Content(body)))
                );
    }


    private static HtmlEmailRequest buildHtmlEmailRequest(User user) {
        return HtmlEmailRequest.builder()
                .name(user.getFullName())
                .templateName("registration-completion")
                .subject("Registration Completion")
                .to(user.getEmail())
                .build();
    }

    private RegistrationVerificationSession buildRegistrationVerificationSession(String userId) {
        return RegistrationVerificationSession.builder()
                .userId(userId)
                .verificationId(generateRandomUUID())
                .build();
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
