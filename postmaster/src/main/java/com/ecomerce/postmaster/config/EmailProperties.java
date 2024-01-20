package com.ecomerce.postmaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.properties-mail")
public class EmailProperties {
    private String transportProtocol;
    private boolean smtpAuth;
    private boolean smtpStarttlsEnable;
    private boolean debug;
}
