package com.springboot.authentication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dokumen_uas")
public class DokumenMts22 {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String namaDokumen;
  private String jenis;
  private String tanggalUpload;
  private String status;

  private String filePath;

  public Long getId() {
    return id;
  }

  public String getNamaDokumen() {
    return namaDokumen;
  }

  public String getJenis() {
    return jenis;
  }

  public String getTanggalUpload() {
    return tanggalUpload;
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

  public void setNamaDokumen(String namaDokumen) {
    this.namaDokumen = namaDokumen;
  }

  public void setJenis(String jenis) {
    this.jenis = jenis;
  }

  public void setTanggalUpload(String tanggalUpload) {
    this.tanggalUpload = tanggalUpload;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}