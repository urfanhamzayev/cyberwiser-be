package com.phoenix_sat.phoenix_sat_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping
    public HttpStatus get(){
        return HttpStatus.OK;
    }
}
