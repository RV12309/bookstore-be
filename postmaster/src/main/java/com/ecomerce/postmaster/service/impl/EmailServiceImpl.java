package com.ecomerce.postmaster.service.impl;

import com.ecomerce.postmaster.common.Constant;
import com.ecomerce.postmaster.model.Notify;
import com.ecomerce.postmaster.model.Request;
import com.ecomerce.postmaster.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class EmailServiceImpl implements NotifyService {

    private final JavaMailSender sender;

    @Value("#{'${email.white-list}'.split(',')}")
    private List<String> whiteListEmail;

    @Value("${email.enable-white-list:#{null}}")
    private Boolean enableWhiteList;

    @Value("${spring.mail.from}")
    private String mailFrom;


    public EmailServiceImpl(JavaMailSender emailSender) {
        this.sender = emailSender;
    }


    @Override
    public void sendNotification(Notify<Request> notify) {
        Request request = notify.getData();
        if (isNull(enableWhiteList)) {
            log.info("Cannot send email to any one because Not exist Config enable white list email. " +
                    "Please add config 'email.enable-white-list' " +
                    "(TRUE: to send to email in white list - FALSE: to send every one)");
            return;
        }

        if (enableWhiteList.equals(FALSE) || whiteListEmail.contains(request.getReceiver())) {
            doSend(request);
        } else {
            log.info("Email {} is not in white list", request.getReceiver());
        }
    }

    private void doSend(Request request) {
        log.info("Send message to email: {}", request.getReceiver());
        try {
            sendMail(request, sender);
        } catch (MessagingException ex) {
            log.error("Error when send email: ", ex);
        }
    }

    private void sendMail(Request request, JavaMailSender sender) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, Constant.EMAIL_CHARSET);
        String[] receiverList = request.getReceiver().split(",");
        helper.setFrom(mailFrom);
        helper.setTo(receiverList);
        helper.setSubject(request.getSubject());
        helper.setText(request.getContent(), true);
        if (!CollectionUtils.isEmpty(request.getAttachment())) {
            for (Map.Entry<String, String> kv : request.getAttachment().entrySet()) {
                String base64data = kv.getValue();
                String fileName = kv.getKey();

                byte[] byteArrays = Base64.getDecoder().decode(base64data.getBytes(StandardCharsets.UTF_8));
                ByteArrayDataSource dataSource = new ByteArrayDataSource(byteArrays, getMimeType(fileName));
                helper.addAttachment(fileName, dataSource);
            }
        }

        sender.send(mimeMessage);
    }


    private String getMimeType(String fileName) {
        if (fileName.endsWith(".xml")) return "application/xml";
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".txt")) return "text/plain";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".rar")) return "application/vnd.rar";
        if (fileName.endsWith(".zip")) return "application/zip";
        if (fileName.endsWith(".xls")) return "application/vnd.ms-excel";
        if (fileName.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return "text/plain";
    }

}
