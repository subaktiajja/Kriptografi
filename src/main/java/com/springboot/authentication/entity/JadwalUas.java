package com.springboot.authentication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jadwal_uas")
public class JadwalUas {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Hari pelaksanaan UAS, contoh: Senin.
  private String hari;

  // Mata pelajaran yang diujikan, contoh: Matematika.
  @Column(name = "mata_pelajaran")
  private String mataPelajaran;

  // Kelas peserta UAS, contoh: IX A.
  private String kelas;

  // Waktu pelaksanaan UAS, contoh: 07:30 - 09:00.
  private String jam;

  // Ruang pelaksanaan UAS, contoh: Ruang 1.
  private String tempat;

  public Long getId() {
    return id;
  }

  public String getHari() {
    return hari;
  }

  public String getMataPelajaran() {
    return mataPelajaran;
  }

  public String getKelas() {
    return kelas;
  }

  public String getJam() {
    return jam;
  }

  public String getTempat() {
    return tempat;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setHari(String hari) {
    this.hari = hari;
  }

  public void setMataPelajaran(String mataPelajaran) {
    this.mataPelajaran = mataPelajaran;
  }

  public void setKelas(String kelas) {
    this.kelas = kelas;
  }

  public void setJam(String jam) {
    this.jam = jam;
  }

  public void setTempat(String tempat) {
    this.tempat = tempat;
  }
}
