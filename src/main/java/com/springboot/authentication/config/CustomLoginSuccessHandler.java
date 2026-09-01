package com.springboot.authentication.config;

import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;

  public CustomLoginSuccessHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    String loginInput = authentication.getName();

    User user = userRepository.findByUsernameOrEmail(loginInput, loginInput)
        .orElse(null);

    if (user == null) {
      response.sendRedirect("/login?error=true");
      return;
    }

    HttpSession session = request.getSession();
    session.setAttribute("loggedUser", user);

    if (user.getRole() == Role.ADMIN) {
      response.sendRedirect("/admin/dashboard");
    } else if (user.getRole() == Role.USER) {
      response.sendRedirect("/user/dashboard");
    } else {
      response.sendRedirect("/login?error=true");
    }
  }
}