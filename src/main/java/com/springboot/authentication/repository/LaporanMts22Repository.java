package com.springboot.authentication.repository;

import com.springboot.authentication.entity.LaporanMts22;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaporanMts22Repository extends JpaRepository<LaporanMts22, Long> {
}