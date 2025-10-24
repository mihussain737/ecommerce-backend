package com.ecommerce.sb_ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Entity
@Table(name = "roles")
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20,name="role_name")
    private AppRole roleName;

    public Role(AppRole roleName){
        this.roleName=roleName;
    }
}
