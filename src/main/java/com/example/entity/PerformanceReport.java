package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "performance_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, unique = true)
    private User staff;

    @Builder.Default
    private int totalTasks = 0;

    @Builder.Default
    private double avgResponseTimeHours = 0.0;
}
