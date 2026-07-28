package com.flickzz.desk.vo;

import java.time.*;

import lombok.*;

@Getter
@Setter
public class SystemAuditVO {

	private Long auditId;
	private String module;
	private String area;
	private String entityName;
	private Long entityId;
	private String action;
	private String oldValue;
	private String newValue;
	private String changedFields;
	private Long userId;
	private Long companyId;
	private String status;
	private String errorMessage;
	private LocalDateTime createdAt;
}