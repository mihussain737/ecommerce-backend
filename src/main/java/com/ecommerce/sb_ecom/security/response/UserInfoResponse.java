package com.ecommerce.sb_ecom.security.response;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserInfoResponse {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String jwtToken;
    @Setter
    @Getter
    private String username;
    private List<String> roles;

    public UserInfoResponse() {
    }

    public UserInfoResponse(Long id, String jwtToken,String username, List<String> roles) {
        this.id=id;
        this.username = username;
        this.roles = roles;
        this.jwtToken=jwtToken;
    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id=id;
        this.username = username;
        this.roles = roles;
        this.jwtToken=jwtToken;
    }




    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
