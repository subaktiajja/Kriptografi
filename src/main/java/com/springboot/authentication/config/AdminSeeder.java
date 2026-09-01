package com.springboot.authentication.config;

import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    boolean adminExists = userRepository.findByUsername("admin").isPresent();

    if (!adminExists) {
      User admin = new User();
      admin.setFirstName("I Wayan");
      admin.setLastName("Adi");
      admin.setUsername("admin");
      admin.setEmail("admin@gmail.com");
      admin.setPassword(passwordEncoder.encode("Admin123"));
      admin.setRole(Role.ADMIN);
      admin.setEnabled(true);
      admin.setLocked(false);

      userRepository.save(admin);

      System.out.println("Admin berhasil dibuat");
      System.out.println("Username: admin");
      System.out.println("Password: Admin123");
    } else {
      System.out.println("Admin sudah ada, tidak dibuat ulang");
    }
  }
}