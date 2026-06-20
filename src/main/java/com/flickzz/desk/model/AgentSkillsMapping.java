package com.flickzz.desk.model;

import java.time.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

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

	@Column(name = "EXPERIENCE_YEARS", nullable = false)
	private Integer experienceYears;

	@Column(name = "EXPERIENCE_MONTHS", nullable = false)
	private Integer experienceMonths;

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
