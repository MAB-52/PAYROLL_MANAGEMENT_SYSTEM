package com.project.service;

import com.project.dto.ConcernDTO;
import com.project.entity.Concern;
import com.project.entity.ConcernStatus;

import java.util.List;

public interface ConcernService {
    ConcernDTO createConcern(Concern concern);
    List<Concern> getConcernsByEmployee(Long employeeId);
    List<Concern> getConcernsByOrganization(Long organizationId);
    List<Concern> getAllConcerns();
    Concern updateConcernStatus(Long id, ConcernStatus status, String resolutionRemarks);
    void deleteConcern(Long id);
}
