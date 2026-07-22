package com.flickzz.desk.vo;

import java.time.*;

import lombok.*;
import tools.jackson.databind.*;

@Getter
@Setter
public class SystemAuditVO {

	private Long auditId;
	private String module;
	private String area;
	private String entityName;
	private Long entityId;
	private String action;
	private String description;
	private JsonNode oldValue;
	private JsonNode newValue;
	private JsonNode changedFields;
	private Long userId;
	private String userName;
	private Long companyId;
	private String requestId;
	private String sessionId;
	private String url;
	private String httpMethod;
	private String ipAddress;
	private String userAgent;
	private String status;
	private String errorMessage;
	private LocalDateTime createdAt;
}