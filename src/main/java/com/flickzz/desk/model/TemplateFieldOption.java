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
@Table(name = "FD_TEMPLATE_FIELD_OPTION")
public class TemplateFieldOption {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fieldOptionGen")
	@SequenceGenerator(name = "fieldOptionGen", sequenceName = "FIELD_OPTION_SEQ", allocationSize = 1)
	@Column(name = "OPTION_ID")
	private Long optionId;

	@ManyToOne
	@JoinColumn(name = "FIELD_ID", foreignKey = @ForeignKey(name = "FK_OPTION_FIELD"), nullable = false)
	@JsonBackReference
	private TemplateDetailField field;

	@Column(name = "LABEL", nullable = false, length = 255)
	private String label;

	@Column(name = "VALUE", nullable = false, length = 255)
	private String value;

	@Column(name = "DEFAULT_SELECTED")
	private Boolean defaultSelected;

	@Column(name = "OPTION_SEQUENCE", nullable = false)
	private Integer optionSequence;

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
