package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.event.RegistrationVerificationEvent;
import com.phoenix_sat.phoenix_sat_backend.model.request.RegistrationCompletionHtmlEmailRequest;

import java.util.List;
import java.util.Map;

public interface EmailService {

    void sendRegistrationCompleteEmail(RegistrationVerificationEvent verificationEvent);
}
