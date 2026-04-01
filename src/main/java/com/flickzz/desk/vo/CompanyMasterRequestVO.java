package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyMasterRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long companyId;
    private String companyName;
    private String registeredNumber;
    private Long countryId;    // reference to CountryMaster;
    private String address;
    private String mail;
    private Boolean markAsServiceProvider;
    private Boolean isServiceProvider;
    private Boolean isRequestor;
    private String createdBy;
    private String updatedBy;
}
