package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_ENQUIRY_INFO")
public class EnquiryInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enquiryTokenGen")
	@SequenceGenerator(name = "enquiryTokenGen", sequenceName = "ENQUIRY_INFO_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "TOKEN", nullable = false, length = 255, unique = true)
	private String token;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ENQUIRY_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_ENQUIRY_INFO_REGISTRATION"))
	private EnquiryRegistration enquiryRegistration;

	@Column(name = "EXPIRY_TIME", nullable = false)
	private LocalDateTime expiryTime;

	@Builder.Default
	@Column(name = "IS_USED")
	private Boolean used = false;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
