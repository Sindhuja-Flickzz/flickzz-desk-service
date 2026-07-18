package com.flickzz.desk.model;

import java.time.*;

import org.hibernate.annotations.*;
import org.hibernate.type.*;

import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.*;

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

	@Column(name = "DESCRIPTION")
	private String description;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "OLD_VALUE", columnDefinition = "jsonb")
	private JsonNode oldValue;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "NEW_VALUE", columnDefinition = "jsonb")
	private JsonNode newValue;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "CHANGED_FIELDS", columnDefinition = "jsonb")
	private JsonNode changedFields;

	@Column(name = "USER_ID", nullable = false)
	private Long userId;

	@Column(name = "USER_NAME", length = 200)
	private String userName;

	@Column(name = "COMPANY_ID")
	private Long companyId;

	@Column(name = "REQUEST_ID", length = 100)
	private String requestId;

	@Column(name = "SESSION_ID", length = 200)
	private String sessionId;

	@Column(name = "URL", length = 500)
	private String url;

	@Column(name = "HTTP_METHOD", length = 20)
	private String httpMethod;

	@Column(name = "IP_ADDRESS", length = 100)
	private String ipAddress;

	@Column(name = "USER_AGENT", length = 1000)
	private String userAgent;

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