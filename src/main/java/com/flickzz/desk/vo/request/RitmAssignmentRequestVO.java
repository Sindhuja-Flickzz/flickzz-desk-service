package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RitmAssignmentRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long agentId;
    private List<Long> supportGroupIds;
}
