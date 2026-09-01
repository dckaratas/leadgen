package com.senin.leadgen.controller;

import com.senin.leadgen.orchestrator.LeadPipelineOrchestrator;
import com.senin.leadgen.web.ScanRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leads")
public class LeadPipelineController {

    private final LeadPipelineOrchestrator orchestrator;

    public LeadPipelineController(LeadPipelineOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/scan")
    public ResponseEntity<Void> scan(@Valid @RequestBody ScanRequest request) {
        orchestrator.runFor(request.latitude(), request.longitude(), request.initialRadiusMeters());
        return ResponseEntity.accepted().build();
    }
}