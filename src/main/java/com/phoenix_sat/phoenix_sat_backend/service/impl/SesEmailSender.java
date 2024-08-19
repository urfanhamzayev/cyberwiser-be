package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.phoenix_sat.phoenix_sat_backend.gateway.EmailSenderGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SesEmailSender implements EmailSenderGateway {
    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Autowired
    public SesEmailSender(AmazonSimpleEmailService amazonSimpleEmailService) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withSource("start@phoenixmild.com")
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(new Message()
                        .withSubject(new Content(subject))
                        .withBody(new Body().withText(new Content(body)))
                );

        amazonSimpleEmailService.sendEmail(request);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withSource("start@phoenixmild.com")
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withSubject(new Content(subject))
                        .withBody(new Body().withHtml(new Content(body)))
                );
        amazonSimpleEmailService.sendEmail(request);
        log.info("Email sent successfully to {}", to);
    }
}
