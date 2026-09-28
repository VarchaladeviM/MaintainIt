package com.example.maintainit.repository;

import com.example.maintainit.entity.MaintenanceTask;
import com.example.maintainit.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTaskRepository
        extends JpaRepository<MaintenanceTask, Long> {

    List<MaintenanceTask> findByMachineAndStatus(
            Machine machine,
            String status
    );
}