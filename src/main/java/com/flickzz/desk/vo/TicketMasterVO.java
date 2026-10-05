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
public class TicketMasterVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long ticketId;
    private String ticketNumber;
    private CompanyMasterVO company;
    private AgentMasterVO requestedBy;
    private AgentMasterVO requestedFor;
    private BPCategoryVO category;
    private BPSubCategoryVO subCategory;
    private BPSupportGroupVO supportGroup;
    private BPPriorityVO priority;
    private AgentMasterVO assignedTo;
    private RequestTypeMasterVO requestType;
    private List<TicketAttachmentVO> ritmAttachments;
    private List<TicketWatchlistVO> watchlist;
    private List<TicketTemplateDetailVO> templateDetails;
    private List<TicketCommentVO> comments;
    private List<TicketAuditVO> audits;
    private StatusMasterVO status;
    private LocalDateTime requestedAt;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime customerResolution;
    private String actionReason;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
}
