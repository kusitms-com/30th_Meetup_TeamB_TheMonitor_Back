package the_monitor.application.service;


import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendEmail(String to, String subject, String text) throws MessagingException, UnsupportedEncodingException;

}
