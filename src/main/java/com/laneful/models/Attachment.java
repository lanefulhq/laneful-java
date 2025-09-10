package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

/**
 * Represents a file attachment for an email.
 */
public record Attachment(
    @JsonProperty("file_name") String filename,
    @JsonProperty("content_type") String contentType,
    @JsonProperty("content") String content
) {
    
    /**
     * Creates an attachment from a file.
     * 
     * @param file The file to attach
     * @throws IOException if the file cannot be read
     */
    public static Attachment fromFile(File file) throws IOException {
        return fromFile(file.toPath());
    }
    
    /**
     * Creates an attachment from a file path.
     * 
     * @param path The path to the file
     * @throws IOException if the file cannot be read
     */
    public static Attachment fromFile(Path path) throws IOException {
        String filename = path.getFileName().toString();
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        byte[] bytes = Files.readAllBytes(path);
        String content = Base64.getEncoder().encodeToString(bytes);
        
        return new Attachment(filename, contentType, content);
    }
    
    /**
     * Creates an attachment from raw data.
     * 
     * @param filename The filename
     * @param contentType The MIME type
     * @param content Base64-encoded content
     */
    public Attachment {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Content type cannot be empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
    
    /**
     * Creates an attachment from a map representation.
     * 
     * @param data Map containing attachment data
     * @return New Attachment instance
     * @throws IllegalArgumentException if the data is invalid
     */
    public static Attachment fromMap(java.util.Map<String, Object> data) throws IllegalArgumentException {
        String filename = (String) data.get("filename");
        String contentType = (String) data.get("content_type");
        String content = (String) data.get("content");
        return new Attachment(filename, contentType, content);
    }
    
    @Override
    public String toString() {
        return """
            Attachment{
                filename='%s',
                contentType='%s',
                contentLength=%d
            }""".formatted(
                filename,
                contentType,
                content != null ? content.length() : 0
            );
    }
}
