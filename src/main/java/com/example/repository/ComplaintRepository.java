package com.example.repository;

import com.example.entity.Complaint;
import com.example.entity.Department;
import com.example.entity.ComplaintStatus;
import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Page<Complaint> findByCitizen(User citizen, Pageable pageable);

    Page<Complaint> findByCitizenAndStatus(User citizen, ComplaintStatus status, Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.citizen = :citizen AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Complaint> searchByCitizenAndKeyword(@Param("citizen") User citizen, @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.citizen = :citizen AND c.status = :status AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Complaint> searchByCitizenAndStatusAndKeyword(@Param("citizen") User citizen,
            @Param("status") ComplaintStatus status, @Param("keyword") String keyword, Pageable pageable);

    Page<Complaint> findByStaff(User staff, Pageable pageable);

    // For admin to monitor
    List<Complaint> findByDepartment(Department department);

    List<Complaint> findByStatus(ComplaintStatus status);

    long countByStatus(ComplaintStatus status);
}
