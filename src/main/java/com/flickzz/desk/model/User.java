package com.flickzz.desk.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "FD_USER")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="userGenn")
    @Column(name = "USER_ID", unique=true, nullable = false)
	@SequenceGenerator(name="userGenn", sequenceName = "USER_SEQ", allocationSize = 1)
    private Long userId;
    
    @Column(name = "USER_NAME", nullable = false, length = 50, unique = true)
    private String userName;
    
    @Column(name = "PASSWORD", nullable = false, length = 255, unique = true)
    private String password;
    
    @Column(name = "FIRST_NAME", nullable = false, length = 50, unique = true)
    private String firstName;
    
    @Column(name = "LAST_NAME", nullable = false, length = 50, unique = true)
    private String lastName;

    @Column(name = "EMAIL", nullable = false, length = 50, unique = true)
    private String email;
    
    @Column(name = "ROLE", nullable = false, length = 100)
    private String role;
    
    @Column(name = "MFA_ENABLED", nullable = false)
    private boolean mfaEnabled;
    
    @Column(name = "SECRET", length = 255)
    private String secret;

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
