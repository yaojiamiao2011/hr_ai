package com.example.hrai.config;

import com.example.hrai.service.impl.AudioTranscriptionService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 音视频转录配置类
 * 控制是否使用Vosk离线语音识别引擎
 */
@Configuration
public class AudioTranscriptionConfig {

    private final AudioTranscriptionService audioTranscriptionService;

    public AudioTranscriptionConfig(AudioTranscriptionService audioTranscriptionService) {
        this.audioTranscriptionService = audioTranscriptionService;
    }

    /**
     * 初始化时设置是否使用Vosk引擎
     * 可以根据需要修改此设置
     */
    @PostConstruct
    public void init() {
    // 设置为true以启用Vosk离线语音识别引擎
    // 设置为false以使用默认的模拟识别（用于演示）
    audioTranscriptionService.setUseVosk(true);
    System.out.println("音频转录服务配置完成");
    }
}