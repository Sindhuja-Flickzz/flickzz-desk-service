package com.flickzz.desk.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String createdBy;
	private String updatedBy;
}
