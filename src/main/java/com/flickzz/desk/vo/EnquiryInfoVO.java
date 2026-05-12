package com.flickzz.desk.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryInfoVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String token;
	private EnquiryRegistrationVO enquiryRegistration;
	private LocalDateTime expiryTime;
	@Builder.Default
	private Boolean used = false;
}
