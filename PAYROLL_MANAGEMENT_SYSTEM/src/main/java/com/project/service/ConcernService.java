package com.project.service;

import com.project.entity.Concern;
import java.util.List;

public interface ConcernService {
    Concern createConcern(Concern concern);
    List<Concern> getConcernsByEmployee(Long employeeId);
    List<Concern> getAllConcerns();
    Concern updateConcernStatus(Long id, String status);
    void deleteConcern(Long id);
}
