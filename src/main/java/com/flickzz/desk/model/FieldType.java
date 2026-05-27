package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_FIELD_TYPES")
public class FieldType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_type_seq")
	@SequenceGenerator(name = "field_type_seq", sequenceName = "FIELD_TYPE_SEQ", allocationSize = 1)
	@Column(name = "TYPE_ID")
	private Long typeId;

	@Column(name = "CODE", nullable = false, unique = true, length = 50)
	private String code;

	@Column(name = "LABEL", nullable = false, length = 100)
	private String label;

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
