package com.example.maintainit.service;

import com.example.maintainit.entity.Technician;
import com.example.maintainit.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public Technician createTechnician(Technician technician) {
        if (technician.getActive() == null) {
            technician.setActive(true);
        }

        return technicianRepository.save(technician);
    }

    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    public Technician getTechnicianById(Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Technician not found with id: " + id));
    }

    public Technician updateTechnician(Long id, Technician updatedTechnician) {

        Technician existingTechnician = getTechnicianById(id);

        existingTechnician.setName(updatedTechnician.getName());
        existingTechnician.setEmail(updatedTechnician.getEmail());
        existingTechnician.setPhone(updatedTechnician.getPhone());
        existingTechnician.setSpecialization(updatedTechnician.getSpecialization());
        existingTechnician.setActive(updatedTechnician.getActive());

        return technicianRepository.save(existingTechnician);
    }

    public void deleteTechnician(Long id) {
        Technician technician = getTechnicianById(id);
        technicianRepository.delete(technician);
    }
}