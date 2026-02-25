package com.example.controller;

import com.example.entity.Complaint;
import com.example.entity.Department;
import com.example.entity.User;
import com.example.security.CustomUserDetails;
import com.example.service.ComplaintService;
import com.example.service.NotificationService;
import com.example.service.UserService;
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
@RequestMapping("/citizen")
public class CitizenController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping("/dashboard")
    public String citizenDashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        User originalCitizen = userDetails.getUser();
        User citizen = userService.findById(originalCitizen.getId());
        Page<Complaint> complaints = complaintService.getComplaintsByCitizen(citizen, page, 5);

        model.addAttribute("citizen", citizen);
        model.addAttribute("complaints", complaints);
        model.addAttribute("notifications", notificationService.getNotificationsForUser(citizen));
        return "citizen_dashboard";
    }

    @GetMapping("/complaint/new")
    public String newComplaintForm(Model model) {
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("departments", Department.values());
        return "citizen_new_complaint";
    }

    @PostMapping("/complaint/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint,
            @RequestParam("imageFile") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            complaint.setImagePath("/uploads/" + fileName);
        }

        complaintService.saveComplaint(complaint, userDetails.getUser());
        return "redirect:/citizen/dashboard?success";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name, @RequestParam String email,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateUserProfile(userDetails.getUser().getId(), name, email);
        return "redirect:/citizen/dashboard?profileSuccess";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean success = userService.updatePassword(userDetails.getUser().getId(), oldPassword, newPassword);
        if (success) {
            return "redirect:/citizen/dashboard?passwordSuccess";
        } else {
            return "redirect:/citizen/dashboard?passwordError";
        }
    }

    @GetMapping("/complaint/{id}")
    public String viewComplaint(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        if (!complaint.getCitizen().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/citizen/dashboard";
        }
        model.addAttribute("complaint", complaint);
        return "citizen_view_complaint";
    }

    @PostMapping("/complaint/{id}/withdraw")
    public String withdrawComplaint(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean success = complaintService.withdrawComplaint(id, userDetails.getUser());
        if (success) {
            return "redirect:/citizen/dashboard?withdrawSuccess";
        }
        return "redirect:/citizen/dashboard?withdrawError";
    }
}
