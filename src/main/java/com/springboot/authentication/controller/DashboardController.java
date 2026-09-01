package com.springboot.authentication.controller;

// Import entity Role untuk membedakan ADMIN dan USER
import com.springboot.authentication.entity.Role;

// Import entity User untuk mengambil data user yang sedang login
import com.springboot.authentication.entity.User;

// Import repository untuk mengambil data dokumen dari database
import com.springboot.authentication.repository.DokumenMts22Repository;

// Import repository untuk mengambil data jadwal dari database
import com.springboot.authentication.repository.JadwalUasRepository;
import com.springboot.authentication.repository.LaporanMts22Repository;

// Import repository untuk mengambil data user dari database
import com.springboot.authentication.repository.UserRepository;

// Import session untuk mengecek user yang sedang login
import jakarta.servlet.http.HttpSession;

// Import controller Spring MVC
import org.springframework.stereotype.Controller;

// Import Model untuk mengirim data dari controller ke HTML Thymeleaf
import org.springframework.ui.Model;

// Import anotasi mapping GET
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  // Repository user digunakan untuk menghitung dan mengambil data user
  private final UserRepository userRepository;

  // Repository jadwal digunakan agar data jadwal admin bisa muncul di dashboard
  // user
  private final JadwalUasRepository jadwalUasRepository;

  // Repository dokumen digunakan untuk mengambil data dokumen
  private final DokumenMts22Repository dokumenMts22Repository;

  // Repository laporan digunakan untuk menghitung riwayat laporan yang telah dicetak
  private final LaporanMts22Repository laporanMts22Repository;

  // Constructor untuk dependency injection repository
  public DashboardController(
      UserRepository userRepository,
      JadwalUasRepository jadwalUasRepository,
      DokumenMts22Repository dokumenMts22Repository,
      LaporanMts22Repository laporanMts22Repository) {

    this.userRepository = userRepository;
    this.jadwalUasRepository = jadwalUasRepository;
    this.dokumenMts22Repository = dokumenMts22Repository;
    this.laporanMts22Repository = laporanMts22Repository;
  }

  // Method untuk menampilkan dashboard admin
  @GetMapping("/admin/dashboard")
  public String adminDashboard(HttpSession session, Model model) {

    // Mengambil data user yang sedang login dari session
    User user = (User) session.getAttribute("loggedUser");

    // Jika belum login atau rolenya bukan ADMIN, maka diarahkan ke halaman login
    if (user == null || user.getRole() != Role.ADMIN) {
      return "redirect:/login";
    }

    // Mengirim data user login ke halaman admin-dashboard.html
    model.addAttribute("user", user);

    // Mengirim jumlah user yang memiliki role USER
    model.addAttribute("jumlahUser", userRepository.countByRole(Role.USER));

    // Mengirim daftar user dengan role USER
    model.addAttribute("userTerbaru", userRepository.findByRole(Role.USER));

    // Mengirim jumlah jadwal yang ada di database
    model.addAttribute("jumlahJadwal", jadwalUasRepository.count());

    // Mengirim data jadwal ke dashboard admin
    model.addAttribute("jadwalTerbaru", jadwalUasRepository.findAll());

    // Mengirim jumlah dokumen yang ada di database
    model.addAttribute("jumlahDokumen", dokumenMts22Repository.count());

    // Mengirim jumlah riwayat laporan yang telah berhasil dicetak
    model.addAttribute("jumlahLaporan", laporanMts22Repository.count());

    // Menampilkan file templates/admin-dashboard.html
    return "admin-dashboard";
  }

  // Method untuk menampilkan dashboard user
  @GetMapping("/user/dashboard")
  public String userDashboard(HttpSession session, Model model) {

    // Mengambil data user yang sedang login dari session
    User user = (User) session.getAttribute("loggedUser");

    // Jika belum login atau rolenya bukan USER, maka diarahkan ke halaman login
    if (user == null || user.getRole() != Role.USER) {
      return "redirect:/login";
    }

    // Mengirim data user login ke halaman user-dashboard.html
    model.addAttribute("user", user);

    /*
     * INI BAGIAN PENTING
     * Data jadwal diambil dari tabel jadwal_uas.
     * Jadi kalau admin tambah atau update jadwal,
     * user akan otomatis melihat data terbaru di dashboard.
     */
    model.addAttribute("jadwalTerbaru", jadwalUasRepository.findAll());

    /*
     * Attribute tambahan ini dibuat agar aman.
     * Kalau di HTML menggunakan ${jadwalList},
     * datanya tetap akan terbaca.
     */
    model.addAttribute("jadwalList", jadwalUasRepository.findAll());

    // Mengirim data dokumen ke dashboard user jika dibutuhkan
    model.addAttribute("dokumenList", dokumenMts22Repository.findAll());

    // Menampilkan file templates/user-dashboard.html
    return "user-dashboard";
  }
}