package com.laneful.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.exceptions.ValidationException;
import com.laneful.models.Email;
import okhttp3.*;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Main client for communicating with the Laneful email API.
 */
public class LanefulClient {
    
    private static final String API_VERSION = "v1";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String USER_AGENT = "laneful-java/1.0.0";
    
    private final String baseUrl;
    private final String authToken;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new LanefulClient with the specified configuration.
     * 
     * @param baseUrl The base URL of the Laneful API
     * @param authToken The authentication token
     * @throws ValidationException When input validation fails
     */
    public LanefulClient(String baseUrl, String authToken) throws ValidationException {
        this(baseUrl, authToken, DEFAULT_TIMEOUT);
    }
    
    /**
     * Creates a new LanefulClient with custom timeout.
     * 
     * @param baseUrl The base URL of the Laneful API
     * @param authToken The authentication token
     * @param timeout The request timeout
     * @throws ValidationException When input validation fails
     */
    public LanefulClient(String baseUrl, String authToken, Duration timeout) throws ValidationException {
        this(baseUrl, authToken, timeout, null);
    }
    
    /**
     * Creates a new LanefulClient with custom HTTP client.
     * 
     * @param baseUrl The base URL of the Laneful API
     * @param authToken The authentication token
     * @param timeout The request timeout
     * @param httpClient Custom HTTP client (optional)
     * @throws ValidationException When input validation fails
     */
    public LanefulClient(String baseUrl, String authToken, Duration timeout, OkHttpClient httpClient) throws ValidationException {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new ValidationException("Base URL cannot be empty");
        }
        if (authToken == null || authToken.trim().isEmpty()) {
            throw new ValidationException("Auth token cannot be empty");
        }
        
        this.baseUrl = baseUrl.trim();
        this.authToken = authToken.trim();
        
        // Initialize ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        // Initialize HTTP client using pattern matching
        this.httpClient = switch (httpClient) {
            case null -> new OkHttpClient.Builder()
                    .connectTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .build();
            case OkHttpClient client -> client;
        };
    }
    
    /**
     * Sends a single email.
     * 
     * @param email The email to send
     * @return API response data
     * @throws ApiException When the API returns an error
     * @throws HttpException When HTTP communication fails
     * @throws ValidationException When input validation fails
     */
    public Map<String, Object> sendEmail(Email email) throws ApiException, HttpException, ValidationException {
        return sendEmails(List.of(email));
    }
    
    /**
     * Sends multiple emails.
     * 
     * @param emails List of emails to send
     * @return API response data
     * @throws ApiException When the API returns an error
     * @throws HttpException When HTTP communication fails
     * @throws ValidationException When input validation fails
     */
    public Map<String, Object> sendEmails(List<Email> emails) throws ApiException, HttpException, ValidationException {
        if (emails == null || emails.isEmpty()) {
            throw new ValidationException("Emails list cannot be empty");
        }
        
        // Validate all emails are Email instances
        for (Email email : emails) {
            if (email == null) {
                throw new ValidationException("Email cannot be null");
            }
        }
        
        try {
            // Prepare request data
            Map<String, Object> requestData = Map.of("emails", emails);
            String jsonBody = objectMapper.writeValueAsString(requestData);
            
            // Build request
            Request request = new Request.Builder()
                    .url(buildUrl("/email/send"))
                    .post(RequestBody.create(jsonBody, null))
                    .headers(getDefaultHeaders())
                    .build();
            
            // Execute request
            try (Response response = httpClient.newCall(request).execute()) {
                return handleResponse(response);
            }
            
        } catch (IOException e) {
            throw new HttpException("HTTP request failed: " + e.getMessage(), 0, e);
        }
    }
    
    /**
     * Builds the full API URL for an endpoint.
     * 
     * @param endpoint The API endpoint
     * @return Full URL
     */
    private String buildUrl(String endpoint) {
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        return cleanBaseUrl + "/" + API_VERSION + cleanEndpoint;
    }
    
    /**
     * Gets the default headers for API requests.
     * 
     * @return Headers map
     */
    private Headers getDefaultHeaders() {
        return new Headers.Builder()
                .add("Authorization", "Bearer " + authToken)
                .add("content-type", "application/json")
                .add("Accept", "application/json")
                .add("User-Agent", USER_AGENT)
                .build();
    }
    
    /**
     * Handles the HTTP response and converts it to a map.
     * 
     * @param response The HTTP response
     * @return Response data as map
     * @throws ApiException When the API returns an error
     * @throws HttpException When response parsing fails
     */
    private Map<String, Object> handleResponse(Response response) throws ApiException, HttpException {
        int statusCode = response.code();
        String body;
        
        try {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new HttpException("Empty response body", statusCode);
            }
            body = responseBody.string();
        } catch (IOException e) {
            throw new HttpException("Failed to read response body: " + e.getMessage(), statusCode, e);
        }
        
        // Handle specific status codes using pattern matching
        return switch (statusCode) {
            case 404 -> throw new HttpException(
                "API endpoint not found (404). Check your base URL. Requested: " + response.request().url(),
                statusCode
            );
            case 200, 201, 202 -> parseAndReturnData(body, response.request().url());
            default -> handleErrorResponse(body, statusCode, response.request().url());
        };
    }
    
    /**
     * Parses JSON response and returns data for successful responses.
     */
    private Map<String, Object> parseAndReturnData(String body, HttpUrl url) throws HttpException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(body, Map.class);
            return data;
        } catch (IOException e) {
            String truncatedBody = body.length() > 500 ? body.substring(0, 500) + "..." : body;
            throw new HttpException(
                "Failed to decode JSON response: " + e.getMessage() +
                ". Response body: " + truncatedBody + ". URL: " + url,
                0
            );
        }
    }
    
    /**
     * Handles error responses and throws appropriate exceptions.
     */
    private Map<String, Object> handleErrorResponse(String body, int statusCode, HttpUrl url) 
            throws ApiException, HttpException {
        Map<String, Object> data;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = objectMapper.readValue(body, Map.class);
            data = parsed;
        } catch (IOException e) {
            String truncatedBody = body.length() > 500 ? body.substring(0, 500) + "..." : body;
            throw new HttpException(
                "Failed to decode JSON response: " + e.getMessage() +
                ". Response body: " + truncatedBody + ". URL: " + url,
                statusCode
            );
        }
        
        // Enhanced error reporting
        String errorMessage = (String) data.getOrDefault("error", "Unknown API error");
        String details = (String) data.getOrDefault("details", "");
        String fullError = errorMessage + (details.isEmpty() ? "" : " - " + details);
        
        throw new ApiException(
            "API request failed to " + url,
            statusCode,
            fullError
        );
    }
}
