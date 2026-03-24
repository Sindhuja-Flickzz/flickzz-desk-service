package com.flickzz.desk.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "AUTH")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="authGenn")
    @Column(name = "AUTH_ID", unique=true, nullable = false)
	@SequenceGenerator(name="authGenn", sequenceName = "AUTH_SEQ", allocationSize = 1)
    private Long authId;

    @Column(name = "TOKEN", nullable = false, length = 255, unique = true)
    private String token;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Date expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false,
                foreignKey = @ForeignKey(name = "FD_USERS_REFRESH_TOKEN"))
    private User user;

    /* ===================== STATUS ===================== */
    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CREATED_AT", updatable = false)
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
}
