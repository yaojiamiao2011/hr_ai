package com.example.hrai.controller;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.service.AIModelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/default")
    @Operation(summary = "Get default AI model configuration")
    public ResponseEntity<AIModelConfig> getDefaultConfig() {
        Optional<AIModelConfig> config = aiModelConfigService.getDefaultConfig();
        return config.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/set-default/{id}")
    @Operation(summary = "Set an AI model configuration as default")
    public ResponseEntity<AIModelConfig> setDefaultConfig(@PathVariable Long id) {
        try {
            AIModelConfig updatedConfig = aiModelConfigService.setDefaultConfig(id);
            return ResponseEntity.ok(updatedConfig);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/test/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Test an AI model configuration")
    public ResponseEntity<?> testConfig(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            // 获取配置信息
            Optional<AIModelConfig> configOpt = aiModelConfigService.getConfigById(id);
            if (!configOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            AIModelConfig config = configOpt.get();
            String prompt = request.getOrDefault("prompt", "");

            // 处理中文字符编码
            if (prompt != null) {
                byte[] bytes = prompt.getBytes(StandardCharsets.UTF_8);
                prompt = new String(bytes, StandardCharsets.UTF_8);
            }

            if (!config.getIsActive()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Configuration is not active",
                    "message", "该模型配置未启用，无法测试"
                ));
            }

            // 调用AI服务进行测试
            String response = aiModelConfigService.testModel(config, prompt);

            return ResponseEntity.ok(Map.of(
                "content", response,
                "model", config.getModelName(),
                "provider", config.getProvider(),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Test failed",
                "message", e.getMessage()
            ));
        }
    }
}