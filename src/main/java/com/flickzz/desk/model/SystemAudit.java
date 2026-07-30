package com.flickzz.desk.model;

import java.time.*;

import org.hibernate.annotations.*;
import org.hibernate.type.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "FD_SYSTEM_AUDIT")
public class SystemAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "systemAuditSeq")
	@SequenceGenerator(name = "systemAuditSeq", sequenceName = "FD_SYSTEM_AUDIT_SEQ", allocationSize = 1)
	@Column(name = "AUDIT_ID")
	private Long auditId;

	@Column(name = "MODULE", nullable = false, length = 100)
	private String module;

	@Column(name = "AREA", length = 100)
	private String area;

	@Column(name = "ENTITY_NAME", nullable = false, length = 100)
	private String entityName;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "ACTION", nullable = false, length = 50)
	private String action;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "OLD_VALUE", columnDefinition = "jsonb")
	private String oldValue;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "NEW_VALUE", columnDefinition = "jsonb")
	private String newValue;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "CHANGED_FIELDS", columnDefinition = "jsonb")
	private String changedFields;

	@Column(name = "USER_ID", nullable = false)
	private Long userId;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "COMPANY_ID")
	private Long companyId;

	@Column(name = "STATUS", length = 20)
	private String status;

	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}