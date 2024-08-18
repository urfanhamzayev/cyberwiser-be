package com.phoenix_sat.phoenix_sat_backend.gateway;

public interface EmailSenderGateway {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String body);
}
