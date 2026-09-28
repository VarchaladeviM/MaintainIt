package com.example.maintainit.service;

import com.example.maintainit.entity.Machine;
import com.example.maintainit.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;

    public Machine createMachine(Machine machine) {
        if (machine.getCurrentUsageHours() == null) {
            machine.setCurrentUsageHours(0.0);
        }

        if (machine.getActive() == null) {
            machine.setActive(true);
        }

        return machineRepository.save(machine);
    }

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public Machine getMachineById(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));
    }

    public Machine updateMachine(Long id, Machine updatedMachine) {
        Machine existingMachine = getMachineById(id);

        existingMachine.setName(updatedMachine.getName());
        existingMachine.setMaintenanceIntervalHours(
                updatedMachine.getMaintenanceIntervalHours()
        );
        existingMachine.setMaintenanceIntervalDays(
                updatedMachine.getMaintenanceIntervalDays()
        );
        existingMachine.setCurrentUsageHours(
                updatedMachine.getCurrentUsageHours()
        );
        existingMachine.setLastMaintenanceDate(
                updatedMachine.getLastMaintenanceDate()
        );
        existingMachine.setActive(updatedMachine.getActive());

        return machineRepository.save(existingMachine);
    }

    public void deleteMachine(Long id) {
        Machine machine = getMachineById(id);
        machineRepository.delete(machine);
    }
}