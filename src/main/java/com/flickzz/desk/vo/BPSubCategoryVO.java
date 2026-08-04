package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSubCategoryVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long subCategoryId;

	private BPCategoryVO category;

	private String subCategoryName;

	private Boolean isActive;

	private Boolean isUnderApproval;

	private Long createdBy;

	private Long updatedBy;
}