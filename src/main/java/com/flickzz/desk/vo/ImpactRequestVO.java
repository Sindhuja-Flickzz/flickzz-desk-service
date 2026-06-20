package com.flickzz.desk.vo;

import java.io.*;
import java.math.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpactRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long impactId;
	private String impactCode; // LOW, MEDIUM, HIGH, CRITICAL
	private Integer impactLevel; // 1 = highest
	private BigDecimal slaMultiplier;
	private Long orgId;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
