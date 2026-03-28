package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String firstname;    
    private String lastname;
    private String email;    
    private String password;    
    private String role;    
    private Boolean mfaEnabled;
}
