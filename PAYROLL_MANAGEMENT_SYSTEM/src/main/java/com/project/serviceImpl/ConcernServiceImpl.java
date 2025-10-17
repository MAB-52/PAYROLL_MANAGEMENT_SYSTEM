package com.project.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.ConcernDTO;
import com.project.entity.Concern;
import com.project.entity.ConcernStatus;
import com.project.entity.Employee;
import com.project.repo.BankAdminRepo;
import com.project.repo.ConcernRepo;
import com.project.repo.EmployeeRepo;
import com.project.repo.OrganizationRepo;
import com.project.service.ConcernService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcernServiceImpl implements ConcernService {

    private final ConcernRepo concernRepo;
    private final EmployeeRepo employeeRepo;
    private final OrganizationRepo organizationRepo;
    private final BankAdminRepo bankAdminRepo;

    @Override
    public ConcernDTO createConcern(Concern concern) {
        if (concern.getRaisedByEmployee() != null && concern.getRaisedByEmployee().getId() != null) {
            Employee emp = employeeRepo.findById(concern.getRaisedByEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            concern.setRaisedByEmployee(emp);
        }

        concern.setStatus(ConcernStatus.PENDING);
        concern.setCreatedAt(LocalDateTime.now());

        Concern saved = concernRepo.save(concern);

        return ConcernDTO.builder()
                .id(saved.getId())
                .subject(saved.getSubject())
                .message(saved.getMessage())
                .status(saved.getStatus().name())
                .raisedByEmployeeName(saved.getRaisedByEmployee() != null ? saved.getRaisedByEmployee().getFullName() : null)
                .raisedByEmployeeEmail(saved.getRaisedByEmployee() != null ? saved.getRaisedByEmployee().getEmail() : null)
                .createdAt(saved.getCreatedAt())
                .build();
    }


    @Override
    public List<Concern> getConcernsByEmployee(Long employeeId) {
        return concernRepo.findByRaisedByEmployeeId(employeeId);
    }

    @Override
    public List<Concern> getConcernsByOrganization(Long organizationId) {
        return concernRepo.findByRaisedByOrganizationId(organizationId);
    }

    @Override
    public List<Concern> getAllConcerns() {
        return concernRepo.findAll();
    }

    public Concern updateConcernStatus(Long id, ConcernStatus status, String resolutionRemarks) {
        Concern concern = concernRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Concern not found"));

        concern.setStatus(status);

        if (status == ConcernStatus.RESOLVED) {
            concern.setResolvedAt(LocalDateTime.now());
            concern.setResolutionRemarks(resolutionRemarks);
        }

        return concernRepo.save(concern);
    }

    @Override
    public void deleteConcern(Long id) {
        concernRepo.deleteById(id);
    }
    
    
}
