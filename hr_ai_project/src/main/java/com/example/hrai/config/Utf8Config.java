package com.example.hrai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class Utf8Config implements WebMvcConfigurer {

    @Bean
    public ObjectMapper utf8ObjectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
            .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
            .map(converter -> (MappingJackson2HttpMessageConverter) converter)
            .findFirst()
            .ifPresent(converter -> {
                converter.setDefaultCharset(StandardCharsets.UTF_8);
            });
    }
}