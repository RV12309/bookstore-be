package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.enumration.MailTemplate;
import com.hust.bookstore.serrvice.NotificationService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class EmailServiceImpl implements NotificationService {
    private JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${email.from}")
    private String mailFrom;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(MailTemplate template, Context context, String to) {

        try {

            String body = templateEngine.process(template.getCode(), context);

            MimeMessage message = mailSender.createMimeMessage();
            message.setContent(body, "text/html; charset=utf-8");
            message.setFrom(mailFrom);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(template.getSubject());
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Send email error: {}", e.getMessage());
        }
    }
}
