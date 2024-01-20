package com.hust.bookstore.service;

import com.hust.bookstore.enumration.MailTemplate;
import org.thymeleaf.context.Context;

public interface NotificationService {
    void send(MailTemplate template, Context context, String to);

}