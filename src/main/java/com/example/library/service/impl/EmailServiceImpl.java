package com.example.library.service.impl;

import com.example.library.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmails(String msg, List<String> emailsList) {

    }
}
