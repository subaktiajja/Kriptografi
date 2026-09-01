package com.springboot.authentication.controller;

import com.springboot.authentication.entity.DokumenMts22;
import com.springboot.authentication.entity.JadwalUas;
import com.springboot.authentication.entity.LaporanMts22;
import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.DokumenMts22Repository;
import com.springboot.authentication.repository.JadwalUasRepository;
import com.springboot.authentication.repository.LaporanMts22Repository;
import com.springboot.authentication.repository.UserRepository;
import com.springboot.authentication.service.PdfReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Controller
public class AdminPageController {

  private final JadwalUasRepository jadwalUasRepository;
  private final DokumenMts22Repository dokumenMts22Repository;
  private final UserRepository userRepository;
  private final PdfReportService pdfReportService;
  private final LaporanMts22Repository laporanMts22Repository;

  private final String dokumenUploadDir = "uploads/dokumen";

  public AdminPageController(
      JadwalUasRepository jadwalUasRepository,
      DokumenMts22Repository dokumenMts22Repository,
      UserRepository userRepository,
      PdfReportService pdfReportService,
      LaporanMts22Repository laporanMts22Repository) {

    this.jadwalUasRepository = jadwalUasRepository;
    this.dokumenMts22Repository = dokumenMts22Repository;
    this.userRepository = userRepository;
    this.pdfReportService = pdfReportService;
    this.laporanMts22Repository = laporanMts22Repository;
  }

  private boolean isAdmin(HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    return user != null && user.getRole() == Role.ADMIN;
  }

  // =========================
  // HALAMAN DATA USER
  // =========================
  @GetMapping("/admin/user")
  public String dataUser(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Data User");
    model.addAttribute("description", "Data user yang telah terdaftar pada sistem.");
    model.addAttribute("page", "user");
    model.addAttribute("userList", userRepository.findByRole(Role.USER));

    return "admin-page";
  }

  // =========================
  // HALAMAN JADWAL
  // =========================
  @GetMapping("/admin/jadwal")
  public String jadwal(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Jadwal UAS");
    model.addAttribute("description", "Halaman untuk mengelola jadwal UAS.");
    model.addAttribute("page", "jadwal");
    model.addAttribute("jadwalList", jadwalUasRepository.findAll());

    return "admin-page";
  }

  @GetMapping("/admin/jadwal/tambah")
  public String tambahJadwal(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Tambah Jadwal UAS");
    model.addAttribute("description", "Form untuk menambahkan jadwal UAS.");
    model.addAttribute("page", "form-jadwal");
    model.addAttribute("jadwal", new JadwalUas());

    return "admin-page";
  }

  @GetMapping("/admin/jadwal/edit/{id}")
  public String editJadwal(
      @PathVariable Long id,
      HttpSession session,
      Model model) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    JadwalUas jadwal = jadwalUasRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Data jadwal tidak ditemukan dengan id: " + id));

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Edit Jadwal UAS");
    model.addAttribute("description", "Form untuk mengubah data jadwal UAS.");
    model.addAttribute("page", "form-jadwal");
    model.addAttribute("jadwal", jadwal);

