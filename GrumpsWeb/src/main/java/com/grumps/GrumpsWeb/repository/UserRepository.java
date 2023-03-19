package com.grumps.GrumpsWeb.repository;

import com.grumps.GrumpsWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
