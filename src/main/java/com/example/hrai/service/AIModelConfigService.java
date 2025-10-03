package com.example.hrai.service;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.repository.AIModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AIModelConfigService {

    @Autowired
    private AIModelConfigRepository aiModelConfigRepository;

    public List<AIModelConfig> getAllConfigs() {
        return aiModelConfigRepository.findAll();
    }

    public Optional<AIModelConfig> getConfigById(Long id) {
        return aiModelConfigRepository.findById(id);
    }

    public AIModelConfig saveConfig(AIModelConfig config) {
        return aiModelConfigRepository.save(config);
    }

    public AIModelConfig updateConfig(Long id, AIModelConfig configDetails) {
        AIModelConfig config = aiModelConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Config not found for id: " + id));

        config.setModelName(configDetails.getModelName());
        config.setProvider(configDetails.getProvider());
        config.setApiKey(configDetails.getApiKey());
        config.setModelEndpoint(configDetails.getModelEndpoint());
        config.setIsActive(configDetails.getIsActive());

        return aiModelConfigRepository.save(config);
    }

    public void deleteConfig(Long id) {
        AIModelConfig config = aiModelConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Config not found for id: " + id));
        aiModelConfigRepository.delete(config);
    }

    public List<AIModelConfig> getActiveConfigs() {
        return aiModelConfigRepository.findByIsActiveTrue();
    }

    public Optional<AIModelConfig> getConfigByModelName(String modelName) {
        return aiModelConfigRepository.findByModelName(modelName);
    }
}