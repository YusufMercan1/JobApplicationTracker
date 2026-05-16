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

    public void sendDeadlineReminder(String to, String company, String deadline) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Påminnelse: deadline för jobbansökan");
        message.setText(
                "Hej!\n\n"
                        + "Deadline för din ansökan hos " + company + " närmar sig.\n"
                        + "Deadline: " + deadline + "\n\n"
                        + "Logga in i Job Application Tracker för att uppdatera status.\n\n"
                        + "Lycka till!"
        );
        mailSender.send(message);
    }

    public void sendMeetingReminder(String to, String company, String dateTime, String location) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Påminnelse: intervjumöte");
        message.setText(
                "Hej!\n\n"
                        + "Du har ett intervjumöte kopplat till ansökan hos " + company + ".\n"
                        + "Tid: " + dateTime + "\n"
                        + "Plats: " + location + "\n\n"
                        + "Logga in i Job Application Tracker för detaljer.\n\n"
                        + "Lycka till!"
        );
        mailSender.send(message);
    }
}
