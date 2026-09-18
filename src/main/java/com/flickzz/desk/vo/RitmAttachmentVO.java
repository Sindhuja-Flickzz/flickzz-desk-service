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
public class RitmAttachmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long attachmentId;

    private RitmMasterVO ritmId;

    private String fileName;

    private String originalFileName;

    private String mimeType;

    private Long fileSize;

    private String storageType;

    private String storagePath;

    private String fileHash;

    private Boolean isActive;

    private Long uploadedBy;

    private LocalDateTime uploadedAt;

    private Long deletedBy;

    private LocalDateTime deletedAt;
}
