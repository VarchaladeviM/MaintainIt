package com.example.maintainit.controller;

import com.example.maintainit.entity.Technician;
import com.example.maintainit.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    @PostMapping
    public ResponseEntity<Technician> createTechnician(
            @Valid @RequestBody Technician technician) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(technicianService.createTechnician(technician));
    }

    @GetMapping
    public ResponseEntity<List<Technician>> getAllTechnicians() {

        return ResponseEntity.ok(
                technicianService.getAllTechnicians()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Technician> getTechnicianById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                technicianService.getTechnicianById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Technician> updateTechnician(
            @PathVariable Long id,
            @Valid @RequestBody Technician technician) {

        return ResponseEntity.ok(
                technicianService.updateTechnician(id, technician)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(
            @PathVariable Long id) {

        technicianService.deleteTechnician(id);

        return ResponseEntity.noContent().build();
    }
}