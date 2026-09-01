package com.springboot.authentication.repository;

// Import entity jadwal UAS
import com.springboot.authentication.entity.JadwalUas;

// Import JpaRepository agar bisa memakai findAll(), save(), deleteById(), count(), dan lain-lain
import org.springframework.data.jpa.repository.JpaRepository;

// Import anotasi Repository
import org.springframework.stereotype.Repository;

@Repository
public interface JadwalUasRepository extends JpaRepository<JadwalUas, Long> {

  /*
   * Tidak perlu menulis query manual.
   * Karena JpaRepository sudah menyediakan method bawaan:
   *
   * findAll() = mengambil semua data jadwal
   * findById() = mencari jadwal berdasarkan id
   * save() = menyimpan atau update jadwal
   * deleteById() = menghapus jadwal berdasarkan id
   * count() = menghitung jumlah data jadwal
   */
}