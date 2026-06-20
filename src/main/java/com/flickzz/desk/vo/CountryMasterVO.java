package com.flickzz.desk.vo;

import java.io.*;
import java.time.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryMasterVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long countryId;
	private String countryName;
	private String isoCode;
	private String phoneCode;
	private String currencyCode;
	private String currencyName;
	private String timezone;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
