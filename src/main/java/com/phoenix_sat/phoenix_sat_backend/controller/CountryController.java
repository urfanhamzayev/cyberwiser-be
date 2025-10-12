package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.entity.Country;
import com.phoenix_sat.phoenix_sat_backend.model.request.CountryResponse;
import com.phoenix_sat.phoenix_sat_backend.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public List<CountryResponse> getCountries(
            @RequestParam(defaultValue = "en") String lang) {

        return countryService.getAllCountries().stream()
                .map(country -> mapToResponse(country, lang))
                .toList();
    }

    private CountryResponse mapToResponse(Country c, String lang) {
        String name;
        switch (lang.toLowerCase()) {
            case "az" -> name = c.getNameAz();
            case "ru" -> name = c.getNameRu();
            default -> name = c.getNameEn();
        }
        return new CountryResponse(c.getId(), c.getCode(), name);
    }
}
