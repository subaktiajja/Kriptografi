package com.springboot.authentication.repository;

import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    // Untuk mengambil semua user yang role-nya USER
    List<User> findByRole(Role role);

    // Untuk menghitung jumlah user berdasarkan role
    long countByRole(Role role);
}