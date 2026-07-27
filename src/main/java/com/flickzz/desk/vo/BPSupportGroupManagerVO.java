package com.flickzz.desk.vo;

import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.BPSupportGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSupportGroupManagerVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long managerId;
    private BPSupportGroupVO supportGroup;
    private AgentMasterVO agent;
    private Boolean isInternal;
    private Boolean isBP;
    private Boolean isActive;
}
