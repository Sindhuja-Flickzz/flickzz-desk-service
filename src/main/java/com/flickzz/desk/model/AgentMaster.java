package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

	@Column(name = "PHONE", length = 20)
	private String phone;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_AGENT_USER"))
	private User user;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_AGENT_COMPANY"))
	private CompanyMaster organization;

	@ManyToOne
	@JoinColumn(name = "CALENDAR_ID", foreignKey = @ForeignKey(name = "FK_AGENT_CALENDAR"))
	private CalendarMaster calendarMaster;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

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
