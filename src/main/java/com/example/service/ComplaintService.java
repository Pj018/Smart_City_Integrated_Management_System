package com.example.service;

import com.example.entity.*;
import com.example.repository.ComplaintRepository;
import com.example.repository.PerformanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PerformanceReportRepository performanceReportRepository;

    public Complaint saveComplaint(Complaint complaint, User citizen) {
        complaint.setCitizen(citizen);
        complaint.setStatus(ComplaintStatus.PENDING);
        Complaint saved = complaintRepository.save(complaint);

        notificationService.sendNotification(citizen,
                "Your complaint '" + saved.getTitle() + "' has been submitted successfully!");
        return saved;
    }

    public Page<Complaint> getComplaintsByCitizen(User citizen, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return complaintRepository.findByCitizen(citizen, pageable);
    }

    public Page<Complaint> getComplaintsByStaff(User staff, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return complaintRepository.findByStaff(staff, pageable);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll(Sort.by("createdDate").descending());
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElseThrow();
    }

    public void assignToStaff(Long complaintId, Long staffId) {
        Complaint complaint = getComplaintById(complaintId);
        User staff = userService.findById(staffId);
        if (staff != null && staff.getRole() == Role.STAFF) {
            complaint.setStaff(staff);
            complaintRepository.save(complaint);

            notificationService.sendNotification(staff,
                    "A new complaint '" + complaint.getTitle() + "' has been assigned to you.");
            notificationService.sendNotification(complaint.getCitizen(),
                    "Your complaint has been assigned to a staff member.");
        }
    }

    public void updateComplaintStatus(Long complaintId, ComplaintStatus status, String workNotes, String imagePath) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(status);
        if (workNotes != null) {
            complaint.setWorkNotes(workNotes);
        }
        if (imagePath != null) {
            complaint.setResolutionImagePath(imagePath);
        }

        if (status == ComplaintStatus.COMPLETED) {
            complaint.setResolvedDate(LocalDateTime.now());
            // Update citizen points
            userService.updatePoints(complaint.getCitizen(), 10);
            notificationService.sendNotification(complaint.getCitizen(),
                    "Your complaint '" + complaint.getTitle() + "' has been resolved! You earned +10 points.");

            // Update staff performance
            updateStaffPerformance(complaint);
        } else if (status == ComplaintStatus.IN_PROGRESS) {
            notificationService.sendNotification(complaint.getCitizen(),
                    "Your complaint '" + complaint.getTitle() + "' is now in progress.");
        }

        complaintRepository.save(complaint);
    }

    public void markAsFake(Long complaintId) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setFakeFlag(true);
        complaint.setStatus(ComplaintStatus.COMPLETED);
        complaint.setResolvedDate(LocalDateTime.now());
        complaintRepository.save(complaint);

        userService.updatePoints(complaint.getCitizen(), -5);
        notificationService.sendNotification(complaint.getCitizen(),
                "Your complaint '" + complaint.getTitle() + "' was marked as fake. 5 points have been deducted.");
    }

    public boolean withdrawComplaint(Long complaintId, User citizen) {
        Complaint complaint = getComplaintById(complaintId);
        if (complaint.getCitizen().getId().equals(citizen.getId())
                && complaint.getStatus() == ComplaintStatus.PENDING) {
            complaint.setStatus(ComplaintStatus.WITHDRAWN);
            complaintRepository.save(complaint);
            notificationService.sendNotification(citizen,
                    "You have successfully withdrawn your complaint: '" + complaint.getTitle() + "'.");
            return true;
        }
        return false;
    }

    private void updateStaffPerformance(Complaint complaint) {
        if (complaint.getStaff() != null) {
            User staff = complaint.getStaff();
            PerformanceReport report = performanceReportRepository.findByStaff(staff)
                    .orElse(PerformanceReport.builder().staff(staff).build());

            report.setTotalTasks(report.getTotalTasks() + 1);

            // basic response time avg logic
            double hoursTaken = Duration.between(complaint.getCreatedDate(), complaint.getResolvedDate()).toMinutes()
                    / 60.0;
            double currentAvg = report.getAvgResponseTimeHours();
            int currentTasks = report.getTotalTasks() - 1; // before adding this one

            double newAvg = ((currentAvg * currentTasks) + hoursTaken) / (currentTasks + 1);
            report.setAvgResponseTimeHours(newAvg);

            performanceReportRepository.save(report);
        }
    }
}
