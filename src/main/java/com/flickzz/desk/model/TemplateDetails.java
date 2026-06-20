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
@ToString(exclude = "fields")
@Table(name = "FD_TEMPLATE_DETAILS")
public class TemplateDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templateGen")
	@SequenceGenerator(name = "templateGen", sequenceName = "TEMPLATE_SEQ", allocationSize = 1)
	@Column(name = "TEMPLATE_ID")
	private Long templateId;

	@Column(name = "TEMPLATE_NAME", nullable = false, length = 255)
	private String templateName;

	@ManyToOne
	@JoinColumn(name = "WORK_ITEM_ID", foreignKey = @ForeignKey(name = "FK_TEMPLATE_WORK_ITEM"), nullable = false)
	private WorkItem workItem;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_TEMPLATE_COMPANY"), nullable = false)
	private CompanyMaster company;

	@OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<TemplateDetailField> fields;

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
