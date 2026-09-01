package com.springboot.authentication.controller;

import com.springboot.authentication.entity.User;
import com.springboot.authentication.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {

  private final UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String loginPage(Model model) {
    return "login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("user", new User());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute User user, Model model) {
    String message = userService.registerUser(user);
    model.addAttribute("message", message);
    return "login";
  }

  @GetMapping("/register/confirmToken")
  public String confirmToken(@RequestParam("token") String token, Model model) {
    String message = userService.confirmToken(token);
    model.addAttribute("message", message);
    return "login";
  }
}