package com.example.hrai;

import com.example.hrai.service.impl.AudioTranscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 提取A1.mp4和A2.mp4音频内容并保存为txt的测试用例
 */
public class VideoBatchTranscriptionTest {

    @Test
    public void testTranscribeA1AndA2AndSaveTxt() throws Exception {
        String[] videoFiles = {"src/main/resources/vide/A1.mp4", "src/main/resources/vide/A2.mp4"};
        for (String videoPath : videoFiles) {
            File file = new File(videoPath);
            Assertions.assertTrue(file.exists(), "测试视频文件不存在: " + videoPath);

            AudioTranscriptionService service = new AudioTranscriptionService();
            String result = service.transcribeAudio(videoPath);
            System.out.println("转写结果(" + videoPath + ")：\n" + result);

            Assertions.assertFalse(result.contains("警告：Vosk模型未找到"), "Vosk模型未找到，请检查模型路径和解压");
            Assertions.assertFalse(result.contains("转录失败"), "转录流程出现异常");
            Assertions.assertTrue(result.trim().length() > 10, "转写结果内容过短，可能未识别成功");

            // 保存为txt
            String txtPath = videoPath.replace(".mp4", ".txt");
            try (FileWriter writer = new FileWriter(txtPath)) {
                writer.write(result);
            }
            Assertions.assertTrue(Files.exists(Paths.get(txtPath)), "转写txt文件未生成: " + txtPath);
        }
    }
}
