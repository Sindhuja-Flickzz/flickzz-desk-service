package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPCategoryVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long categoryId;

	private BPConfigurationVO configuration;

	private String categoryName;

	private Boolean isActive;

	private Boolean isUnderApproval;

	private Long createdBy;

	private Long updatedBy;

	private List<BPSubCategoryVO> subCategories;
}