package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_AGENT_SKILLS_MAPPING")
public class AgentSkillsMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentSkillSeq")
    @SequenceGenerator(name = "agentSkillSeq", sequenceName = "AGENT_SKILL_SEQ", allocationSize = 1)
    @Column(name = "AGENT_SKILL_ID")
    private Long agentSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_AGENT_SKILL_AGENT"))
    private AgentMaster agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_AGENT_SKILL_SKILL"))
    private SkillMaster skill;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
