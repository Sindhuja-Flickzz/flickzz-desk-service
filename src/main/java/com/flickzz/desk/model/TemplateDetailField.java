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
@Table(name = "FD_TEMPLATE_DETAIL_FIELD")
public class TemplateDetailField {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templateFieldGen")
	@SequenceGenerator(name = "templateFieldGen", sequenceName = "TEMPLATE_FIELD_SEQ", allocationSize = 1)
	@Column(name = "FIELD_ID")
	private Long fieldId;

	@ManyToOne
	@JoinColumn(name = "TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_FIELD_TEMPLATE"), nullable = false)
	@JsonBackReference
	private TemplateDetails template;

	@Column(name = "FIELD_NAME", nullable = false, length = 255)
	private String fieldName;

	@ManyToOne
	@JoinColumn(name = "FIELD_TYPE_ID", foreignKey = @ForeignKey(name = "FK_FIELD_TYPE"), nullable = false)
	private FieldType fieldType;

	@Column(name = "MANDATORY", nullable = false)
	private Boolean mandatory;

	@Column(name = "FIELD_SEQUENCE", nullable = false)
	private Integer fieldSequence;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<TemplateFieldOption> options;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
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
