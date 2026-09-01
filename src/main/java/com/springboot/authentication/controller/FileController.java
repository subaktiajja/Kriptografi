package com.springboot.authentication.controller;

import com.springboot.authentication.service.AesService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
public class FileController {

  private final AesService aesService;

  public FileController(AesService aesService) {
    this.aesService = aesService;
  }

  @PostMapping("/encrypt")
  public ResponseEntity<?> encryptFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("secretKey") String secretKey) {
    try {
      if (file == null || file.isEmpty()) {
        return ResponseEntity.badRequest().body("File tidak boleh kosong");
      }

      byte[] encryptedBytes = aesService.encrypt(file.getBytes(), secretKey);

      String originalFilename = file.getOriginalFilename();
      String encryptedFilename = (originalFilename != null ? originalFilename : "file") + ".aes";

      ByteArrayResource resource = new ByteArrayResource(encryptedBytes);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encryptedFilename + "\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .contentLength(encryptedBytes.length)
          .body(resource);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Gagal mengenkripsi file: " + e.getMessage());
    }
  }

  @PostMapping("/decrypt")
  public ResponseEntity<?> decryptFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("secretKey") String secretKey) {
    try {
      if (file == null || file.isEmpty()) {
        return ResponseEntity.badRequest().body("File tidak boleh kosong");
      }

      byte[] decryptedBytes = aesService.decrypt(file.getBytes(), secretKey);

      String originalFilename = file.getOriginalFilename();
      String decryptedFilename;

      if (originalFilename != null && originalFilename.endsWith(".aes")) {
        decryptedFilename = originalFilename.substring(0, originalFilename.length() - 4);
      } else {
        decryptedFilename = "decrypted_" + (originalFilename != null ? originalFilename : "file");
      }

      ByteArrayResource resource = new ByteArrayResource(decryptedBytes);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decryptedFilename + "\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .contentLength(decryptedBytes.length)
          .body(resource);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Gagal mendekripsi file. Pastikan key benar dan file valid.");
    }
  }
}