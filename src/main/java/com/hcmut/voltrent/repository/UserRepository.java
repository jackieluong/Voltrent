package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

//    Optional<User> findByEmail(String email);

    User findByEmail(String email);
}
