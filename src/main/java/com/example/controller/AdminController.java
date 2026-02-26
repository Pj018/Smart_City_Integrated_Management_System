package com.example.controller;

import com.example.entity.Complaint;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.security.CustomUserDetails;
import com.example.service.ComplaintService;
import com.example.service.NotificationService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;
  
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        List<User> allUsers = userService.findAllUsers();
        List<User> staffMembers = allUsers.stream()
                .filter(u -> u.getRole() == Role.STAFF)
                .collect(Collectors.toList());

        model.addAttribute("admin", userDetails.getUser());
        model.addAttribute("complaints", allComplaints);
        model.addAttribute("staffMembers", staffMembers);
        model.addAttribute("users", allUsers);

        // stats
        long pending = allComplaints.stream().filter(c -> c.getStatus().name().equals("PENDING")).count();
        long resolved = allComplaints.stream().filter(c -> c.getStatus().name().equals("COMPLETED")).count();
        model.addAttribute("pendingCount", pending);
        model.addAttribute("resolvedCount", resolved);

        return "admin_dashboard";
    }

    @PostMapping("/complaint/assign")
    public String assignComplaint(@RequestParam Long complaintId, @RequestParam Long staffId) {
        complaintService.assignToStaff(complaintId, staffId);
        return "redirect:/admin/dashboard?assigned";
    }

    @PostMapping("/complaint/mark-fake")
    public String markComplaintFake(@RequestParam Long complaintId) {
        complaintService.markAsFake(complaintId);
        return "redirect:/admin/dashboard?markedFake";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam Long userId) {
        userService.toggleUserActiveStatus(userId);
        return "redirect:/admin/dashboard?statusUpdated";
    }

    @PostMapping("/broadcast")
    public String broadcastMessage(@RequestParam String message) {
        List<User> users = userService.findAllUsers();
        for (User user : users) {
            notificationService.sendNotification(user, "ADMIN BROADCAST: " + message);
        }
        return "redirect:/admin/dashboard?broadcastSent";
    }
}
