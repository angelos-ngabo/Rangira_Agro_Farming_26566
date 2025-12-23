package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {

@NotNull(message = "Receiver ID is required")
private Long receiverId;

private Long relatedInventoryId;

@NotBlank(message = "Subject is required")
private String subject;

@NotBlank(message = "Message content is required")
private String content;

private Long repliedToMessageId;
}

