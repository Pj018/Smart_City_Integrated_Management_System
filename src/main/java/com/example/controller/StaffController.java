package com.example.controller;

import com.example.entity.Complaint;
import com.example.entity.ComplaintStatus;
import com.example.entity.User;
import com.example.repository.PerformanceReportRepository;
import com.example.security.CustomUserDetails;
import com.example.service.ComplaintService;
import com.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PerformanceReportRepository performanceReportRepository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping("/dashboard")
    public String staffDashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        User staff = userDetails.getUser();
        Page<Complaint> assignedComplaints = complaintService.getComplaintsByStaff(staff, page, 5);

        model.addAttribute("staff", staff);
        model.addAttribute("complaints", assignedComplaints);
        model.addAttribute("notifications", notificationService.getNotificationsForUser(staff));
        model.addAttribute("performance", performanceReportRepository.findByStaff(staff).orElse(null));
        return "staff_dashboard";
    }

    @PostMapping("/complaint/update")
    public String updateComplaint(@RequestParam Long id,
            @RequestParam ComplaintStatus status,
            @RequestParam(required = false) String workNotes,
            @RequestParam(value = "resolutionImage", required = false) MultipartFile file) throws IOException {

        String imagePath = null;
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_resolution_"
                    + StringUtils.cleanPath(file.getOriginalFilename());
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            imagePath = "/uploads/" + fileName;
        }

        complaintService.updateComplaintStatus(id, status, workNotes, imagePath);
        return "redirect:/staff/dashboard?updated";
    }
}
