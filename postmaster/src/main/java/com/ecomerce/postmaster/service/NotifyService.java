package com.ecomerce.postmaster.service;


import com.ecomerce.postmaster.model.Notify;
import com.ecomerce.postmaster.model.Request;

import javax.mail.MessagingException;

public interface NotifyService {

    void sendNotification(Notify<Request> notify) throws MessagingException;
}
