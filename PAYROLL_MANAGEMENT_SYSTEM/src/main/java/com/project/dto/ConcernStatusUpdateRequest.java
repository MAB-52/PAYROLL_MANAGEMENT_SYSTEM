package com.project.dto;

import com.project.entity.ConcernStatus;
import lombok.Data;

@Data
public class ConcernStatusUpdateRequest {
    private ConcernStatus status;       // Enum for status
    private String resolutionRemarks;   // Optional remarks
}
