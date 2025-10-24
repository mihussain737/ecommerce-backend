package com.ecommerce.sb_ecom.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {


        System.out.println(passwordEncoder().encode("admin"));
        System.out.println(passwordEncoder().encode("user1"));
    }
    @Bean
   static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
