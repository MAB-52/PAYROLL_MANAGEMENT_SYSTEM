package com.project.dto;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity.HeadersBuilder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConcernDTO {
    private Long id;
    private String subject;
    private String message;
    private String status;
    private String raisedByEmployeeName;
    private String raisedByEmployeeEmail;
    private String resolutionRemarks;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}

