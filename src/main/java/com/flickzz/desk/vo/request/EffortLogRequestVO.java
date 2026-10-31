package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EffortLogRequestVO {

    private Long effortId;
    private Long ticketId;
    private Long orgId;
    private LocalDate date;
    private Integer effortHours;
    private Integer effortMinutes;
    private Long effortTypeId;
    private String remark;
    private Long userWorked;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
