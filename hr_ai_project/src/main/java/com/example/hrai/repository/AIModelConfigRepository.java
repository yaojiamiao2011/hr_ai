package com.example.hrai.repository;

import com.example.hrai.entity.AIModelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIModelConfigRepository extends JpaRepository<AIModelConfig, Long> {
    List<AIModelConfig> findByIsActiveTrue();
    Optional<AIModelConfig> findByModelName(String modelName);
    List<AIModelConfig> findByProvider(String provider);
    Optional<AIModelConfig> findByIsDefaultTrue();
    List<AIModelConfig> findByIsDefault(Boolean isDefault);
}