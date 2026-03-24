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
import jakarta.persistence.OneToOne;
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
@Table(name = "LOGIN_MASTER")
public class LoginMaster {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="loginGenn")
    @Column(name = "LOGIN_ID", unique=true, nullable = false)
	@SequenceGenerator(name="loginGenn", sequenceName = "LOGIN_SEQ", allocationSize = 1)
    private Long loginId;
    
    @Column(name = "USER_NAME", nullable = false, length = 255, unique = true)
    private String userName;
    
    @Column(name = "PASSWORD", nullable = false, length = 255, unique = true)
    private String password;
    
    @Column(name = "ROLE", nullable = false, length = 100)
    private String role;

    @Column(name = "LAST_LOGIN_DATETIME")
    private Date lastLoginDatetime;
    
    // Foreign key to FD_USER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "FK_LOGIN_USER"))
    private User user;

    // Foreign key to AUTH
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTH_ID", unique = true,
                foreignKey = @ForeignKey(name = "FK_LOGIN_AUTH"))
    private Auth auth;

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
