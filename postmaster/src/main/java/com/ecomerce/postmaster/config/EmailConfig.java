package com.ecomerce.postmaster.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private final EmailProperties emailProperties;

    @Bean
    @Qualifier(value = "defaultEmailSender")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("utf-8");
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailProperties.getTransportProtocol());
        props.put("mail.smtp.auth", emailProperties.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailProperties.isSmtpStarttlsEnable());
        props.put("mail.debug", emailProperties.isDebug());
        props.put("mail.mime.charset", "utf-8");

        return mailSender;
    }
}
