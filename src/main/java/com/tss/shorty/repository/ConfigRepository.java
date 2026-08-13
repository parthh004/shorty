package com.tss.shorty.repository;

import com.tss.shorty.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConfigRepository extends JpaRepository<SystemConfig, String> {
}
