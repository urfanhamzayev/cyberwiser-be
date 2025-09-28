package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.event.RegistrationVerificationEvent;
import com.phoenix_sat.phoenix_sat_backend.model.request.RegistrationCompletionHtmlEmailRequest;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;
    private final AmazonSimpleEmailService amazonSimpleEmailService;


    @Override
    @Transactional
    public void sendRegistrationCompleteEmail(RegistrationVerificationEvent verificationEvent) {
        int index = 0;
        for (User user : verificationEvent.users()) {
            var request = buildHtmlEmailRequest(user,verificationEvent.temporaryPasswords().get(index));


            Map<String, Object> model = new HashMap<>();
            model.put("fullname", request.fullName());
            model.put("email", request.to());
            model.put("temp_password", verificationEvent.temporaryPasswords().get(index));
//            model.put("login_link", "http://sat-phoenix-load-balancer-942756280.eu-central-1.elb.amazonaws.com/swagger-ui/index.html#/public-controller/logIn");

            htmlSend(request, model);
            index++;
        }

    }


    @Async
    public void htmlSend(RegistrationCompletionHtmlEmailRequest htmlEmailRequest, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);

        String templateName = htmlEmailRequest.templateName();
        String to = htmlEmailRequest.to();

        String body = templateEngine.process(templateName, context);

        log.debug("Processed HTML: {}", body);

        SendEmailRequest sendEmailRequest = getSendEmailRequest(to, body);
        amazonSimpleEmailService.sendEmail(sendEmailRequest);

        log.info("Email sent successfully to {}", to);
    }

    private SendEmailRequest getSendEmailRequest(String toEmail, String body) {
        return new SendEmailRequest()
                .withSource("start@phoenixmild.com")
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(new Message()
                        .withSubject(new Content("Registration Completion"))
                        .withBody(new Body().withHtml(new Content(body)))
                );
    }


    private static RegistrationCompletionHtmlEmailRequest buildHtmlEmailRequest(User user,String tempPassword) {
        return RegistrationCompletionHtmlEmailRequest.builder()
                .fullName(user.getFullName())
                .templateName("registration-completion")
                .temporaryPassword(tempPassword)
                .to(user.getEmail())
                .build();
    }

}
