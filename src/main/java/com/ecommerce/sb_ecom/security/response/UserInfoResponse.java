package com.ecommerce.sb_ecom.security.response;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class UserInfoResponse {

    private Long id;
    @JsonIgnore
    private String token;
    private String username;
    private List<String> roles;

    public UserInfoResponse() {
    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id=id;
        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse(Long id,String token, String username, List<String> roles) {
        this.id=id;
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }



    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
