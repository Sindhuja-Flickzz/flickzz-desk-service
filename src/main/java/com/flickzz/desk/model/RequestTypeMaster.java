package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_REQUEST_TYPE_MASTER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_REQUEST_TYPE_NAME_COMPANY",
                        columnNames = {"REQUEST_TYPE_NAME", "COMPANY_ID"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestTypeMaster {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_type_seq"
    )
    @SequenceGenerator(
            name = "request_type_seq",
            sequenceName = "REQUEST_TYPE_SEQ",
            allocationSize = 1
    )
    @Column(name = "REQUEST_TYPE_ID")
    private Long requestTypeId;

    @Column(name = "REQUEST_TYPE_NAME", nullable = false, length = 100)
    private String requestTypeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean isUpdaterAdmin = false;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}