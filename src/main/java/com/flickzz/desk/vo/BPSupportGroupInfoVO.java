package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSupportGroupInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long supportGroupId;
    private String supportGroupName;
    private Long totalCountRitm;
    private Long unassignedRitmCount;
    private List<BPSupportGroupStatusCountInfoVO> statusCounts;
    private List<BPSupportGroupAgentInfoVO> agents;
}