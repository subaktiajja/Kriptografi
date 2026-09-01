package com.springboot.authentication.repository;

import com.springboot.authentication.entity.PasswordResetToken;
import com.springboot.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByToken(String token);

  void deleteByUser(User user);
}