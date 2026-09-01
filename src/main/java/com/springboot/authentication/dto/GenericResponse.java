package com.springboot.authentication.dto;

public class GenericResponse {

  private String message;

  public GenericResponse() {
  }

  public GenericResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}