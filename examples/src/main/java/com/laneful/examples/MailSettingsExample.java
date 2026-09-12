package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.exceptions.ValidationException;
import com.laneful.models.Address;
import com.laneful.models.Email;
import com.laneful.models.MailSettings;
import com.laneful.models.TrackingSettings;

import java.util.Map;

public class MailSettingsExample {
    public static void main(String[] args) {
        String baseUrl = System.getenv("LANEFUL_BASE_URL");
        String authToken = System.getenv("LANEFUL_AUTH_TOKEN");
        String fromEmail = System.getenv("LANEFUL_FROM_EMAIL");
        String toEmails = System.getenv("LANEFUL_TO_EMAILS");

        if (baseUrl == null || authToken == null || fromEmail == null || toEmails == null) {
            System.err.println("Missing LANEFUL_BASE_URL, LANEFUL_AUTH_TOKEN, LANEFUL_FROM_EMAIL, LANEFUL_TO_EMAILS");
            System.exit(1);
        }

        String toEmail = toEmails.split(",")[0].trim();

        try {
            LanefulClient client = new LanefulClient(baseUrl, authToken);

            Email email = new Email.Builder()
                .from(new Address(fromEmail, "Your Name"))
                .to(new Address(toEmail, "Recipient Name"))
                .subject("Sandbox email")
                .textContent("This email is sent with sandbox mode and returns message IDs.")
                .fromHeader(new Address(fromEmail, "Newsletter"))
                .tracking(new TrackingSettings(true, true, false, null, "Newsletters"))
                .build();

            Map<String, Object> response = client.sendEmail(
                email,
                new MailSettings(true, true)
            );
            System.out.println("Email sent successfully");
            System.out.println("Response: " + response);
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error: " + e.getMessage());
        } catch (HttpException e) {
            System.err.println("HTTP error: " + e.getMessage());
        }
    }
}
