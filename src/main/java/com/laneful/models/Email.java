package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laneful.exceptions.ValidationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single email to be sent.
 */
public class Email {
    
    @JsonProperty("from")
    private final Address from;
    
    @JsonProperty("to")
    private final List<Address> to;
    
    @JsonProperty("cc")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final List<Address> cc;
    
    @JsonProperty("bcc")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final List<Address> bcc;
    
    @JsonProperty("subject")
    private final String subject;
    
    @JsonProperty("text_content")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final String textContent;
    
    @JsonProperty("html_content")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final String htmlContent;
    
    @JsonProperty("template_id")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final String templateId;
    
    @JsonProperty("template_data")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final Map<String, Object> templateData;
    
    @JsonProperty("attachments")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final List<Attachment> attachments;
    
    @JsonProperty("headers")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final Map<String, String> headers;
    
    @JsonProperty("reply_to")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final Address replyTo;
    
    @JsonProperty("send_time")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final Long sendTime;
    
    @JsonProperty("webhook_data")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final Map<String, String> webhookData;
    
    @JsonProperty("tag")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final String tag;
    
    @JsonProperty("tracking")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private final TrackingSettings tracking;
    
    private Email(Builder builder) throws ValidationException {
        this.from = builder.from;
        this.to = new ArrayList<>(builder.to);
        this.cc = new ArrayList<>(builder.cc);
        this.bcc = new ArrayList<>(builder.bcc);
        this.subject = builder.subject;
        this.textContent = builder.textContent;
        this.htmlContent = builder.htmlContent;
        this.templateId = builder.templateId;
        this.templateData = builder.templateData;
        this.attachments = new ArrayList<>(builder.attachments);
        this.headers = builder.headers;
        this.replyTo = builder.replyTo;
        this.sendTime = builder.sendTime;
        this.webhookData = builder.webhookData;
        this.tag = builder.tag;
        this.tracking = builder.tracking;
        
        validate();
    }
    
    /**
     * Creates an Email from a map representation.
     * 
     * @param data Map containing email data
     * @return New Email instance
     * @throws ValidationException if the data is invalid
     */
    public static Email fromMap(Map<String, Object> data) throws ValidationException {
        Builder builder = new Builder();
        
        if (data.containsKey("from")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fromData = (Map<String, Object>) data.get("from");
            try {
                builder.from(Address.fromMap(fromData));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid from address: " + e.getMessage(), e);
            }
        }
        
        if (data.containsKey("to")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> toList = (List<Map<String, Object>>) data.get("to");
            for (Map<String, Object> toData : toList) {
                try {
                    builder.to(Address.fromMap(toData));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid to address: " + e.getMessage(), e);
                }
            }
        }
        
        if (data.containsKey("cc")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ccList = (List<Map<String, Object>>) data.get("cc");
            for (Map<String, Object> ccData : ccList) {
                try {
                    builder.cc(Address.fromMap(ccData));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid cc address: " + e.getMessage(), e);
                }
            }
        }
        
        if (data.containsKey("bcc")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> bccList = (List<Map<String, Object>>) data.get("bcc");
            for (Map<String, Object> bccData : bccList) {
                try {
                    builder.bcc(Address.fromMap(bccData));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid bcc address: " + e.getMessage(), e);
                }
            }
        }
        
        if (data.containsKey("subject")) {
            builder.subject((String) data.get("subject"));
        }
        
        if (data.containsKey("text_content")) {
            builder.textContent((String) data.get("text_content"));
        }
        
        if (data.containsKey("html_content")) {
            builder.htmlContent((String) data.get("html_content"));
        }
        
        if (data.containsKey("template_id")) {
            builder.templateId((String) data.get("template_id"));
        }
        
        if (data.containsKey("template_data")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> templateData = (Map<String, Object>) data.get("template_data");
            builder.templateData(templateData);
        }
        
        if (data.containsKey("attachments")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) data.get("attachments");
            for (Map<String, Object> attachmentData : attachmentList) {
                try {
                    builder.attachment(Attachment.fromMap(attachmentData));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid attachment: " + e.getMessage(), e);
                }
            }
        }
        
        if (data.containsKey("headers")) {
            @SuppressWarnings("unchecked")
            Map<String, String> headers = (Map<String, String>) data.get("headers");
            builder.headers(headers);
        }
        
        if (data.containsKey("reply_to")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> replyToData = (Map<String, Object>) data.get("reply_to");
            try {
                builder.replyTo(Address.fromMap(replyToData));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid reply_to address: " + e.getMessage(), e);
            }
        }
        
        if (data.containsKey("send_time")) {
            builder.sendTime((Long) data.get("send_time"));
        }
        
        if (data.containsKey("webhook_data")) {
            @SuppressWarnings("unchecked")
            Map<String, String> webhookData = (Map<String, String>) data.get("webhook_data");
            builder.webhookData(webhookData);
        }
        
        if (data.containsKey("tag")) {
            builder.tag((String) data.get("tag"));
        }
        
        if (data.containsKey("tracking")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> trackingData = (Map<String, Object>) data.get("tracking");
            builder.tracking(TrackingSettings.fromMap(trackingData));
        }
        
