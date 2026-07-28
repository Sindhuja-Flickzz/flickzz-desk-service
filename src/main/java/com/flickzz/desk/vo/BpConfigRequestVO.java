package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpConfigRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long priorityId;
	private Long ticketTypeId;
	private Long businessPartnerId;
	private String code;
	private Integer level;
	private String description;
	private Long orgId;

	private Long slaId;
	private Integer firstResponseTime;
	private Character firstResponseTerm;
	private Integer resolutionTime;
	private Character resolutionTerm;
	private Integer updateFrequency;
	private Character updateFrequencyTerm;

	private Long categoryId;
	private String categoryName;
	private List<String> subCategories;

	private Long supportGroupId;
	private String groupName;
	private List<Long> agents;
	private List<Long> managerInternalAgents;
	private List<Long> managerBpAgents;
	private Long subCategoryId;

	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;

}
