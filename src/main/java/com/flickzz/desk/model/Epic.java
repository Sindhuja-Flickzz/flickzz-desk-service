package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	@JsonManagedReference
	private Project project; // maps to FD_PROJECT

	@Column(name = "EPIC_NAME", nullable = false)
	private String epicName;

	@Column(name = "EPIC_SEQUENCE", nullable = false)
	private Integer epicSequence;

	@ManyToOne
	@JoinColumn(name = "PROGRESS_ID", referencedColumnName = "PROGRESS_ID", nullable = false)
	private ProgressStatus progressStatus; // maps to FD_PROGRESS_STATUS

	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

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
