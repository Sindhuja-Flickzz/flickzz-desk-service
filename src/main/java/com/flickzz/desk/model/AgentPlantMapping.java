package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "FD_AGENT_PLANT_MAPPING",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_AGENT_PLANT",
                        columnNames = {"AGENT_ID", "PLANT_ID"}
                )
        }
)
public class AgentPlantMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentPlantMappingGen")
    @SequenceGenerator(name = "agentPlantMappingGen", sequenceName = "FD_AGENT_PLANT_MAPPING_SEQ", allocationSize = 1)
    @Column(name = "MAPPING_ID", unique = true, nullable = false)
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "AGENT_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_AGENT_PLANT_AGENT")
    )
    private AgentMaster agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "PLANT_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_AGENT_PLANT_PLANT")
    )
    @JsonBackReference
    private PlantMaster plant;

    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean creatorAdmin;

    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean updaterAdmin = false;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

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
