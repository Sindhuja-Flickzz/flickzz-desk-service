package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_TEMPLATE_FIELD")
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templateFieldGen")
    @SequenceGenerator(name = "templateFieldGen", sequenceName = "TEMPLATE_FIELD_SEQ", allocationSize = 1)
    @Column(name = "FIELD_ID")
    private Long fieldId;

    @ManyToOne
    @JoinColumn(name = "TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_FIELD_TEMPLATE"), nullable = false)
    @JsonBackReference
    private Template template;

    @Column(name = "FIELD_NAME", nullable = false, length = 255)
    private String fieldName;

    @Column(name = "DEFAULT_VALUE", columnDefinition = "TEXT")
    private String defaultValue;

    @Builder.Default
    @Column(name = "IS_EDITABLE")
    private boolean isEditable = true;

    @ManyToOne
    @JoinColumn(name = "FIELD_TYPE_ID", foreignKey = @ForeignKey(name = "FK_FIELD_TYPE"), nullable = false)
    private FieldType fieldType;

    @Column(name = "MANDATORY", nullable = false)
    private Boolean mandatory;

    @Column(name = "FIELD_SEQUENCE", nullable = false)
    private Integer fieldSequence;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TemplateFieldOption> options;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Builder.Default
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
