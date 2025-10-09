package com.project.repo;

import com.project.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcernRepo extends JpaRepository<Concern, Long> {
    List<Concern> findByRaisedByEmployeeId(Long employeeId);
    List<Concern> findByRaisedByOrganizationId(Long organizationId);
}
