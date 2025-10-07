package com.example.hrai.config;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.repository.AIModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAIModelConfig implements CommandLineRunner {

    @Autowired
    private AIModelConfigRepository aiModelConfigRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在默认配置
        if (aiModelConfigRepository.count() == 0) {
            // 添加本地Ollama配置
            AIModelConfig ollamaConfig = new AIModelConfig();
            ollamaConfig.setModelName("qwen3:4b");
            ollamaConfig.setProvider("Ollama");
            ollamaConfig.setApiKey("");
            ollamaConfig.setModelEndpoint("http://localhost:11434/api/chat");
            ollamaConfig.setIsActive(true);
            aiModelConfigRepository.save(ollamaConfig);

            // 添加在线ModelScope配置
            AIModelConfig modelScopeConfig = new AIModelConfig();
            modelScopeConfig.setModelName("deepseek-ai/DeepSeek-V3.1");
            modelScopeConfig.setProvider("ModelScope");
            modelScopeConfig.setApiKey("ms-4dfb0661-09bf-4f32-8b7b-2f4e79958990");
            modelScopeConfig.setModelEndpoint("https://api-inference.modelscope.cn/v1");
            modelScopeConfig.setIsActive(false); // 默认不启用在线配置
            aiModelConfigRepository.save(modelScopeConfig);
        }
    }
}