package com.springboot.authentication.controller;

import com.springboot.authentication.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {

  private final PasswordResetService passwordResetService;

  public ForgotPasswordController(PasswordResetService passwordResetService) {
    this.passwordResetService = passwordResetService;
  }

  @GetMapping("/forgot-password")
  public String forgotPasswordPage() {
    return "forgotPassword";
  }

  @PostMapping("/forgot-password")
  public String processForgotPassword(
      @RequestParam("email") String email,
      Model model) {
    try {
      passwordResetService.createResetTokenAndSendEmail(email);
      model.addAttribute("success", "Link reset password berhasil dikirim ke email Anda.");
    } catch (RuntimeException e) {
      model.addAttribute("error", e.getMessage());
    }

    return "forgotPassword";
  }

  @GetMapping("/reset-password")
  public String resetPasswordPage(
      @RequestParam("token") String token,
      Model model) {
    model.addAttribute("token", token);
    return "resetPassword";
  }

  @PostMapping("/reset-password")
  public String processResetPassword(
      @RequestParam("token") String token,
      @RequestParam("newPassword") String newPassword,
      @RequestParam("confirmPassword") String confirmPassword,
      Model model) {
    try {
      passwordResetService.resetPassword(token, newPassword, confirmPassword);

      model.addAttribute("success", "Password berhasil diubah. Silakan login menggunakan password baru.");
      return "login";

    } catch (RuntimeException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("token", token);
      return "resetPassword";
    }
  }
}