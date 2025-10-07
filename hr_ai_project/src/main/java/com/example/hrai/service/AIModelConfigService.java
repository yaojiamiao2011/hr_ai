package com.example.hrai.service;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.repository.AIModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIModelConfigService {

    @Autowired
    private AIModelConfigRepository aiModelConfigRepository;

    private RestTemplate restTemplate = new RestTemplate();

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

    public Optional<AIModelConfig> getDefaultConfig() {
        return aiModelConfigRepository.findByIsDefaultTrue();
    }

    public AIModelConfig setDefaultConfig(Long id) {
        // 首先将所有配置的isDefault设置为false
        List<AIModelConfig> allConfigs = aiModelConfigRepository.findAll();
        for (AIModelConfig config : allConfigs) {
            config.setIsDefault(false);
        }
        aiModelConfigRepository.saveAll(allConfigs);

        // 然后将指定ID的配置设置为默认
        AIModelConfig config = aiModelConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Config not found for id: " + id));
        config.setIsDefault(true);
        return aiModelConfigRepository.save(config);
    }

 @SuppressWarnings("unchecked")
    public String testModel(AIModelConfig config, String prompt) {
        try {
            Map<String, Object> requestBody;

            // 根据不同的提供商构建不同的请求体
            if (config.getProvider().equalsIgnoreCase("ModelScope")) {
                // ModelScope需要不同的请求格式
                requestBody = Map.of(
                    "model", config.getModelName(),
                    "messages", List.of(
                        Map.of(
                            "role", "user",
                            "content", prompt
                        )
                    )
                );
            } else {
                // 其他提供商使用原始格式
                requestBody = Map.of(
                    "model", config.getModelName(),
                    "messages", Map.of(
                        "role", "user",
                        "content", prompt
                    )
                );
            }

            // 根据不同的提供商采用不同的调用方式
            if (config.getProvider().equalsIgnoreCase("ModelScope")) {
                return callModelScopeAPI(config.getModelEndpoint(), config.getApiKey(), requestBody);
            } else if (config.getProvider().equalsIgnoreCase("OpenAI")) {
                return callOpenAIAPI(config.getModelEndpoint(), config.getApiKey(), requestBody);
            } else if (config.getProvider().equalsIgnoreCase("Ollama")) {
                return callOllamaAPI(config.getModelEndpoint(), config.getApiKey(), requestBody);
            } else {
                throw new UnsupportedOperationException("Unsupported provider: " + config.getProvider());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to test model: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String callModelScopeAPI(String endpoint, String apiKey, Map<String, Object> requestBody) {
        try {
            // 设置请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

            // 发送请求
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(
                endpoint, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object choices = responseBody.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Map<String, Object> firstChoice = (Map<String, Object>) ((java.util.List<?>) choices).get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }

            return "API返回格式异常，无法解析响应内容";
        } catch (Exception e) {
            throw new RuntimeException("ModelScope API调用失败: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String callOpenAIAPI(String endpoint, String apiKey, Map<String, Object> requestBody) {
        try {
            // 设置请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

            // 发送请求
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(
                endpoint, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object choices = responseBody.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Map<String, Object> firstChoice = (Map<String, Object>) ((java.util.List<?>) choices).get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }

            return "API返回格式异常，无法解析响应内容";
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API调用失败: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String callOllamaAPI(String endpoint, String apiKey, Map<String, Object> requestBody) {
        try {
            // 设置请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");

            // 如果有API密钥，则添加到请求头
            if (apiKey != null && !apiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + apiKey);
            }

            // 构建Ollama API请求体
            Map<String, Object> ollamaRequestBody = new java.util.HashMap<>(requestBody);

            // 获取原始消息并转换为Ollama格式
            Object messages = ollamaRequestBody.get("messages");
            if (messages instanceof Map) {
                // 将单个消息对象转换为消息数组
                java.util.List<Map<String, Object>> messagesList = new java.util.ArrayList<>();
                messagesList.add((Map<String, Object>) messages);
                ollamaRequestBody.put("messages", messagesList);
            }

            // 添加stream=false参数以获得单个JSON响应而非NDJSON流
            ollamaRequestBody.put("stream", false);

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(ollamaRequestBody, headers);

            // 发送请求
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(
                endpoint, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // Ollama API响应格式
                Object message = responseBody.get("message");
                if (message instanceof Map) {
                    return (String) ((Map<String, Object>) message).get("content");
                }
            }

            return "API返回格式异常，无法解析响应内容";
        } catch (Exception e) {
            throw new RuntimeException("Ollama API调用失败: " + e.getMessage(), e);
        }
    }
}