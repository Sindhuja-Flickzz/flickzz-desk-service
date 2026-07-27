package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantWeekoffVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long weekoffId;
    private String weekoff;
    private PlantMasterVO plant;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
