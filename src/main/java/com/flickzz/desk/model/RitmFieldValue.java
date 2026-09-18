package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_RITM_FIELD_VALUE",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_RITM_TEMPLATE_FIELD",
                        columnNames = {"RITM_ID", "TEMPLATE_FIELD_ID"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmFieldValue {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ritmFieldValueSeq"
    )
    @SequenceGenerator(
            name = "ritmFieldValueSeq",
            sequenceName = "FD_RITM_FIELD_VALUE_SEQ",
            allocationSize = 1
    )
    @Column(name = "RITM_FIELD_VALUE_ID")
    private Long ritmFieldValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RITM_ID", nullable = false)
    private RitmMaster ritm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_FIELD_ID", nullable = false)
    private TemplateField templateField;

    @Column(name = "FIELD_VALUE", columnDefinition = "TEXT")
    private String fieldValue;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "IS_UPDATER_ADMIN", nullable = false)
    @Builder.Default
    private Boolean isUpdaterAdmin = false;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (isActive == null) {
            isActive = true;
        }

        if (isUpdaterAdmin == null) {
            isUpdaterAdmin = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}