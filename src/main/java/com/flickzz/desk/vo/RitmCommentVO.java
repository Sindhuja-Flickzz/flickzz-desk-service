package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmCommentVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long commentId;
    private RitmMasterVO ritm;
    private String commentType;
    private String commentText;
    private Boolean isInternal;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
