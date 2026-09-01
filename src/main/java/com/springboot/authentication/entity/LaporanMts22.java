package com.springboot.authentication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "laporan_mts22")
public class LaporanMts22 {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String namaLaporan;
  private String periode;
  private String tanggalDibuat;
  private String jenis;
  private String status;
  private String filePath;

  public LaporanMts22() {
  }

  public Long getId() {
    return id;
  }

  public String getNamaLaporan() {
    return namaLaporan;
  }

  public String getPeriode() {
    return periode;
  }

  public String getTanggalDibuat() {
    return tanggalDibuat;
  }

  public String getJenis() {
    return jenis;
  }

  public String getStatus() {
    return status;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setNamaLaporan(String namaLaporan) {
    this.namaLaporan = namaLaporan;
  }

  public void setPeriode(String periode) {
    this.periode = periode;
  }

  public void setTanggalDibuat(String tanggalDibuat) {
    this.tanggalDibuat = tanggalDibuat;
  }

  public void setJenis(String jenis) {
    this.jenis = jenis;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}