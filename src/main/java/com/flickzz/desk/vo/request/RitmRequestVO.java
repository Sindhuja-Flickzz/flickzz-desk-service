package com.flickzz.desk.vo.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class RitmRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long ritmId;
    private String ritmNumber;
    private Long orgId;
    private Long openedBy;
    private Long requestedFor;
    private Long category;
    private Long subCategory;
    @JsonAlias({"assignmentGroup", "supportGroup"})
    private Long supportGroup;
    private Long priority;
    private String shortDescription;
    private String description;
    private String stepsToReproduce;
    private String otherNotes;
    private Long assignedTo;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime cancelledAt;
    private String actionReason;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
    private String requestType;
    private String location;
    private String availabilityTime;
    private String currentTime;
    @JsonAlias({"assignmentGroup"})
    private Long assignmentGroup;
    private List<Long> watchList;
    private List<String> attachments;
}
