package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(length = 500)
    private String imagePath;

    private Double latitude;
    private Double longitude;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime resolvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Builder.Default
    private boolean fakeFlag = false;

    @Column(length = 1000)
    private String workNotes;

    @Column(length = 500)
    private String resolutionImagePath;

    @Column(name = "rating")
    private Integer rating;

    @Column(length = 1000)
    private String feedback;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
