package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_RITM_COMMENT")
public class RitmComment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ritmCommentSeq"
    )
    @SequenceGenerator(
            name = "ritmCommentSeq",
            sequenceName = "FD_RITM_COMMENT_SEQ",
            allocationSize = 1
    )
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RITM_ID", nullable = false)
    private RitmMaster ritm;

    @Column(name = "COMMENT_TYPE", nullable = false, length = 30)
    private String commentType;

    @Column(name = "COMMENT_TEXT", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Builder.Default
    @Column(name = "IS_INTERNAL")
    private Boolean isInternal = false;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
