package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.AuthenticationResult;
import com.ecommerce.sb_ecom.payload.UserResponse;
import com.ecommerce.sb_ecom.security.request.LoginRequest;
import com.ecommerce.sb_ecom.security.request.SignupRequest;
import com.ecommerce.sb_ecom.security.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<?> registerUser(@Valid SignupRequest signupRequest);

    UserResponse getAllSellers(Pageable pageDetails);
}
