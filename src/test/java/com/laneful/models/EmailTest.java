package com.laneful.models;

import com.laneful.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.List;

class EmailTest {
    
    @Test
    void testEmailBuilderWithRequiredFields() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .subject("Test Subject")
            .textContent("Test content")
            .build();
        
        assertNotNull(email);
        assertEquals("sender@example.com", email.getFrom().email());
        assertEquals(1, email.getTo().size());
        assertEquals("recipient@example.com", email.getTo().get(0).email());
        assertEquals("Test Subject", email.getSubject());
        assertEquals("Test content", email.getTextContent());
    }
    
    @Test
    void testEmailBuilderWithHtmlContent() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .htmlContent("<h1>Test</h1>")
            .build();
        
        assertNotNull(email);
        assertEquals("<h1>Test</h1>", email.getHtmlContent());
    }
    
    @Test
    void testEmailBuilderWithTemplate() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .templateId("test-template")
            .templateData(Map.of("name", "John"))
            .build();
        
        assertNotNull(email);
        assertEquals("test-template", email.getTemplateId());
        assertEquals("John", email.getTemplateData().get("name"));
    }
    
    @Test
    void testEmailBuilderWithMultipleRecipients() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("user1@example.com"))
            .to(new Address("user2@example.com", "User Two"))
            .cc(new Address("cc@example.com"))
            .bcc(new Address("bcc@example.com"))
            .textContent("Test content")
            .build();
        
        assertNotNull(email);
        assertEquals(2, email.getTo().size());
        assertEquals(1, email.getCc().size());
        assertEquals(1, email.getBcc().size());
        assertEquals("User Two", email.getTo().get(1).name());
    }
    
    @Test
    void testEmailBuilderWithTracking() throws ValidationException {
        TrackingSettings tracking = new TrackingSettings(true, true, false);
        
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .textContent("Test content")
            .tracking(tracking)
            .build();
        
        assertNotNull(email);
        assertNotNull(email.getTracking());
        assertTrue(email.getTracking().opens());
        assertTrue(email.getTracking().clicks());
        assertFalse(email.getTracking().unsubscribes());
    }
    
    @Test
    void testEmailBuilderWithoutFromAddress() {
        assertThrows(ValidationException.class, () -> {
            new Email.Builder()
                .to(new Address("recipient@example.com"))
                .textContent("Test content")
                .build();
        });
    }
    
    @Test
    void testEmailBuilderWithoutRecipients() {
        assertThrows(ValidationException.class, () -> {
            new Email.Builder()
                .from(new Address("sender@example.com"))
                .textContent("Test content")
                .build();
        });
    }
    
    @Test
    void testEmailBuilderWithoutContentOrTemplate() {
        assertThrows(ValidationException.class, () -> {
            new Email.Builder()
                .from(new Address("sender@example.com"))
                .to(new Address("recipient@example.com"))
                .build();
        });
    }
    
    @Test
    void testEmailBuilderWithPastSendTime() {
        assertThrows(ValidationException.class, () -> {
            new Email.Builder()
                .from(new Address("sender@example.com"))
                .to(new Address("recipient@example.com"))
                .textContent("Test content")
                .sendTime(System.currentTimeMillis() / 1000 - 3600) // 1 hour ago
                .build();
        });
    }
    
    @Test
    void testEmailEquality() throws ValidationException {
        Email email1 = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .subject("Test")
            .textContent("Content")
            .build();
        
        Email email2 = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .subject("Test")
            .textContent("Content")
            .build();
        
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    void testEmailToString() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com", "Sender"))
            .to(new Address("recipient@example.com", "Recipient"))
            .subject("Test Subject")
            .textContent("Test content")
            .build();
        
        String toString = email.toString();
        assertTrue(toString.contains("sender@example.com"));
        assertTrue(toString.contains("recipient@example.com"));
        assertTrue(toString.contains("Test Subject"));
    }
}
