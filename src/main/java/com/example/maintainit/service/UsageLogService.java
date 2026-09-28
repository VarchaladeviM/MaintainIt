package com.example.maintainit.service;

import com.example.maintainit.entity.Machine;
import com.example.maintainit.entity.UsageLog;
import com.example.maintainit.repository.MachineRepository;
import com.example.maintainit.repository.UsageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageLogService {

    private final UsageLogRepository usageLogRepository;
    private final MachineRepository machineRepository;

    public UsageLog createUsageLog(Long machineId, UsageLog usageLog) {

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Machine not found with id: " + machineId));

        usageLog.setMachine(machine);

        if (usageLog.getLoggedAt() == null) {
            usageLog.setLoggedAt(LocalDateTime.now());
        }

        double currentUsage = machine.getCurrentUsageHours() == null
                ? 0.0
                : machine.getCurrentUsageHours();

        double newUsage = usageLog.getUsageHours() == null
                ? 0.0
                : usageLog.getUsageHours();

        machine.setCurrentUsageHours(currentUsage + newUsage);

        machineRepository.save(machine);

        return usageLogRepository.save(usageLog);
    }

    public List<UsageLog> getAllUsageLogs() {
        return usageLogRepository.findAll();
    }

    public UsageLog getUsageLogById(Long id) {
        return usageLogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usage log not found with id: " + id));
    }

    public UsageLog updateUsageLog(
            Long id,
            Long machineId,
            UsageLog updatedUsageLog) {

        UsageLog existingLog = getUsageLogById(id);

        Machine oldMachine = existingLog.getMachine();

        Machine newMachine = machineRepository.findById(machineId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Machine not found with id: " + machineId));

        double oldUsage = existingLog.getUsageHours();
        double newUsage = updatedUsageLog.getUsageHours();

        if (oldMachine.getId().equals(newMachine.getId())) {

            double currentUsage =
                    oldMachine.getCurrentUsageHours() == null
                            ? 0.0
                            : oldMachine.getCurrentUsageHours();

            oldMachine.setCurrentUsageHours(
                    currentUsage - oldUsage + newUsage
            );

            machineRepository.save(oldMachine);

        } else {

            double oldMachineUsage =
                    oldMachine.getCurrentUsageHours() == null
                            ? 0.0
                            : oldMachine.getCurrentUsageHours();

            double newMachineUsage =
                    newMachine.getCurrentUsageHours() == null
                            ? 0.0
                            : newMachine.getCurrentUsageHours();

            oldMachine.setCurrentUsageHours(
                    Math.max(0.0, oldMachineUsage - oldUsage)
            );

            newMachine.setCurrentUsageHours(
                    newMachineUsage + newUsage
            );

            machineRepository.save(oldMachine);
            machineRepository.save(newMachine);

            existingLog.setMachine(newMachine);
        }

        existingLog.setUsageHours(newUsage);

        return usageLogRepository.save(existingLog);
    }

    public void deleteUsageLog(Long id) {

        UsageLog usageLog = getUsageLogById(id);

        usageLogRepository.delete(usageLog);
    }
}