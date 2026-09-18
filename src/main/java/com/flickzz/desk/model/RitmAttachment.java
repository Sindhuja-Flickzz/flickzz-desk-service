package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_RITM_ATTACHMENT")
public class RitmAttachment {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ritmAttachmentSeq")
    @SequenceGenerator(name = "ritmAttachmentSeq", sequenceName = "FD_RITM_ATTACHMENT_SEQ", allocationSize = 1)
    @Column(name = "ATTACHMENT_ID")
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RITM_ID", nullable = false)
    private RitmMaster ritm;

    @Column(name = "FILE_NAME", nullable = false, length = 255)
    private String fileName;

    @Column(name = "ORIGINAL_FILE_NAME", length = 255)
    private String originalFileName;

    @Column(name = "MIME_TYPE", length = 150)
    private String mimeType;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "STORAGE_TYPE", length = 30)
    private String storageType;

    @Column(name = "STORAGE_PATH", nullable = false, length = 1000)
    private String storagePath;

    @Column(name = "FILE_HASH", length = 255)
    private String fileHash;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "UPLOADED_BY", nullable = false)
    private Long uploadedBy;

    @Column(name = "UPLOADED_AT")
    private LocalDateTime uploadedAt;

    @Column(name = "DELETED_BY")
    private Long deletedBy;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
