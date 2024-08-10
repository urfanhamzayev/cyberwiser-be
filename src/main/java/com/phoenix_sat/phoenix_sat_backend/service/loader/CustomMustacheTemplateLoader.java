package com.phoenix_sat.phoenix_sat_backend.service.loader;

import com.samskivert.mustache.Mustache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;

@Component("customMustacheTemplateLoader")
@RequiredArgsConstructor
public class CustomMustacheTemplateLoader {

    private final Mustache.Compiler mustacheCompiler;

    public String loadTemplate(String templateName, Map<String, Object> data) {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("templates/" + templateName)))) {
            return mustacheCompiler.compile(reader).execute(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template", e);
        }
    }
}