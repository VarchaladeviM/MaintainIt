package com.example.maintainit.controller;

import com.example.maintainit.entity.UsageLog;
import com.example.maintainit.service.UsageLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usage-logs")
@RequiredArgsConstructor
public class UsageLogController {

    private final UsageLogService usageLogService;

    @PostMapping("/machine/{machineId}")
    public ResponseEntity<UsageLog> createUsageLog(
            @PathVariable Long machineId,
            @Valid @RequestBody UsageLog usageLog) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        usageLogService.createUsageLog(
                                machineId,
                                usageLog
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<UsageLog>> getAllUsageLogs() {

        return ResponseEntity.ok(
                usageLogService.getAllUsageLogs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageLog> getUsageLogById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usageLogService.getUsageLogById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsageLog> updateUsageLog(
            @PathVariable Long id,
            @RequestParam Long machineId,
            @Valid @RequestBody UsageLog usageLog) {

        return ResponseEntity.ok(
                usageLogService.updateUsageLog(
                        id,
                        machineId,
                        usageLog
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsageLog(
            @PathVariable Long id) {

        usageLogService.deleteUsageLog(id);

        return ResponseEntity.noContent().build();
    }
}