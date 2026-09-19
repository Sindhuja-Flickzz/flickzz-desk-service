package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long statusId;
    private CompanyMasterVO company;
    private String statusCode;
    private Integer sequenceNo;
    private Boolean isActive;
    private Long createdBy;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
    private Long updatedBy;
}
