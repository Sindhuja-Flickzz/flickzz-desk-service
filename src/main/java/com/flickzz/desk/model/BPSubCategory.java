package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_SUB_CATEGORY", uniqueConstraints = {
		@UniqueConstraint(name = "UK_SUB_CATEGORY", columnNames = { "CATEGORY_ID", "SUB_CATEGORY_NAME" }) })
@ToString(exclude = "category")
public class BPSubCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSubCategorySeq")
	@SequenceGenerator(name = "bpSubCategorySeq", sequenceName = "BP_SUB_CATEGORY_SEQ", allocationSize = 1)
	@Column(name = "SUB_CATEGORY_ID")
	private Long subCategoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private BPCategory category;

	@Column(name = "SUB_CATEGORY_NAME", nullable = false, length = 100)
	private String subCategoryName;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "CREATED_AT", updatable = false)
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