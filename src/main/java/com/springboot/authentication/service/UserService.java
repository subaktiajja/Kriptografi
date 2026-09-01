package com.springboot.authentication.service;

import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  private final ConcurrentHashMap<String, String> confirmationTokens = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> resetPasswordTokens = new ConcurrentHashMap<>();

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      EmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {

    User user = userRepository.findByUsernameOrEmail(username, username)
        .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan"));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        true,
        true,
        true,
        Collections.singletonList(
            new SimpleGrantedAuthority(user.getRole().name())));
  }

  public String registerUser(User user) {

    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      return "Username sudah digunakan";
    }

    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      return "Email sudah digunakan";
    }

    if (user.getRole() == null) {
      user.setRole(Role.USER);
    }

    user.setEnabled(false);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    String token = UUID.randomUUID().toString();
    confirmationTokens.put(token, user.getEmail());

    emailService.sendSimpleMail(user.getEmail(), token);

    return "Registrasi berhasil. Silakan cek email untuk verifikasi akun.";
  }

  public String confirmToken(String token) {

    if (token == null || token.isEmpty()) {
      return "Token tidak valid.";
    }

    String email = confirmationTokens.get(token);

    if (email == null) {
      return "Token tidak valid atau sudah digunakan.";
    }

    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isEmpty()) {
      return "User tidak ditemukan.";
    }

    User user = optionalUser.get();
    user.setEnabled(true);

    userRepository.save(user);
    confirmationTokens.remove(token);

    return "Akun berhasil diverifikasi. Silakan login.";
  }

  public User findUserByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  public void createPasswordResetTokenForUser(User user, String token) {
    if (user == null || token == null || token.isEmpty()) {
      return;
    }

    resetPasswordTokens.put(token, user.getEmail());
  }

  public String validatePasswordResetToken(String token) {
    if (token == null || token.isEmpty()) {
      return "invalidToken";
    }

    if (!resetPasswordTokens.containsKey(token)) {
      return "invalidToken";
    }

    return "valid";
  }

  public void resetPassword(String token, String newPassword) {
    if (token == null || token.isEmpty()) {
      return;
    }

    String email = resetPasswordTokens.get(token);

    if (email == null) {
      return;
    }

    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isEmpty()) {
      return;
    }

    User user = optionalUser.get();
    user.setPassword(passwordEncoder.encode(newPassword));

    userRepository.save(user);
    resetPasswordTokens.remove(token);
  }

  public User save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> findByUsernameOrEmail(String input) {
    return userRepository.findByUsernameOrEmail(input, input);
  }

  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public boolean usernameExists(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  public void updatePassword(User user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}