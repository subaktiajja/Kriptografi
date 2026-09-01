package com.springboot.authentication.repository;

import com.springboot.authentication.entity.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {
  Optional<Token> findByToken(String token);
}