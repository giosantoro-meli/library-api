package com.example.library.service.impl;

import com.example.library.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmails(String msg, List<String> emailsList) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String[] emails = emailsList.toArray(new String[emailsList.size()]);

        mailMessage.setFrom(sender);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(msg);
        mailMessage.setTo(emails);

        javaMailSender.send(mailMessage);
    }
}
