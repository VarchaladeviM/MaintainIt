package com.example.maintainit.controller;

import com.example.maintainit.entity.Machine;
import com.example.maintainit.service.MachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PostMapping
    public ResponseEntity<Machine> createMachine(
            @Valid @RequestBody Machine machine) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(machineService.createMachine(machine));
    }

    @GetMapping
    public ResponseEntity<List<Machine>> getAllMachines() {

        return ResponseEntity.ok(machineService.getAllMachines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Machine> getMachineById(
            @PathVariable Long id) {

        return ResponseEntity.ok(machineService.getMachineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Machine> updateMachine(
            @PathVariable Long id,
            @Valid @RequestBody Machine machine) {

        return ResponseEntity.ok(
                machineService.updateMachine(id, machine)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(
            @PathVariable Long id) {

        machineService.deleteMachine(id);

        return ResponseEntity.noContent().build();
    }
}