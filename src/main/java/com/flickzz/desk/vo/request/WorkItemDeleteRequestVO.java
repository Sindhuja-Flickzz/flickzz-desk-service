package com.flickzz.desk.vo.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemDeleteRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long orgId;
    private Long deletedBy;
    private Boolean isDeletedByAdmin;
}
