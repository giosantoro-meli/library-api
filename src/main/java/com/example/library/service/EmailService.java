package com.example.library.service;

import java.util.List;

public interface EmailService {
    void sendEmails(String msg, List<String> emailsList);
}
