package com.springboot.authentication.controller;

import com.springboot.authentication.entity.DokumenMts22;
import com.springboot.authentication.entity.Role;
import com.springboot.authentication.entity.User;
import com.springboot.authentication.repository.DokumenMts22Repository;
import com.springboot.authentication.repository.JadwalUasRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UserPageController {

  /*
   * Repository jadwal dipakai untuk mengambil data jadwal UAS.
   * Data ini sama dengan data yang dikelola admin.
   */
  private final JadwalUasRepository jadwalUasRepository;

  /*
   * Repository dokumen dipakai untuk mengambil data dokumen dari database.
   * Di tampilan user, dokumen ini kita tampilkan sebagai Informasi UAS.
   */
  private final DokumenMts22Repository dokumenMts22Repository;

  /*
   * Constructor injection.
   * Spring Boot akan otomatis memasukkan repository yang dibutuhkan.
   */
  public UserPageController(
      JadwalUasRepository jadwalUasRepository,
      DokumenMts22Repository dokumenMts22Repository) {

    this.jadwalUasRepository = jadwalUasRepository;
    this.dokumenMts22Repository = dokumenMts22Repository;
  }

  /*
   * Method bantuan untuk mengecek apakah yang login adalah USER.
   */
  private boolean isUser(HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    return user != null && user.getRole() == Role.USER;
  }

  /*
   * Halaman profil user.
   */
  @GetMapping("/user/profil")
  public String profil(HttpSession session, Model model) {
    if (!isUser(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Profil Saya");
    model.addAttribute("description", "Halaman untuk melihat informasi profil pengguna.");
    model.addAttribute("page", "profil");

    return "user-page";
  }

  /*
   * Halaman jadwal UAS user.
   * Data jadwal diambil dari tabel yang sama dengan admin.
   */
  @GetMapping("/user/jadwal")
  public String jadwal(HttpSession session, Model model) {
    if (!isUser(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Jadwal UAS");
    model.addAttribute("description", "Halaman untuk melihat jadwal UAS user.");
    model.addAttribute("page", "jadwal");

    /*
     * Data jadwal dari admin dikirim ke user-page.html.
     */
    model.addAttribute("jadwalList", jadwalUasRepository.findAll());

    return "user-page";
  }

  /*
   * Halaman informasi sekolah.
   */
  @GetMapping("/user/informasi")
  public String informasi(HttpSession session, Model model) {
    if (!isUser(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Informasi Sekolah");
    model.addAttribute("description", "Halaman untuk melihat informasi dan pengumuman MTSN 22 JAKARTA.");
    model.addAttribute("page", "informasi");

    return "user-page";
  }

  /*
   * Halaman dokumen user.
   * Route tetap /user/dokumen, tapi tampilannya kita jadikan Informasi UAS.
   */
  @GetMapping("/user/dokumen")
  public String dokumen(HttpSession session, Model model) {
    if (!isUser(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));

    /*
     * Judul diganti menjadi Informasi UAS.
     * Fitur tetap memakai data dokumen lama.
     */
    model.addAttribute("title", "Informasi UAS");
    model.addAttribute("description", "Halaman untuk melihat dan mengunduh informasi terkait UAS.");
    model.addAttribute("page", "dokumen");

    /*
     * INI PENTING:
     * Data dokumen harus dikirim ke HTML.
     * Kalau tidak ada ini, tabel user tidak bisa menampilkan data dari admin.
     */
    model.addAttribute("dokumenList", dokumenMts22Repository.findAll());

    return "user-page";
  }

  /*
   * DOWNLOAD DOKUMEN UNTUK USER
   * Ini yang sebelumnya belum ada.
   * Karena tombol user mengarah ke /user/dokumen/download/{id},
   * maka route ini wajib dibuat.
   */
  @GetMapping("/user/dokumen/download/{id}")
  public ResponseEntity<Resource> downloadDokumenUser(
      @PathVariable Long id,
      HttpSession session) {

    /*
     * Jika yang akses bukan USER, maka tidak boleh download.
     */
    if (!isUser(session)) {
      return ResponseEntity.status(403).build();
    }

    try {
      /*
       * Cari dokumen berdasarkan id dari database.
       */
      DokumenMts22 dokumen = dokumenMts22Repository.findById(id)
          .orElseThrow(() -> new RuntimeException("Dokumen tidak ditemukan"));

      /*
       * Ambil lokasi file yang tersimpan di database.
       * Field ini berasal dari DokumenMts22.getFilePath().
       */
      Path path = Paths.get(dokumen.getFilePath());

      /*
       * Ubah path file menjadi Resource agar bisa dikirim sebagai download.
       */
      Resource resource = new UrlResource(path.toUri());

      /*
       * Kalau file fisiknya tidak ada di folder uploads/dokumen,
       * maka kembalikan not found.
       */
      if (!resource.exists()) {
        return ResponseEntity.notFound().build();
      }

      /*
       * Kirim file sebagai attachment.
       * Nama file download memakai namaDokumen dari database.
       */
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dokumen.getNamaDokumen() + "\"")
          .body(resource);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }

  /*
   * Halaman enkripsi file user.
   */
  @GetMapping("/user/enkripsi")
  public String enkripsi(HttpSession session, Model model) {
    if (!isUser(session)) {
      return "redirect:/login";
    }

    model.addAttribute("user", session.getAttribute("loggedUser"));
    model.addAttribute("title", "Enkripsi File");
    model.addAttribute("description", "Halaman untuk enkripsi dan dekripsi file.");

    return "user-file-security";
  }
}