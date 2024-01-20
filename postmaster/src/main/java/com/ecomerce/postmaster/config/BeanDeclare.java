package com.ecomerce.postmaster.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

@Configuration
public class BeanDeclare {

    @Bean
    @Qualifier("jsonMapper")
    @Primary
    public ObjectMapper jsonMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter(@Qualifier("jsonMapper") ObjectMapper jsonMapper) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(jsonMapper);
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
        return jsonConverter;
    }
}
