package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminder(String to,
                             String company,
                             String deadline) {

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(to);

            message.setSubject(
                    "⏰ Job application reminder"
            );

            message.setText(
                    "Reminder!\n\n" +
                            "Company: " + company + "\n" +
                            "Deadline: " + deadline + "\n\n" +
                            "Good luck!"
            );

            mailSender.send(message);

            logger.info(
                    "Reminder email sent to {} for company {}",
                    to,
                    company
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to send reminder email to {}",
                    to,
                    e
            );

            throw new EmailSendingException(
                    "Failed to send reminder email"
            );
        }
    }
}
