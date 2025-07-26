package com.example.ecommerce.service.otp_email;

import com.example.ecommerce.validation.annotation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendEmail(@ValidEmail String to, @NotBlank @NotNull String subject, @NotBlank @NotNull String body) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Email subject cannot be empty.");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Email Body cannot be empty.");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email.", e);
        }
    }

}
