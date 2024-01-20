package com.ecomerce.postmaster.listener;

import com.ecomerce.postmaster.common.Constant;
import com.ecomerce.postmaster.model.Notify;
import com.ecomerce.postmaster.model.Request;
import com.ecomerce.postmaster.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static com.ecomerce.postmaster.common.Constant.VALID_EMAIL_ADDRESS_REGEX;

@Slf4j
@Component
public class NotificationListener {

    private final NotifyService notifyService;

    public NotificationListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @KafkaListener(
            topics = "${kafka-topic}",
            containerFactory = "notifyListenerContainerFactory",
            groupId = "${kafka-consumer-group-id}",
            concurrency = "1"
    )
    public void handle(Notify<Request> notify) {
        Request request = notify.getData();
        if (invalidRequest(request)) {
            log.info("Missing value or invalid request for email");
            return;
        }
        MDC.put(Constant.TRACE_ID, StringUtils.isBlank(request.getRequestId()) ?
                String.valueOf(System.nanoTime()) : request.getRequestId());
        try {
            log.info("Received request to send email to {}", request.getReceiver());
            notifyService.sendNotification(notify);
        } catch (Exception ex) {
            log.error("Error when consumer notify", ex);
        } finally {
            MDC.clear();
        }
    }


    private boolean invalidRequest(Request request) {
        return request == null
                || StringUtils.isBlank(request.getSubject())
                || StringUtils.isBlank(request.getContent())
                || invalidEmail(request.getSender())
                || invalidEmail(request.getReceiver());
    }

    boolean invalidEmail(String email) {
        if (StringUtils.isBlank(email)) return true;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return !matcher.find();
    }
}
