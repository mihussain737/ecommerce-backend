package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User ,Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
