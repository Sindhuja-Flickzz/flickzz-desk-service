package com.flickzz.desk.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = { "agent", "skill" })
@Table(name = "FD_AGENT_SKILLS_MAPPING")
public class AgentSkillsMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentSkillSeq")
	@SequenceGenerator(name = "agentSkillSeq", sequenceName = "AGENT_SKILL_SEQ", allocationSize = 1)
	@Column(name = "AGENT_SKILL_ID")
	private Long agentSkillId;

	@ManyToOne
	@JoinColumn(name = "AGENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_AGENT_SKILL_AGENT"))
	@JsonManagedReference
	private AgentMaster agent;

	@ManyToOne
	@JoinColumn(name = "SKILL_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_AGENT_SKILL_SKILL"))
	@JsonManagedReference
	private SkillMaster skill;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt = LocalDateTime.now();

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
