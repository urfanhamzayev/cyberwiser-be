package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.model.request.HtmlEmailRequest;

import java.util.List;
import java.util.Map;

public interface EmailService {
    void htmlSend(HtmlEmailRequest HTMLRequest, Map<String, Object> model);

    void sendRegistrationCompleteEmail(List<? extends User> items);
}
