package com.springboot.authentication.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Async
  public void sendSimpleMail(String to, String token) {
    try {
      String confirmationLink = "http://localhost:8080/register/confirmToken?token=" + token;

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setFrom("mishashafa13@gmail.com");
      message.setSubject("Confirm your email");
      message.setText(
          "Hello from Awesome App Team!\n\n" +
              "Please click the following link to verify your email:\n\n" +
              confirmationLink);

      System.out.println("EMAIL TO   : " + to);
      System.out.println("TOKEN      : " + token);
      System.out.println("LINK EMAIL : " + confirmationLink);

      mailSender.send(message);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to send email: " + e.getMessage());
    }
  }

  @Async
  public void sendResetPasswordMail(String to, String token) {
    try {
      String resetLink = "http://localhost:8080/reset-password?token=" + token;

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setFrom("mishashafa13@gmail.com");
      message.setSubject("Reset Password");
      message.setText(
          "Halo,\n\n" +
              "Silakan klik link berikut untuk reset password akun Anda:\n\n" +
              resetLink + "\n\n" +
              "Link ini berlaku selama 30 menit.\n\n" +
              "Jika Anda tidak meminta reset password, abaikan email ini.");

      System.out.println("RESET EMAIL TO : " + to);
      System.out.println("RESET TOKEN    : " + token);
      System.out.println("RESET LINK     : " + resetLink);

      mailSender.send(message);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to send reset password email: " + e.getMessage());
    }
  }
}