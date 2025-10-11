package com.example.hrai;

import com.example.hrai.service.impl.AudioTranscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * 用于测试本地vosk语音识别环境和视频转文本功能的Junit单元测试。
 */
public class VoskTranscriptionEnvTest {

    @Test
    public void testTranscribeA2Mp4() throws Exception {
        String videoPath = "src/main/resources/vide/A2.mp4";
        File file = new File(videoPath);
        Assertions.assertTrue(file.exists(), "测试视频文件不存在: " + videoPath);

        AudioTranscriptionService service = new AudioTranscriptionService();
        String result = service.transcribeAudio(videoPath);
        System.out.println("转写结果：\n" + result);

        // 检查vosk模型是否有效（如果模型缺失会返回警告或模拟内容）
        Assertions.assertFalse(result.contains("警告：Vosk模型未找到"), "Vosk模型未找到，请检查模型路径和解压");
        Assertions.assertFalse(result.contains("转录失败"), "转录流程出现异常");
        Assertions.assertTrue(result.trim().length() > 10, "转写结果内容过短，可能未识别成功");
    }
}
