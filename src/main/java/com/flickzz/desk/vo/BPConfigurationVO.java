package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPConfigurationVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long configurationId;

	private BusinessPartnerVO businessPartner;

	private Boolean isActive;

	private Long createdBy;

	private Long updatedBy;

	private Boolean isCreatorAdmin;

	private Boolean isUpdaterAdmin;
}
