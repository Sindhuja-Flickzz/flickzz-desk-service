package com.flickzz.desk.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "FD_COMPANY_MASTER")
public class CompanyMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companyGen")
    @SequenceGenerator(name = "companyGen", sequenceName = "COMPANY_SEQ", allocationSize = 1)
    @Column(name = "COMPANY_ID", unique = true, nullable = false)
    private Long companyId;

    @Column(name = "COMPANY_NAME", unique = true, nullable = false, length = 255)
    private String companyName;

    @Column(name = "REGISTERED_NUMBER", nullable = false, length = 100)
    private String registeredNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID",
                foreignKey = @ForeignKey(name = "FK_PLANT_REGION"), nullable = false)
    private CountryMaster country;
    
    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "MAIL", length = 100)
    private String mail;

    @Builder.Default
    @Column(name = "IS_SERVICE_PROVIDER", nullable = false)
    private Boolean isServiceProvider = false;

    @Builder.Default
    @Column(name = "IS_REQUESTOR", nullable = false)
    private Boolean isRequestor = false;
    
    @Builder.Default
    @Column(name = "IS_BOTH", nullable = false)
    private Boolean isBoth = false;

    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CREATED_AT", updatable = false)
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
}
