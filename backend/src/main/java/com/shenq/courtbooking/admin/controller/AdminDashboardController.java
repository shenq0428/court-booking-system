package com.shenq.courtbooking.admin.controller;

import com.shenq.courtbooking.admin.dto.AdminDashboardResponse;
import com.shenq.courtbooking.admin.service.AdminDashboardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService
    ) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping
    public AdminDashboardResponse getDashboard() {
        return adminDashboardService.getDashboard();
    }
    
}