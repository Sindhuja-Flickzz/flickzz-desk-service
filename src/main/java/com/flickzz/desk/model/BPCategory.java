package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_CATEGORY", uniqueConstraints = {
		@UniqueConstraint(name = "UK_CATEGORY_NAME_VERSION", columnNames = { "CONFIGURATION_ID", "CATEGORY_NAME", "VERSION" }) })
public class BPCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpCategorySeq")
	@SequenceGenerator(name = "bpCategorySeq", sequenceName = "BP_CATEGORY_SEQ", allocationSize = 1)
	@Column(name = "CATEGORY_ID")
	private Long categoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private BPConfiguration configuration;

	@Column(name = "CATEGORY_NAME", nullable = false)
	private String categoryName;

	@Builder.Default
	@Column(name = "VERSION", nullable = false)
	private Integer version = 1;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Builder.Default
	@Column(name = "IS_UNDER_APPROVAL")
	private Boolean isUnderApproval = false;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	private List<BPSubCategory> subCategories = new ArrayList<>();

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