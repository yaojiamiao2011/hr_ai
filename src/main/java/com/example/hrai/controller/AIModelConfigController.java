package com.example.hrai.controller;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.service.AIModelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai-config")
@Tag(name = "AI Model Configuration", description = "APIs for managing AI model configurations")
public class AIModelConfigController {

    @Autowired
    private AIModelConfigService aiModelConfigService;

    @GetMapping
    @Operation(summary = "Get all AI model configurations")
    public List<AIModelConfig> getAllConfigs() {
        return aiModelConfigService.getAllConfigs();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get AI model configuration by ID")
    public ResponseEntity<AIModelConfig> getConfigById(@PathVariable Long id) {
        Optional<AIModelConfig> config = aiModelConfigService.getConfigById(id);
        return config.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new AI model configuration")
    public AIModelConfig createConfig(@RequestBody AIModelConfig config) {
        return aiModelConfigService.saveConfig(config);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing AI model configuration")
    public ResponseEntity<AIModelConfig> updateConfig(@PathVariable Long id, @RequestBody AIModelConfig configDetails) {
        try {
            AIModelConfig updatedConfig = aiModelConfigService.updateConfig(id, configDetails);
            return ResponseEntity.ok(updatedConfig);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an AI model configuration")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        try {
            aiModelConfigService.deleteConfig(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active AI model configurations")
    public List<AIModelConfig> getActiveConfigs() {
        return aiModelConfigService.getActiveConfigs();
    }
}