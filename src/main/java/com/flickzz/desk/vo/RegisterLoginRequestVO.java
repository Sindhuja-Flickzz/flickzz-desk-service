package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginRequestVO {
    
    private String firstname;    
    private String lastname;
    private String email;    
    private String password;    
    private String role;    
    private Boolean mfaEnabled;
}