    return "admin-page";
  }

  @PostMapping("/admin/jadwal/simpan")
  public String simpanJadwal(
      HttpSession session,
      @ModelAttribute("jadwal") JadwalUas jadwal) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    jadwalUasRepository.save(jadwal);

    return "redirect:/admin/jadwal";
  }

  @GetMapping("/admin/jadwal/hapus/{id}")
  public String hapusJadwal(
      @PathVariable Long id,
      HttpSession session) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    jadwalUasRepository.deleteById(id);

    return "redirect:/admin/jadwal";
  }

  // =========================
  // HALAMAN DOKUMEN
  // =========================
  @GetMapping("/admin/dokumen")
  public String dokumen(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Dokumen");
    model.addAttribute("description", "Halaman untuk mengelola dokumen MTSN 22 JAKARTA.");
    model.addAttribute("page", "dokumen");
    model.addAttribute("dokumenList", dokumenMts22Repository.findAll());

    return "admin-page";
  }

  @GetMapping("/admin/dokumen/tambah")
  public String tambahDokumen(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Upload Dokumen");
    model.addAttribute("description", "Form untuk mengupload dokumen MTSN 22.");
    model.addAttribute("page", "form-dokumen");
    model.addAttribute("dokumen", new DokumenMts22());

    return "admin-page";
  }

  @GetMapping("/admin/dokumen/edit/{id}")
  public String editDokumen(
      @PathVariable Long id,
      HttpSession session,
      Model model) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    DokumenMts22 dokumen = dokumenMts22Repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Dokumen tidak ditemukan dengan id: " + id));

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Edit Dokumen");
    model.addAttribute("description", "Form untuk mengubah data dokumen.");
    model.addAttribute("page", "form-dokumen");
    model.addAttribute("dokumen", dokumen);

    return "admin-page";
  }

  @PostMapping("/admin/dokumen/simpan")
  public String simpanDokumen(
      HttpSession session,
      @ModelAttribute("dokumen") DokumenMts22 dokumen,
      @RequestParam("file") MultipartFile file) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    try {
      Files.createDirectories(Paths.get(dokumenUploadDir));

      if (dokumen.getId() != null) {
        DokumenMts22 dokumenLama = dokumenMts22Repository.findById(dokumen.getId())
            .orElseThrow(() -> new RuntimeException("Dokumen tidak ditemukan"));

        dokumenLama.setNamaDokumen(dokumen.getNamaDokumen());
        dokumenLama.setJenis(dokumen.getJenis());
        dokumenLama.setStatus(dokumen.getStatus());

        if (file != null && !file.isEmpty()) {
          String originalFilename = file.getOriginalFilename();
          String fileName = System.currentTimeMillis() + "_" + originalFilename;
          Path filePath = Paths.get(dokumenUploadDir, fileName);

          Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

          if (dokumenLama.getFilePath() != null && !dokumenLama.getFilePath().isBlank()) {
            Files.deleteIfExists(Paths.get(dokumenLama.getFilePath()));
          }

          dokumenLama.setFilePath(filePath.toString());

          if (dokumenLama.getJenis() == null || dokumenLama.getJenis().isBlank()) {
            dokumenLama.setJenis(getFileExtension(originalFilename));
          }
        }

        dokumenMts22Repository.save(dokumenLama);
      } else {
        if (file == null || file.isEmpty()) {
          return "redirect:/admin/dokumen/tambah";
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(dokumenUploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        if (dokumen.getNamaDokumen() == null || dokumen.getNamaDokumen().isBlank()) {
          dokumen.setNamaDokumen(originalFilename);
        }

        if (dokumen.getJenis() == null || dokumen.getJenis().isBlank()) {
          dokumen.setJenis(getFileExtension(originalFilename));
        }

        dokumen.setTanggalUpload(LocalDate.now().toString());
        dokumen.setStatus("Tersedia");
        dokumen.setFilePath(filePath.toString());

        dokumenMts22Repository.save(dokumen);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return "redirect:/admin/dokumen";
  }

  @GetMapping("/admin/dokumen/download/{id}")
  public ResponseEntity<Resource> downloadDokumen(
      @PathVariable Long id,
      HttpSession session) {

    if (!isAdmin(session)) {
      return ResponseEntity.status(403).build();
    }

    try {
      DokumenMts22 dokumen = dokumenMts22Repository.findById(id)
          .orElseThrow(() -> new RuntimeException("Dokumen tidak ditemukan"));

      Path path = Paths.get(dokumen.getFilePath());
      Resource resource = new UrlResource(path.toUri());

      if (!resource.exists()) {
        return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dokumen.getNamaDokumen() + "\"")
          .body(resource);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/admin/dokumen/hapus/{id}")
  public String hapusDokumen(
      @PathVariable Long id,
      HttpSession session) {

    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    try {
      DokumenMts22 dokumen = dokumenMts22Repository.findById(id)
          .orElseThrow(() -> new RuntimeException("Dokumen tidak ditemukan"));

      if (dokumen.getFilePath() != null && !dokumen.getFilePath().isBlank()) {
        Files.deleteIfExists(Paths.get(dokumen.getFilePath()));
      }

      dokumenMts22Repository.deleteById(id);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return "redirect:/admin/dokumen";
  }

  // =========================
  // HALAMAN LAPORAN
  // =========================
  @GetMapping("/admin/laporan")
  public String laporan(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Laporan");
    model.addAttribute("description", "Pilih jenis laporan untuk dibuat otomatis dari data yang tersimpan di database.");
    model.addAttribute("page", "laporan");

    model.addAttribute("jumlahUser", userRepository.countByRole(Role.USER));
    model.addAttribute("jumlahJadwal", jadwalUasRepository.count());

    return "admin-page";
  }

  @GetMapping("/admin/laporan/user/pdf")
  public ResponseEntity<byte[]> cetakLaporanUser(HttpSession session) throws IOException {
    if (!isAdmin(session)) {
      return ResponseEntity.status(403).build();
    }

    String filename = "laporan-data-user.pdf";
    byte[] pdf = pdfReportService.generateUserReport(userRepository.findByRole(Role.USER));
    simpanRiwayatLaporan("Laporan Data User", "DATA_USER", filename);
    return pdfResponse(pdf, filename);
  }

  @GetMapping("/admin/laporan/jadwal/pdf")
  public ResponseEntity<byte[]> cetakLaporanJadwal(HttpSession session) throws IOException {
    if (!isAdmin(session)) {
      return ResponseEntity.status(403).build();
    }

    String filename = "laporan-jadwal-uas.pdf";
    byte[] pdf = pdfReportService.generateJadwalReport(jadwalUasRepository.findAll());
    simpanRiwayatLaporan("Laporan Jadwal UAS", "JADWAL_UAS", filename);
    return pdfResponse(pdf, filename);
  }

  private void simpanRiwayatLaporan(String namaLaporan, String jenis, String filename) {
    LaporanMts22 laporan = new LaporanMts22();
    laporan.setNamaLaporan(namaLaporan);
    laporan.setJenis(jenis);
    laporan.setPeriode(String.valueOf(LocalDate.now().getYear()));
    laporan.setTanggalDibuat(LocalDate.now().toString());
    laporan.setStatus("Berhasil");
    laporan.setFilePath(filename);
    laporanMts22Repository.save(laporan);
  }

  private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
        .body(pdf);
  }

  // =========================
  // ENKRIPSI FILE
  // =========================
  @GetMapping("/admin/enkripsi")
  public String enkripsi(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Enkripsi File");
    model.addAttribute("description", "Halaman untuk mengenkripsi file menggunakan AES.");
    model.addAttribute("mode", "encrypt");

    return "admin-file-security";
  }

  // =========================
  // DEKRIPSI FILE
  // =========================
  @GetMapping("/admin/dekripsi")
  public String dekripsi(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Dekripsi File");
    model.addAttribute("description", "Halaman untuk mendekripsi file menggunakan AES.");
    model.addAttribute("mode", "decrypt");

    return "admin-file-security";
  }

  // =========================
  // PENGATURAN
  // =========================
  @GetMapping("/admin/pengaturan")
  public String pengaturan(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Pengaturan");
    model.addAttribute("description", "Halaman untuk mengatur konfigurasi sistem.");
    model.addAttribute("page", "pengaturan");

    return "admin-page";
  }

  // =========================
  // AMBIL EKSTENSI FILE
  // =========================
  private String getFileExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "FILE";
    }

    return filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
  }
}