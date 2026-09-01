package com.springboot.authentication.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @Column(nullable = false)
  private boolean used = false;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public PasswordResetToken() {
  }

  public PasswordResetToken(String token, LocalDateTime expiredAt, User user) {
    this.token = token;
    this.expiredAt = expiredAt;
    this.user = user;
    this.used = false;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiredAt);
  }

  public Long getId() {
    return id;
  }

  public String getToken() {
    return token;
  }

  public LocalDateTime getExpiredAt() {
    return expiredAt;
  }

  public boolean isUsed() {
    return used;
  }

  public User getUser() {
    return user;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setExpiredAt(LocalDateTime expiredAt) {
    this.expiredAt = expiredAt;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public void setUser(User user) {
    this.user = user;
  }
}