        return builder.build();
    }
    
    private void validate() throws ValidationException {
        if (from == null) {
            throw new ValidationException("From address is required");
        }
        
        // Must have at least one recipient
        if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
            throw new ValidationException("Email must have at least one recipient (to, cc, or bcc)");
        }
        
        // Must have either content or template
        boolean hasContent = (textContent != null && !textContent.trim().isEmpty()) ||
                           (htmlContent != null && !htmlContent.trim().isEmpty());
        boolean hasTemplate = templateId != null && !templateId.trim().isEmpty();
        
        if (!hasContent && !hasTemplate) {
            throw new ValidationException("Email must have either content (text/HTML) or a template ID");
        }
        
        // Validate send time
        if (sendTime != null && sendTime <= Instant.now().getEpochSecond()) {
            throw new ValidationException("Send time must be in the future");
        }
    }
    
    // Getters
    public Address getFrom() { return from; }
    public List<Address> getTo() { return new ArrayList<>(to); }
    public List<Address> getCc() { return new ArrayList<>(cc); }
    public List<Address> getBcc() { return new ArrayList<>(bcc); }
    public String getSubject() { return subject; }
    public String getTextContent() { return textContent; }
    public String getHtmlContent() { return htmlContent; }
    public String getTemplateId() { return templateId; }
    public Map<String, Object> getTemplateData() { return templateData; }
    public List<Attachment> getAttachments() { return new ArrayList<>(attachments); }
    public Map<String, String> getHeaders() { return headers; }
    public Address getReplyTo() { return replyTo; }
    public Long getSendTime() { return sendTime; }
    public Map<String, String> getWebhookData() { return webhookData; }
    public String getTag() { return tag; }
    public TrackingSettings getTracking() { return tracking; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return Objects.equals(from, email.from) &&
               Objects.equals(to, email.to) &&
               Objects.equals(cc, email.cc) &&
               Objects.equals(bcc, email.bcc) &&
               Objects.equals(subject, email.subject) &&
               Objects.equals(textContent, email.textContent) &&
               Objects.equals(htmlContent, email.htmlContent) &&
               Objects.equals(templateId, email.templateId) &&
               Objects.equals(templateData, email.templateData) &&
               Objects.equals(attachments, email.attachments) &&
               Objects.equals(headers, email.headers) &&
               Objects.equals(replyTo, email.replyTo) &&
               Objects.equals(sendTime, email.sendTime) &&
               Objects.equals(webhookData, email.webhookData) &&
               Objects.equals(tag, email.tag) &&
               Objects.equals(tracking, email.tracking);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(from, to, cc, bcc, subject, textContent, htmlContent,
                          templateId, templateData, attachments, headers, replyTo,
                          sendTime, webhookData, tag, tracking);
    }
    
    @Override
    public String toString() {
        return """
            Email{
                from=%s,
                to=%s,
                subject='%s',
                hasTextContent=%s,
                hasHtmlContent=%s,
                templateId='%s',
                attachments=%d
            }""".formatted(
                from,
                to,
                subject,
                textContent != null && !textContent.isEmpty(),
                htmlContent != null && !htmlContent.isEmpty(),
                templateId,
                attachments.size()
            );
    }
    
    /**
     * Builder for creating Email instances.
     */
    public static class Builder {
        private Address from;
        private final List<Address> to = new ArrayList<>();
        private final List<Address> cc = new ArrayList<>();
        private final List<Address> bcc = new ArrayList<>();
        private String subject;
        private String textContent;
        private String htmlContent;
        private String templateId;
        private Map<String, Object> templateData;
        private final List<Attachment> attachments = new ArrayList<>();
        private Map<String, String> headers;
        private Address replyTo;
        private Long sendTime;
        private Map<String, String> webhookData;
        private String tag;
        private TrackingSettings tracking;
        
        public Builder from(Address from) {
            this.from = from;
            return this;
        }
        
        public Builder to(Address to) {
            this.to.add(to);
            return this;
        }
        
        public Builder to(String email) {
            return to(new Address(email));
        }
        
        public Builder to(String email, String name) {
            return to(new Address(email, name));
        }
        
        public Builder cc(Address cc) {
            this.cc.add(cc);
            return this;
        }
        
        public Builder cc(String email) {
            return cc(new Address(email));
        }
        
        public Builder cc(String email, String name) {
            return cc(new Address(email, name));
        }
        
        public Builder bcc(Address bcc) {
            this.bcc.add(bcc);
            return this;
        }
        
        public Builder bcc(String email) {
            return bcc(new Address(email));
        }
        
        public Builder bcc(String email, String name) {
            return bcc(new Address(email, name));
        }
        
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }
        
        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }
        
        public Builder htmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }
        
        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }
        
        public Builder templateData(Map<String, Object> templateData) {
            this.templateData = templateData;
            return this;
        }
        
        public Builder attachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }
        
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
        
        public Builder replyTo(Address replyTo) {
            this.replyTo = replyTo;
            return this;
        }
        
        public Builder replyTo(String email) {
            return replyTo(new Address(email));
        }
        
        public Builder replyTo(String email, String name) {
            return replyTo(new Address(email, name));
        }
        
        public Builder sendTime(Long sendTime) {
            this.sendTime = sendTime;
            return this;
        }
        
        public Builder webhookData(Map<String, String> webhookData) {
            this.webhookData = webhookData;
            return this;
        }
        
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }
        
        public Builder tracking(TrackingSettings tracking) {
            this.tracking = tracking;
            return this;
        }
        
        public Email build() throws ValidationException {
            return new Email(this);
        }
    }
}
