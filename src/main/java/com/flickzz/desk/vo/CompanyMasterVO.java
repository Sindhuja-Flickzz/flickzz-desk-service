package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMasterVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long companyId;
    private String companyName;
    private String registeredNumber;
    private CountryMasterVO country;
    private String address;
    private String mail;
    private Boolean isServiceProvider;
    private Boolean isRequestor;
    private Boolean isBoth;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
}
