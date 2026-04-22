package org.example.jobapplicationtracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminder(String to, String company, String deadline) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("⏰ Job Reminder");
        message.setText("Reminder: " + company + " deadline är " + deadline);

        mailSender.send(message);
    }
}
