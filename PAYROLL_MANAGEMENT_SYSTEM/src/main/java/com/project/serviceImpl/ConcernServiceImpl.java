package com.project.serviceImpl;

import com.project.entity.Concern;
import com.project.entity.ConcernStatus;
import com.project.repo.ConcernRepo;
import com.project.service.ConcernService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcernServiceImpl implements ConcernService {

    private final ConcernRepo concernRepo;

    @Override
    public Concern createConcern(Concern concern) {
        concern.setStatus(ConcernStatus.IN_PROGRESS);  
        return concernRepo.save(concern);
    }

    @Override
    public List<Concern> getConcernsByEmployee(Long employeeId) {
        return concernRepo.findByRaisedByEmployeeId(employeeId);
    }

    @Override
    public List<Concern> getAllConcerns() {
        return concernRepo.findAll();
    }

    @Override
    public Concern updateConcernStatus(Long id, String status) {
        Concern existing = concernRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Concern not found with ID: " + id));
        existing.setStatus(ConcernStatus.valueOf(status.toUpperCase())); 
        return concernRepo.save(existing);
    }

    @Override
    public void deleteConcern(Long id) {
        concernRepo.deleteById(id);
    }
}
