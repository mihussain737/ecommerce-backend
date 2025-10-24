package com.ecommerce.sb_ecom.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(min=3,max = 20)
    private String username;

    @NotBlank
    @Size(min=3,max = 30)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5,max = 15)
    private String password;

    private Set<String> role;
}
