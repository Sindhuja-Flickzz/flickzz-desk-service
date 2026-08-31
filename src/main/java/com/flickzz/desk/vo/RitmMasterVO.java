package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmMasterVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long ritmId;
    private String ritmNumber;
    private CompanyMasterVO company;
    private AgentMasterVO requestedBy;
    private AgentMasterVO requestedFor;
    private BPCategoryVO category;
    private BPSubCategoryVO subCategory;
    private BPSupportGroupVO supportGroup;
    private BPPriorityVO priority;
    private String shortDescription;
    private String description;
    private String stepsToReproduce;
    private String otherNotes;
    private AgentMasterVO assignedTo;
    private List<RitmAttachmentVO> ritmAttachments;
    private List<RitmWatchlistVO> watchlist;
    private List<RitmCommentVO> comments;
    private List<RitmAuditVO> audits;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime cancelledAt;
    private String actionReason;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
}
