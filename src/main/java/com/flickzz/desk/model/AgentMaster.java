package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = { "calendarMaster", "agentSkillsMappings", "organization" })
@Table(name = "FD_AGENT_MASTER")
public class AgentMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentGen")
	@SequenceGenerator(name = "agentGen", sequenceName = "AGENT_SEQ", allocationSize = 1)
	@Column(name = "AGENT_ID", unique = true, nullable = false)
	private Long agentId;

	@Column(name = "AGENT_NAME", nullable = false, unique = true, length = 255)
	private String agentName;

	@Column(name = "MAIL_ID", nullable = false, length = 100)
	private String mailId;

	@Column(name = "ACCESS_ID", nullable = false, length = 100)
	private String accessId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_AGENT_USER"))
	private User user;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_AGENT_COMPANY"), nullable = false)
	private CompanyMaster organization;

	@ManyToOne
	@JoinColumn(name = "CALENDAR_ID", foreignKey = @ForeignKey(name = "FK_AGENT_CALENDAR"))
	private CalendarMaster calendarMaster;

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

	@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<AgentSkillsMapping> agentSkillsMappings;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

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
