package com.example.ecommerce.service.otp_email;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    JavaMailSender mailSender;

    EmailService emailService;
    MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService = new EmailService(mailSender);
    }


    @Test
    void testSendEmail_Success() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail("test@example.com", "Subject", "Body");
        });

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_SendThrowsMessagingException() throws MessagingException {
        MessagingException messagingException = new MessagingException("Simulated failure");
        doThrow(new RuntimeException("Failed to send email.", messagingException))
                .when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("test@example.com", "Subject", "Body");
        });

        assertInstanceOf(MessagingException.class, exception.getCause());
    }

    @Test
    void testSendEmail_NullTo_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(null, "Subject", "Body");
        });
    }

    @Test
    void testSendEmail_EmptySubject_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail("test@example.com", "", "");
        });
    }

    @Test
    void testSendEmail_NullSubjectAndBody_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail("test@example.com", null, null);
        });
    }

}