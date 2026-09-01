package com.springboot.authentication.repository;

import com.springboot.authentication.entity.DokumenMts22;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumenMts22Repository extends JpaRepository<DokumenMts22, Long> {
}