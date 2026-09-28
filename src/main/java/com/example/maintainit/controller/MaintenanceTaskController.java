package com.example.maintainit.controller;

import com.example.maintainit.entity.MaintenanceTask;
import com.example.maintainit.service.MaintenanceTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-tasks")
@RequiredArgsConstructor
public class MaintenanceTaskController {

    private final MaintenanceTaskService maintenanceTaskService;

    @PostMapping("/machine/{machineId}")
    public ResponseEntity<MaintenanceTask> createTask(
            @PathVariable Long machineId,
            @RequestParam(required = false) Long technicianId,
            @Valid @RequestBody MaintenanceTask task) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(maintenanceTaskService.createTask(
                        machineId,
                        technicianId,
                        task
                ));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceTask>> getAllTasks() {

        return ResponseEntity.ok(
                maintenanceTaskService.getAllTasks()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceTask> getTaskById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceTaskService.getTaskById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceTask> updateTask(
            @PathVariable Long id,
            @RequestParam(required = false) Long technicianId,
            @Valid @RequestBody MaintenanceTask task) {

        return ResponseEntity.ok(
                maintenanceTaskService.updateTask(
                        id,
                        technicianId,
                        task
                )
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceTask> completeTask(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceTaskService.completeTask(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        maintenanceTaskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}