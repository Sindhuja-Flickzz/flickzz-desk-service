package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_SKILL_MASTER")
public class SkillMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skillGen")
    @SequenceGenerator(name = "skillGen", sequenceName = "SKILL_SEQ", allocationSize = 1)
    @Column(name = "SKILL_ID", unique = true, nullable = false)
    private Long skillId;

    @Column(name = "SKILL_NAME", nullable = false, length = 100, unique = true)
    private String skillName;

    @Column(name = "EXPERIENCE_YEARS", nullable = false)
    private Integer experienceYears;

    @Column(name = "EXPERIENCE_MONTHS", nullable = false)
    private Integer experienceMonths;
    
    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
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
