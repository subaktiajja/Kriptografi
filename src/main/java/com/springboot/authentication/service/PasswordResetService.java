package com.springboot.authentication.service;

import com.springboot.authentication.entity.PasswordResetToken;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.PasswordResetTokenRepository;
import com.springboot.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public PasswordResetService(
      UserRepository userRepository,
      PasswordResetTokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      EmailService emailService) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @Transactional
  public void createResetTokenAndSendEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Akun dengan email tersebut tidak ditemukan."));

    tokenRepository.deleteByUser(user);

    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken = new PasswordResetToken(
        token,
        LocalDateTime.now().plusMinutes(30),
        user);

    tokenRepository.save(resetToken);

    emailService.sendResetPasswordMail(email, token);
  }

  @Transactional
  public void resetPassword(String token, String newPassword, String confirmPassword) {
    if (token == null || token.isBlank()) {
      throw new RuntimeException("Token reset password tidak boleh kosong.");
    }

    if (newPassword == null || newPassword.isBlank()) {
      throw new RuntimeException("Password baru tidak boleh kosong.");
    }

    if (!newPassword.equals(confirmPassword)) {
      throw new RuntimeException("Password dan konfirmasi password tidak sama.");
    }

    if (newPassword.length() < 6) {
      throw new RuntimeException("Password minimal 6 karakter.");
    }

    PasswordResetToken resetToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Token reset password tidak valid."));

    if (resetToken.isUsed()) {
      throw new RuntimeException("Token sudah pernah digunakan.");
    }

    if (resetToken.isExpired()) {
      throw new RuntimeException("Token sudah kadaluarsa. Silakan minta link reset password baru.");
    }

    User user = resetToken.getUser();

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    resetToken.setUsed(true);
    tokenRepository.save(resetToken);
  }
}