package com.portfoliowatch.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendReport(String to) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        String result = "";

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setText(result, true);
        helper.setSubject("Daily Watch Report");
        emailSender.send(message);
    }

}