package com.example.repository;

import com.example.entity.Complaint;
import com.example.entity.Department;
import com.example.entity.ComplaintStatus;
import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Page<Complaint> findByCitizen(User citizen, Pageable pageable);

    Page<Complaint> findByStaff(User staff, Pageable pageable);

    // For admin to monitor
    List<Complaint> findByDepartment(Department department);

    List<Complaint> findByStatus(ComplaintStatus status);

    long countByStatus(ComplaintStatus status);
}
