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
@Table(name = "FD_EPIC")
public class Epic {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "epic_seq")
	@SequenceGenerator(name = "epic_seq", sequenceName = "EPIC_SEQ", allocationSize = 1)
	@Column(name = "EPIC_ID")
	private Long epicId;

	@ManyToOne
	@JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
	@JsonBackReference
	private Project project; // maps to FD_PROJECT

	@Column(name = "EPIC_NAME", nullable = false)
	private String epicName;

	@Column(name = "EPIC_DESC")
	private String epicDesc;

	@Column(name = "EPIC_SEQUENCE", nullable = false)
	private Integer epicSequence;

	@ManyToOne
	@JoinColumn(name = "PROGRESS_ID", referencedColumnName = "PROGRESS_ID", nullable = false)
	private ProgressStatus progressStatus; // maps to FD_PROGRESS_STATUS

	@Column(name = "PLANNED_START_DATE")
	private Date plannedStartDate;

	@Column(name = "PLANNED_END_DATE")
	private Date plannedEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

	@Column(name = "MAX_PROGRESS_NUMBER", nullable = false)
	private Integer maxProgressStatus;

	@OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<UserStory> userStories;

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
