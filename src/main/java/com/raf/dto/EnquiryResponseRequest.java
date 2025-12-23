package com.raf.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnquiryResponseRequest {
private Long enquiryId;

@NotNull(message = "Accept status is required")
private Boolean accept;

private String responseMessage;
}

