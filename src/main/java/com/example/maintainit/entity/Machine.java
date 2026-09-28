package com.example.maintainit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Machine name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Maintenance interval hours is required")
    @PositiveOrZero(message = "Maintenance interval hours cannot be negative")
    @Column(nullable = false)
    private Double maintenanceIntervalHours;

    @NotNull(message = "Maintenance interval days is required")
    @PositiveOrZero(message = "Maintenance interval days cannot be negative")
    @Column(nullable = false)
    private Integer maintenanceIntervalDays;

    @PositiveOrZero(message = "Current usage hours cannot be negative")
    @Column(nullable = false)
    private Double currentUsageHours = 0.0;

    private LocalDateTime lastMaintenanceDate;

    @Column(nullable = false)
    private Boolean active = true;
}