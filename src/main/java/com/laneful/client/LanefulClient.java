package com.laneful.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.exceptions.ValidationException;
import com.laneful.models.CreateDomainRequest;
import com.laneful.models.Domain;
import com.laneful.models.Email;
import com.laneful.models.ListDomainSpamRatioRadarParams;
import com.laneful.models.ListDomainSpamRatioRadarResponse;
import com.laneful.models.ListDomainsParams;
import com.laneful.models.ListDomainsResponse;
import com.laneful.models.ListGooglePostmasterSpamReportsParams;
import com.laneful.models.ListGooglePostmasterSpamReportsResponse;
import com.laneful.models.ListSndsReportsParams;
import com.laneful.models.ListSndsReportsResponse;
import com.laneful.models.ListUnsubscribeGroupsParams;
import com.laneful.models.ListUnsubscribeGroupsResponse;
import com.laneful.models.MailSettings;
import com.laneful.models.SuccessResponse;
import com.laneful.models.UnsubscribeGroup;
import com.laneful.models.UnsubscribeGroupResponse;
import com.laneful.models.UpdateDomainRequest;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Main client for communicating with the Laneful API.
 * Email sending uses a send host (https://your-endpoint.send.laneful.net).
 * Domain, unsubscribe-group, and analytics endpoints use the organization
 * API host (https://api.laneful.net).
 */
public class LanefulClient {
    
    private static final String API_VERSION = "v1";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String USER_AGENT = "laneful-java/1.2.0";
    
    private final String baseUrl;
    private final String authToken;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new LanefulClient with the specified configuration.
     */
    public LanefulClient(String baseUrl, String authToken) throws ValidationException {
        this(baseUrl, authToken, DEFAULT_TIMEOUT);
    }
    
    /**
     * Creates a new LanefulClient with custom timeout.
     */
    public LanefulClient(String baseUrl, String authToken, Duration timeout) throws ValidationException {
        this(baseUrl, authToken, timeout, null);
    }
    
    /**
     * Creates a new LanefulClient with custom HTTP client.
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
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        
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
     */
    public Map<String, Object> sendEmail(Email email) throws ApiException, HttpException, ValidationException {
        return sendEmails(Arrays.asList(email), null);
    }

    /**
     * Sends a single email with request-level mail settings.
     */
    public Map<String, Object> sendEmail(Email email, MailSettings settings)
            throws ApiException, HttpException, ValidationException {
        return sendEmails(Arrays.asList(email), settings);
    }
    
    /**
     * Sends multiple emails.
     */
    public Map<String, Object> sendEmails(List<Email> emails) throws ApiException, HttpException, ValidationException {
        return sendEmails(emails, null);
    }

    /**
     * Sends multiple emails with request-level mail settings.
     */
    public Map<String, Object> sendEmails(List<Email> emails, MailSettings settings)
            throws ApiException, HttpException, ValidationException {
        if (emails == null || emails.isEmpty()) {
            throw new ValidationException("Emails list cannot be empty");
        }
        
        for (Email email : emails) {
            if (email == null) {
                throw new ValidationException("Email cannot be null");
            }
        }

        Map<String, Object> requestData = new LinkedHashMap<>();
        requestData.put("emails", emails);
        if (settings != null) {
            requestData.put("mail_settings", settings);
        }

        return request("POST", "/email/send", requestData, null);
    }

    /**
     * List unsubscribe groups for a workspace.
     * Uses the organization API host (https://api.laneful.net).
     */
    public ListUnsubscribeGroupsResponse listUnsubscribeGroups(long workspaceId, ListUnsubscribeGroupsParams params)
            throws ApiException, HttpException {
        return request(
            "GET",
            "/workspaces/" + workspaceId + "/unsubscribe-groups",
            null,
            params == null ? null : params.toQuery(),
            ListUnsubscribeGroupsResponse.class
        );
    }

    /**
     * Create an unsubscribe group in a workspace.
     * Uses the organization API host (https://api.laneful.net).
     */
    public UnsubscribeGroup createUnsubscribeGroup(long workspaceId, String name)
            throws ApiException, HttpException {
        UnsubscribeGroupResponse response = request(
            "POST",
            "/workspaces/" + workspaceId + "/unsubscribe-groups",
            Map.of("name", name),
            null,
            UnsubscribeGroupResponse.class
        );
        return response.unsubscribeGroup();
    }

    /**
     * Update an unsubscribe group.
     * Uses the organization API host (https://api.laneful.net).
     */
    public UnsubscribeGroup updateUnsubscribeGroup(long workspaceId, long unsubscribeGroupId, String name)
            throws ApiException, HttpException {
        UnsubscribeGroupResponse response = request(
            "PATCH",
            "/workspaces/" + workspaceId + "/unsubscribe-groups/" + unsubscribeGroupId,
            Map.of("name", name),
            null,
            UnsubscribeGroupResponse.class
        );
        return response.unsubscribeGroup();
    }

    /**
     * List sending domains for a workspace.
     * Uses the organization API host (https://api.laneful.net).
     */
    public ListDomainsResponse listDomains(long workspaceId, ListDomainsParams params)
            throws ApiException, HttpException {
        return request(
            "GET",
            "/workspaces/" + workspaceId + "/domains",
            null,
            params == null ? null : params.toQuery(),
            ListDomainsResponse.class
        );
    }

    /**
     * Get a single sending domain by name.
     * Uses the organization API host (https://api.laneful.net).
     */
    public Domain getDomain(long workspaceId, String domain) throws ApiException, HttpException {
        return request(
            "GET",
            "/workspaces/" + workspaceId + "/domains/" + encodePath(domain),
            null,
            null,
            Domain.class
        );
    }

    /**
     * Create a sending domain in a workspace.
     * Uses the organization API host (https://api.laneful.net).
     */
    public Domain createDomain(long workspaceId, CreateDomainRequest createRequest)
            throws ApiException, HttpException {
        return request("POST", "/workspaces/" + workspaceId + "/domains", createRequest, null, Domain.class);
    }

    /**
     * Update a domain's mutable settings (currently the email track).
     * Uses the organization API host (https://api.laneful.net).
     */
    public Domain updateDomain(long workspaceId, String domain, UpdateDomainRequest updateRequest)
            throws ApiException, HttpException {
        return request(
            "PATCH",
            "/workspaces/" + workspaceId + "/domains/" + encodePath(domain),
            updateRequest,
            null,
            Domain.class
        );
    }

    /**
     * Trigger DNS verification for a domain.
     * Uses the organization API host (https://api.laneful.net).
     */
    public Domain verifyDomain(long workspaceId, String domain) throws ApiException, HttpException {
        return request(
            "POST",
            "/workspaces/" + workspaceId + "/domains/" + encodePath(domain) + "/verify",
            null,
            null,
            Domain.class
        );
    }

    /**
     * Delete a sending domain from a workspace.
     * Uses the organization API host (https://api.laneful.net).
     */
    public SuccessResponse deleteDomain(long workspaceId, String domain) throws ApiException, HttpException {
        return request(
            "DELETE",
            "/workspaces/" + workspaceId + "/domains/" + encodePath(domain),
            null,
            null,
            SuccessResponse.class
        );
    }

    /**
     * List domains whose spam complaint ratio reached a critical level.
     * Uses the organization API host (https://api.laneful.net).
     */
    public ListDomainSpamRatioRadarResponse listDomainSpamRatioRadar(ListDomainSpamRatioRadarParams params)
            throws ApiException, HttpException {
        return request(
            "GET",
            "/analytics/radar/domain-spam-ratio",
            null,
            params == null ? null : params.toQuery(),
            ListDomainSpamRatioRadarResponse.class
        );
    }

    /**
     * List daily Google Postmaster Tools spam-rate reports.
     * Uses the organization API host (https://api.laneful.net).
     */
    public ListGooglePostmasterSpamReportsResponse listGooglePostmasterSpamReports(
            ListGooglePostmasterSpamReportsParams params
    ) throws ApiException, HttpException {
        return request(
            "GET",
            "/analytics/google-postmaster/spam-reports",
            null,
            params == null ? null : params.toQuery(),
            ListGooglePostmasterSpamReportsResponse.class
        );
    }

    /**
     * List daily Microsoft SNDS reports for the organization's sending IPs.
     * Uses the organization API host (https://api.laneful.net).
     */
    public ListSndsReportsResponse listSndsReports(ListSndsReportsParams params)
            throws ApiException, HttpException {
        return request(
            "GET",
            "/analytics/microsoft-snds/reports",
            null,
            params == null ? null : params.toQuery(),
            ListSndsReportsResponse.class
        );
    }

    private Map<String, Object> request(
            String method,
            String path,
            Object body,
            List<Map.Entry<String, String>> query
    ) throws ApiException, HttpException {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = request(method, path, body, query, Map.class);
        return data;
    }

    private <T> T request(
            String method,
            String path,
            Object body,
            List<Map.Entry<String, String>> query,
            Class<T> type
    ) throws ApiException, HttpException {
        try {
            String json = sendRequest(method, path, body, query);
            return objectMapper.readValue(json, type);
        } catch (ApiException | HttpException e) {
            throw e;
        } catch (IOException e) {
            throw new HttpException("Failed to decode JSON response: " + e.getMessage(), 200, e);
        }
    }

    private String sendRequest(
            String method,
            String path,
            Object body,
            List<Map.Entry<String, String>> query
    ) throws ApiException, HttpException {
        try {
            HttpUrl url = buildHttpUrl(path, query);
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .headers(getDefaultHeaders(body != null));

            if (body != null) {
                String jsonBody = objectMapper.writeValueAsString(body);
                // Leave RequestBody media type null so OkHttp keeps our Content-Type header.
                builder.method(method, RequestBody.create(jsonBody, (MediaType) null));
            } else if ("DELETE".equals(method)) {
                builder.delete();
            } else if ("POST".equals(method) || "PATCH".equals(method) || "PUT".equals(method)) {
                builder.method(method, RequestBody.create("", (MediaType) null));
            } else {
                builder.method(method, null);
            }

            try (Response response = httpClient.newCall(builder.build()).execute()) {
                return handleResponse(response);
            }
        } catch (ApiException | HttpException e) {
            throw e;
        } catch (IOException e) {
            throw new HttpException("HTTP request failed: " + e.getMessage(), 0, e);
        }
    }

    private HttpUrl buildHttpUrl(String path, List<Map.Entry<String, String>> query) throws HttpException {
        HttpUrl parsed = HttpUrl.parse(buildUrl(path));
        if (parsed == null) {
            throw new HttpException("Invalid URL: " + buildUrl(path), 0);
        }
        if (query == null || query.isEmpty()) {
            return parsed;
        }
        HttpUrl.Builder builder = parsed.newBuilder();
        for (Map.Entry<String, String> entry : query) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }
    
    private String buildUrl(String endpoint) {
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        return cleanBaseUrl + "/" + API_VERSION + cleanEndpoint;
    }

    private static String encodePath(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
    
    private Headers getDefaultHeaders(boolean withJson) {
        Headers.Builder builder = new Headers.Builder()
                .add("Authorization", "Bearer " + authToken)
                .add("Accept", "application/json")
                .add("User-Agent", USER_AGENT);
        if (withJson) {
            builder.add("Content-Type", "application/json");
        }
        return builder.build();
    }
    
    private String handleResponse(Response response) throws ApiException, HttpException {
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
        
        return switch (statusCode) {
            case 404 -> throw new HttpException(
                "API endpoint not found (404). Check your base URL. Requested: " + response.request().url(),
                statusCode
            );
            case 200, 201, 202 -> parseAndReturnData(body, response.request().url());
            default -> handleErrorResponse(body, statusCode, response.request().url());
        };
    }
    
    private String parseAndReturnData(String body, HttpUrl url) throws HttpException {
        try {
            objectMapper.readTree(body);
            return body;
        } catch (IOException e) {
            String truncatedBody = body.length() > 500 ? body.substring(0, 500) + "..." : body;
            throw new HttpException(
                "Failed to decode JSON response: " + e.getMessage() +
                ". Response body: " + truncatedBody + ". URL: " + url,
                0
            );
        }
    }
    
    private String handleErrorResponse(String body, int statusCode, HttpUrl url)
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
        
        String errorMessage = String.valueOf(data.getOrDefault("error", "Unknown API error"));
        String details = data.get("details") == null ? "" : String.valueOf(data.get("details"));
        String fullError = errorMessage + (details.isEmpty() ? "" : " - " + details);
        
        throw new ApiException(
            "API request failed to " + url,
            statusCode,
            fullError
        );
    }
}
