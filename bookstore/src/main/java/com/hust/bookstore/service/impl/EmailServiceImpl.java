package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.notify.Notify;
import com.hust.bookstore.dto.notify.Request;
import com.hust.bookstore.enumration.MailTemplate;
import com.hust.bookstore.service.NotificationService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static com.hust.bookstore.common.Constants.TRACE_ID;

@Service
@Slf4j
public class EmailServiceImpl implements NotificationService {
    @Value("${kafka-topic}")
    private String kafkaQueueIn;
    private final KafkaTemplate<String, Notify<Request>> kafkaTemplate;
    private JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${email.from}")
    private String mailFrom;

    public EmailServiceImpl(@Qualifier("internalKafkaTemplate") KafkaTemplate<String, Notify<Request>> kafkaTemplate,
                            JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.kafkaTemplate = kafkaTemplate;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(MailTemplate template, Context context, String to) {

        try {

            log.info("Prepare send email to {}", to);
            String body = templateEngine.process(template.getCode(), context);
            Request req = Request.builder()
                    .sender(mailFrom)
                    .subject(template.getSubject())
                    .content(body)
                    .sender(mailFrom)
                    .receiver(to)
                    .requestId(MDC.get(TRACE_ID))
                    .build();

            Notify<Request> notify = Notify.<Request>builder()
                    .data(req)
                    .build();
            kafkaTemplate.send(kafkaQueueIn, notify);
            log.info("Send message to kafka queue: {} successfully", kafkaQueueIn);
        } catch (Exception e) {
            log.info("Send email error: {}", e.getMessage());
        }
    }
}
