package com.example.hrai.controller;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.service.AIModelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mcp")
@Tag(name = "Model Configuration Protocol", description = "APIs for testing AI model configurations using MCP")
public class MCPController {

    @Autowired
    private AIModelConfigService aiModelConfigService;

    @GetMapping("/configs")
    @Operation(summary = "Get all AI model configurations (MCP)")
    public List<AIModelConfig> getAllConfigs() {
        return aiModelConfigService.getAllConfigs();
    }

    @GetMapping("/configs/active")
    @Operation(summary = "Get all active AI model configurations (MCP)")
    public List<AIModelConfig> getActiveConfigs() {
        return aiModelConfigService.getActiveConfigs();
    }

    @PostMapping("/test/{id}")
    @Operation(summary = "Test an AI model configuration (MCP)")
    public ResponseEntity<?> testConfig(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            // 获取配置信息
            Optional<AIModelConfig> configOpt = aiModelConfigService.getConfigById(id);
            if (!configOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            AIModelConfig config = configOpt.get();
            String prompt = request.getOrDefault("prompt", "Hello, world!");

            // 调用AI服务进行测试
            String response = aiModelConfigService.testModel(config, prompt);

            return ResponseEntity.ok(Map.of(
                "modelId", config.getId(),
                "modelName", config.getModelName(),
                "provider", config.getProvider(),
                "endpoint", config.getModelEndpoint(),
                "prompt", prompt,
                "response", response,
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Test failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/test-all-active")
    @Operation(summary = "Test all active AI model configurations (MCP)")
    public ResponseEntity<?> testAllActiveConfigs(@RequestBody Map<String, String> request) {
        try {
            List<AIModelConfig> activeConfigs = aiModelConfigService.getActiveConfigs();
            if (activeConfigs.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No active configurations found",
                    "message", "没有找到启用的模型配置"
                ));
            }

            String prompt = request.getOrDefault("prompt", "Hello, world!");
            StringBuilder results = new StringBuilder();

            for (AIModelConfig config : activeConfigs) {
                try {
                    String response = aiModelConfigService.testModel(config, prompt);
                    results.append(String.format("[%s] %s: %s\n",
                        config.getProvider(), config.getModelName(), response));
                } catch (Exception e) {
                    results.append(String.format("[%s] %s: ERROR - %s\n",
                        config.getProvider(), config.getModelName(), e.getMessage()));
                }
            }

            return ResponseEntity.ok(Map.of(
                "prompt", prompt,
                "results", results.toString(),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Test failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/test-video-summary")
    @Operation(summary = "Test video summary generation (MCP)")
    public ResponseEntity<?> testVideoSummary(@RequestBody Map<String, Object> request) {
        try {
            // Handle both String and other object types for content
            String videoContent = "这是一段视频内容的示例文本。";
            Object contentObj = request.get("content");
            if (contentObj != null) {
                videoContent = contentObj.toString();
            }
            String prompt = "请根据以下视频内容生成一个提纲摘要，以HTML格式返回，要求包含主要内容和关键要点：\n\n" + videoContent;

            // 获取默认AI模型配置
            Optional<AIModelConfig> defaultConfigOpt = aiModelConfigService.getDefaultConfig();
            if (!defaultConfigOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No default model configured",
                    "message", "未设置默认大模型配置"
                ));
            }

            AIModelConfig defaultConfig = defaultConfigOpt.get();

            // 调用AI服务生成摘要
            String summary = aiModelConfigService.testModel(defaultConfig, prompt);

            return ResponseEntity.ok(Map.of(
                "content", videoContent,
                "prompt", prompt,
                "summary", summary,
                "model", defaultConfig.getModelName(),
                "provider", defaultConfig.getProvider(),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video summary test failed",
                "message", e.getMessage()
            ));
        }
    }
}