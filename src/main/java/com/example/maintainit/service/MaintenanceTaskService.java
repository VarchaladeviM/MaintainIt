package com.example.maintainit.service;

import com.example.maintainit.entity.Machine;
import com.example.maintainit.entity.MaintenanceTask;
import com.example.maintainit.entity.Technician;
import com.example.maintainit.repository.MachineRepository;
import com.example.maintainit.repository.MaintenanceTaskRepository;
import com.example.maintainit.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final MachineRepository machineRepository;
    private final TechnicianRepository technicianRepository;

    public MaintenanceTask createTask(
            Long machineId,
            Long technicianId,
            MaintenanceTask task) {

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Machine not found with id: " + machineId));

        if (technicianId != null) {
            Technician technician = technicianRepository.findById(technicianId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Technician not found with id: " + technicianId));

            task.setTechnician(technician);
        }

        task.setMachine(machine);

        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("OPEN");
        }

        if (task.getScheduledDate() == null) {
            task.setScheduledDate(LocalDateTime.now());
        }

        return maintenanceTaskRepository.save(task);
    }

    public List<MaintenanceTask> getAllTasks() {
        return maintenanceTaskRepository.findAll();
    }

    public MaintenanceTask getTaskById(Long id) {
        return maintenanceTaskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Maintenance task not found with id: " + id));
    }

    public MaintenanceTask updateTask(
            Long id,
            Long technicianId,
            MaintenanceTask updatedTask) {

        MaintenanceTask existingTask = getTaskById(id);

        existingTask.setTaskType(updatedTask.getTaskType());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setScheduledDate(updatedTask.getScheduledDate());
        existingTask.setDescription(updatedTask.getDescription());

        if (technicianId != null) {
            Technician technician = technicianRepository.findById(technicianId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Technician not found with id: " + technicianId));

            existingTask.setTechnician(technician);
        }

        if ("COMPLETED".equalsIgnoreCase(updatedTask.getStatus())) {
            existingTask.setCompletedDate(LocalDateTime.now());

            Machine machine = existingTask.getMachine();
            machine.setCurrentUsageHours(0.0);
            machine.setLastMaintenanceDate(LocalDateTime.now());

            machineRepository.save(machine);
        }

        return maintenanceTaskRepository.save(existingTask);
    }

    public MaintenanceTask completeTask(Long id) {

        MaintenanceTask task = getTaskById(id);

        task.setStatus("COMPLETED");
        task.setCompletedDate(LocalDateTime.now());

        Machine machine = task.getMachine();

        machine.setCurrentUsageHours(0.0);
        machine.setLastMaintenanceDate(LocalDateTime.now());

        machineRepository.save(machine);

        return maintenanceTaskRepository.save(task);
    }

    public void deleteTask(Long id) {
        MaintenanceTask task = getTaskById(id);
        maintenanceTaskRepository.delete(task);
    }

    @Scheduled(fixedRate = 3600000)
    public void checkPreventiveMaintenance() {

        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {

            if (!Boolean.TRUE.equals(machine.getActive())) {
                continue;
            }

            if (machine.getMaintenanceIntervalHours() == null) {
                continue;
            }

            double currentUsage = machine.getCurrentUsageHours() == null
                    ? 0.0
                    : machine.getCurrentUsageHours();

            double maintenanceInterval =
                    machine.getMaintenanceIntervalHours();

            if (currentUsage < maintenanceInterval) {
                continue;
            }

            List<MaintenanceTask> openTasks =
                    maintenanceTaskRepository.findByMachineAndStatus(
                            machine,
                            "OPEN"
                    );

            if (!openTasks.isEmpty()) {
                continue;
            }

            MaintenanceTask automaticTask = MaintenanceTask.builder()
                    .machine(machine)
                    .taskType("Preventive Maintenance")
                    .status("OPEN")
                    .scheduledDate(LocalDateTime.now())
                    .description(
                            "Automatic maintenance required. " +
                                    "Machine usage has reached " +
                                    maintenanceInterval +
                                    " hours."
                    )
                    .build();

            maintenanceTaskRepository.save(automaticTask);
        }
    }
}