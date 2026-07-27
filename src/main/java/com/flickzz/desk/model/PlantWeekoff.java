package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_PLANT_WEEKOFF")
public class PlantWeekoff {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weekoffGen")
    @SequenceGenerator(name = "weekoffGen", sequenceName = "weekoff_seq", allocationSize = 1)
    @Column(name = "WEEKOFF_ID", unique = true, nullable = false)
    private Long weekoffId;

    @Column(name = "WEEKOFF", nullable = false, length = 20)
    private String weekoff;

    @ManyToOne
    @JoinColumn(name = "PLANT_ID", nullable = false)
    private PlantMaster plant;
    
    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Builder.Default
    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean isUpdaterAdmin = false;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // or follow JavaBean convention for booleans:
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
