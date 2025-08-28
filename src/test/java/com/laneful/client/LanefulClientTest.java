package com.laneful.client;

import com.laneful.exceptions.ValidationException;
import com.laneful.models.Email;
import com.laneful.models.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class LanefulClientTest {
    
    private LanefulClient client;
    
    @BeforeEach
    void setUp() throws ValidationException {
        client = new LanefulClient(
            "https://test.send.laneful.net",
            "test-auth-token"
        );
    }
    
    @Test
    void testClientCreationWithValidParameters() {
        assertNotNull(client);
    }
    
    @Test
    void testClientCreationWithEmptyBaseUrl() {
        assertThrows(ValidationException.class, () -> {
            new LanefulClient("", "test-token");
        });
    }
    
    @Test
    void testClientCreationWithEmptyAuthToken() {
        assertThrows(ValidationException.class, () -> {
            new LanefulClient("https://test.send.laneful.net", "");
        });
    }
    
    @Test
    void testClientCreationWithNullBaseUrl() {
        assertThrows(ValidationException.class, () -> {
            new LanefulClient(null, "test-token");
        });
    }
    
    @Test
    void testClientCreationWithNullAuthToken() {
        assertThrows(ValidationException.class, () -> {
            new LanefulClient("https://test.send.laneful.net", null);
        });
    }
    
    @Test
    void testSendEmailWithValidEmail() throws ValidationException {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .subject("Test Email")
            .textContent("This is a test email.")
            .build();
        
        // Note: This would require a mock HTTP client for actual testing
        // For now, we just verify the email is valid
        assertNotNull(email);
        assertEquals("sender@example.com", email.getFrom().email());
        assertEquals(1, email.getTo().size());
        assertEquals("recipient@example.com", email.getTo().get(0).email());
    }
}
