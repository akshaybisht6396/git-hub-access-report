package com.githubreporter.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.githubreporter.model.ReportModels.UserAccessReport;
import com.githubreporter.service.GitHubReportService;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubReportController {

    private final GitHubReportService reportService;

    // This "Injects" the service we wrote earlier into this controller
    public GitHubReportController(GitHubReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report/{orgName}")
    public List<UserAccessReport> getReport(@PathVariable String orgName) {
        return reportService.getOrganizationAccessReport(orgName);
    }
}