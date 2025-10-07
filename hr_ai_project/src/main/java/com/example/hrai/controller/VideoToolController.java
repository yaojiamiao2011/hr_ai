package com.example.hrai.controller;

import com.example.hrai.entity.AIModelConfig;
import com.example.hrai.entity.VideoToText;
import com.example.hrai.service.AIModelConfigService;
import com.example.hrai.service.VideoToTextService;
import com.example.hrai.service.impl.AudioTranscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/video-tool")
@Tag(name = "Video Tool", description = "APIs for video processing tool")
public class VideoToolController {

    @Autowired
    private AIModelConfigService aiModelConfigService;

    @Autowired
    private VideoToTextService videoToTextService;

    @Autowired
    private AudioTranscriptionService audioTranscriptionService;

    @PostMapping("/extract-text")
    @Operation(summary = "Extract text from video using Vosk offline speech recognition")
    public ResponseEntity<?> extractTextFromVideo(@RequestBody Map<String, String> request) {
        try {
            String videoPath = request.get("videoPath");
            if (videoPath == null || videoPath.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Missing videoPath parameter",
                    "message", "视频路径不能为空"
                ));
            }

            String extractedText;
            // 使用音频转录服务（使用Vosk离线语音识别）
            try {
                extractedText = audioTranscriptionService.transcribeAudio(videoPath);
            } catch (Exception e) {
                System.err.println("音频转录服务失败: " + e.getMessage());
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Audio transcription failed",
                    "message", "音频转录失败: " + e.getMessage()
                ));
            }

            // 获取视频文件信息
            File videoFile = new File(videoPath);
            String source = videoFile.exists() ? "本地视频" : "在线视频";
            Long duration = getVideoDuration(videoPath); // 这里需要实现获取视频时长的逻辑

            // 保存到数据库
            VideoToText videoToText = new VideoToText();
            videoToText.setSource(source);
            videoToText.setUrl(videoPath);
            videoToText.setDuration(duration);
            videoToText.setTextContent(extractedText);
            videoToText.setSummary(""); // 摘要将在生成后更新
            videoToTextService.saveVideoToText(videoToText);

            return ResponseEntity.ok(Map.of(
                "text", extractedText,
                "recordId", videoToText.getId(), // 返回记录ID，用于后续更新摘要
                "message", "文本提取成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Text extraction failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/generate-summary")
    @Operation(summary = "Generate summary using default AI model")
    public ResponseEntity<?> generateSummary(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            Long recordId = null;
            if (request.get("recordId") != null) {
                if (request.get("recordId") instanceof Long) {
                    recordId = (Long) request.get("recordId");
                } else if (request.get("recordId") instanceof Integer) {
                    recordId = ((Integer) request.get("recordId")).longValue();
                } else {
                    recordId = Long.valueOf(request.get("recordId").toString());
                }
            }

            if (text == null || text.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Missing text parameter",
                    "message", "文本内容不能为空"
                ));
            }

            // 获取默认AI模型配置
            Optional<AIModelConfig> defaultConfigOpt = aiModelConfigService.getDefaultConfig();
            if (!defaultConfigOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No default model configured",
                    "message", "未设置默认大模型配置"
                ));
            }

            AIModelConfig defaultConfig = defaultConfigOpt.get();

            // 生成提纲摘要的提示词
            String prompt = "请根据以下视频内容生成一个提纲摘要，以HTML格式返回，要求包含主要内容和关键要点：\n\n" + text;

            // 调用AI服务生成摘要
            String summary = aiModelConfigService.testModel(defaultConfig, prompt);

            // 如果提供了记录ID，则更新数据库中的摘要
            if (recordId != null) {
                Optional<VideoToText> videoToTextOpt = videoToTextService.getVideoToTextById(recordId);
                if (videoToTextOpt.isPresent()) {
                    VideoToText videoToText = videoToTextOpt.get();
                    videoToText.setSummary(summary);
                    videoToTextService.saveVideoToText(videoToText);
                }
            }

            return ResponseEntity.ok(Map.of(
                "summary", summary,
                "message", "摘要生成成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Summary generation failed",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 获取视频文件的时长（秒）
     * @param videoPath 视频文件路径
     * @return 视频时长（秒）
     */
    private Long getVideoDuration(String videoPath) {
        try {
            Path path = Path.of(videoPath);
            if (Files.exists(path)) {
                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                // 这里只是一个简单的示例，实际应用中可能需要使用FFmpeg等工具来获取准确的视频时长
                // 我们可以通过文件大小和一些假设来估算时长，或者返回null表示未知
                return null; // 未知时长
            }
        } catch (Exception e) {
            // 忽略异常，返回null表示未知时长
        }
        return null; // 未知时长
    }


    @PostMapping("/upload-and-process")
    @Operation(summary = "Upload video file and process with Vosk offline speech recognition")
    public ResponseEntity<?> uploadAndProcessVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "File is empty",
                    "message", "请选择一个视频文件"
                ));
            }

            String extractedText;
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf('.')) : ".tmp";
            Path tempDir = Files.createTempDirectory("video_upload_");
            Path tempVideoFile = tempDir.resolve("temp_video" + extension);

            // 保存上传的文件到临时位置
            Files.copy(file.getInputStream(), tempVideoFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 使用音频转录服务（使用Vosk离线语音识别）
            try {
                extractedText = audioTranscriptionService.transcribeUploadedFile(file);
            } catch (Exception e) {
                System.err.println("音频转录服务失败: " + e.getMessage());
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Audio transcription failed",
                    "message", "音频转录失败: " + e.getMessage()
                ));
            }

            // 获取视频文件信息
            String source = "上传视频";
            Long duration = getVideoDuration(tempVideoFile.toString());

            // 保存到数据库
            VideoToText videoToText = new VideoToText();
            videoToText.setSource(source);
            // 存储源文件名作为参考
            videoToText.setUrl(originalFileName != null ? originalFileName : "上传的视频文件");
            videoToText.setDuration(duration);
            videoToText.setTextContent(extractedText);
            videoToText.setSummary("");
            videoToTextService.saveVideoToText(videoToText);

            // 清理临时文件(使用异步方式清理)
            deleteTempFileAsync(tempDir);

            return ResponseEntity.ok(Map.of(
                "text", extractedText,
                "recordId", videoToText.getId(),
                "message", "视频上传并处理成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video upload and processing failed",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 异步删除临时文件
     * @param tempDir 临时目录
     */
    private void deleteTempFileAsync(Path tempDir) {
        // 异步删除临时文件，不阻塞当前请求处理
        new Thread(() -> {
            try {
                // 等待一会再删除，以确保音频转录已完成处理
                Thread.sleep(5000);
                if (tempDir != null && Files.exists(tempDir)) {
                    Files.walk(tempDir)
                         .sorted(java.util.Comparator.reverseOrder())
                         .map(java.nio.file.Path::toFile)
                         .forEach(java.io.File::delete);
                }
            } catch (Exception e) {
                System.err.println("删除临时文件失败: " + e.getMessage());
            }
        }).start();
    }

    @PostMapping("/test-video-to-txt")
    @Operation(summary = "处理指定的测试视频并保存为txt文件")
    public ResponseEntity<?> processTestVideoToTxt() {
        try {
            // 指定测试视频路径和模型路径
            String videoPath = "hr_ai_project/src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";
            String txtOutputPath = "hr_ai_project/src/main/resources/txt";

            // 检查视频文件是否存在
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                // 尝试其他可能的路径
                String alternativePath = "src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";
                File alternativeVideoFile = new File(alternativePath);
                if (!alternativeVideoFile.exists()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Test video file not found",
                        "message", "指定的测试视频文件不存在: " + videoPath + " 或 " + alternativePath
                    ));
                }
                videoPath = alternativePath;
            }

            // 确保输出目录存在
            Path txtDir = Path.of(txtOutputPath);
            if (!Files.exists(txtDir)) {
                Files.createDirectories(txtDir);
            }

            // 使用AudioTranscriptionService处理视频
            String extractedText = audioTranscriptionService.transcribeAudio(videoPath);

            if (extractedText != null && !extractedText.isEmpty()) {
                // 创建txt文件名
                String videoFileName = Path.of(videoPath).getFileName().toString();
                String baseName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
                String txtFileName = baseName + ".txt";
                Path txtFilePath = txtDir.resolve(txtFileName);

                // 将文本内容写入文件
                Files.write(txtFilePath, extractedText.getBytes("UTF-8"));

                // 保存到数据库
                VideoToText videoToText = new VideoToText();
                videoToText.setSource("测试视频");
                videoToText.setUrl(videoPath);
                videoToText.setDuration(getVideoDuration(videoPath));
                videoToText.setTextContent(extractedText);
                videoToText.setSummary("");
                videoToTextService.saveVideoToText(videoToText);

                // 返回正确的文件路径
                String correctTxtFilePath = txtFilePath.toAbsolutePath().toString();
                return ResponseEntity.ok(Map.of(
                    "text", extractedText,
                    "txtFilePath", correctTxtFilePath,
                    "recordId", videoToText.getId(),
                    "message", "测试视频转文本成功，文件已保存至: " + correctTxtFilePath
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Empty transcription result",
                    "message", "视频转录结果为空"
                ));
            }
        } catch (Exception e) {
            System.err.println("处理测试视频失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video processing failed",
                "message", "处理测试视频失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/test-download-video-to-txt")
    @Operation(summary = "处理下载.mp4视频并保存为txt文件")
    public ResponseEntity<?> processDownloadVideoToTxt() {
        try {
            // 指定下载.mp4视频路径
            String videoPath = "hr_ai_project/src/main/resources/vide/下载.mp4";
            String txtOutputPath = "hr_ai_project/src/main/resources/txt";

            // 检查视频文件是否存在
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                // 尝试其他可能的路径
                String alternativePath = "src/main/resources/vide/下载.mp4";
                File alternativeVideoFile = new File(alternativePath);
                if (!alternativeVideoFile.exists()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Download video file not found",
                        "message", "指定的下载视频文件不存在: " + videoPath + " 或 " + alternativePath
                    ));
                }
                videoPath = alternativePath;
            }

            // 确保输出目录存在
            Path txtDir = Path.of(txtOutputPath);
            if (!Files.exists(txtDir)) {
                Files.createDirectories(txtDir);
            }

            // 使用AudioTranscriptionService处理视频
            String extractedText = audioTranscriptionService.transcribeAudio(videoPath);

            if (extractedText != null && !extractedText.isEmpty()) {
                // 创建txt文件名
                String videoFileName = Path.of(videoPath).getFileName().toString();
                String baseName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
                String txtFileName = baseName + "_" + System.currentTimeMillis() + ".txt";
                Path txtFilePath = txtDir.resolve(txtFileName);

                // 将文本内容写入文件
                Files.write(txtFilePath, extractedText.getBytes("UTF-8"));

                // 保存到数据库
                VideoToText videoToText = new VideoToText();
                videoToText.setSource("下载视频");
                videoToText.setUrl(videoPath);
                videoToText.setDuration(getVideoDuration(videoPath));
                videoToText.setTextContent(extractedText);
                videoToText.setSummary("");
                videoToTextService.saveVideoToText(videoToText);

                // 返回正确的文件路径
                String correctTxtFilePath = txtFilePath.toAbsolutePath().toString();
                return ResponseEntity.ok(Map.of(
                    "text", extractedText,
                    "txtFilePath", correctTxtFilePath,
                    "recordId", videoToText.getId(),
                    "message", "下载视频转文本成功，文件已保存至: " + correctTxtFilePath
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Empty transcription result",
                    "message", "视频转录结果为空"
                ));
            }
        } catch (Exception e) {
            System.err.println("处理下载视频失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video processing failed",
                "message", "处理下载视频失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/process-a1-video-to-txt")
    @Operation(summary = "处理A1.mp4视频并保存为txt文件")
    public ResponseEntity<?> processA1VideoToTxt() {
        try {
            // 指定A1.mp4视频路径
            String videoPath = "hr_ai_project/src/main/resources/vide/A1.mp4";
            String txtOutputPath = "hr_ai_project/src/main/resources/txt";

            // 检查视频文件是否存在
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                // 尝试其他可能的路径
                String alternativePath = "src/main/resources/vide/A1.mp4";
                File alternativeVideoFile = new File(alternativePath);
                if (!alternativeVideoFile.exists()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "A1 video file not found",
                        "message", "指定的A1视频文件不存在: " + videoPath + " 或 " + alternativePath
                    ));
                }
                videoPath = alternativePath;
            }

            // 确保输出目录存在
            Path txtDir = Path.of(txtOutputPath);
            if (!Files.exists(txtDir)) {
                Files.createDirectories(txtDir);
            }

            // 使用AudioTranscriptionService处理视频
            String extractedText = audioTranscriptionService.transcribeAudio(videoPath);

            if (extractedText != null && !extractedText.isEmpty()) {
                // 创建txt文件名
                String videoFileName = Path.of(videoPath).getFileName().toString();
                String baseName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
                String txtFileName = baseName + "_" + System.currentTimeMillis() + ".txt";
                Path txtFilePath = txtDir.resolve(txtFileName);

                // 将文本内容写入文件
                Files.write(txtFilePath, extractedText.getBytes("UTF-8"));

                // 保存到数据库
                VideoToText videoToText = new VideoToText();
                videoToText.setSource("A1视频");
                videoToText.setUrl(videoPath);
                videoToText.setDuration(getVideoDuration(videoPath));
                videoToText.setTextContent(extractedText);
                videoToText.setSummary("");
                videoToTextService.saveVideoToText(videoToText);

                // 返回正确的文件路径
                String correctTxtFilePath = txtFilePath.toAbsolutePath().toString();
                return ResponseEntity.ok(Map.of(
                    "text", extractedText,
                    "txtFilePath", correctTxtFilePath,
                    "recordId", videoToText.getId(),
                    "message", "A1视频转文本成功，文件已保存至: " + correctTxtFilePath
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Empty transcription result",
                    "message", "视频转录结果为空"
                ));
            }
        } catch (Exception e) {
            System.err.println("处理A1视频失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video processing failed",
                "message", "处理A1视频失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/process-a2-video-to-txt")
    @Operation(summary = "处理A2.mp4视频并保存为txt文件")
    public ResponseEntity<?> processA2VideoToTxt() {
        try {
            // 指定A2.mp4视频路径
            String videoPath = "hr_ai_project/src/main/resources/vide/A2.mp4";
            String txtOutputPath = "hr_ai_project/src/main/resources/txt";

            // 检查视频文件是否存在
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                // 尝试其他可能的路径
                String alternativePath = "src/main/resources/vide/A2.mp4";
                File alternativeVideoFile = new File(alternativePath);
                if (!alternativeVideoFile.exists()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "A2 video file not found",
                        "message", "指定的A2视频文件不存在: " + videoPath + " 或 " + alternativePath
                    ));
                }
                videoPath = alternativePath;
            }

            // 确保输出目录存在
            Path txtDir = Path.of(txtOutputPath);
            if (!Files.exists(txtDir)) {
                Files.createDirectories(txtDir);
            }

            // 使用AudioTranscriptionService处理视频
            String extractedText = audioTranscriptionService.transcribeAudio(videoPath);

            if (extractedText != null && !extractedText.isEmpty()) {
                // 创建txt文件名
                String videoFileName = Path.of(videoPath).getFileName().toString();
                String baseName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
                String txtFileName = baseName + "_" + System.currentTimeMillis() + ".txt";
                Path txtFilePath = txtDir.resolve(txtFileName);

                // 将文本内容写入文件
                Files.write(txtFilePath, extractedText.getBytes("UTF-8"));

                // 保存到数据库
                VideoToText videoToText = new VideoToText();
                videoToText.setSource("A2视频");
                videoToText.setUrl(videoPath);
                videoToText.setDuration(getVideoDuration(videoPath));
                videoToText.setTextContent(extractedText);
                videoToText.setSummary("");
                videoToTextService.saveVideoToText(videoToText);

                // 返回正确的文件路径
                String correctTxtFilePath = txtFilePath.toAbsolutePath().toString();
                return ResponseEntity.ok(Map.of(
                    "text", extractedText,
                    "txtFilePath", correctTxtFilePath,
                    "recordId", videoToText.getId(),
                    "message", "A2视频转文本成功，文件已保存至: " + correctTxtFilePath
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Empty transcription result",
                    "message", "视频转录结果为空"
                ));
            }
        } catch (Exception e) {
            System.err.println("处理A2视频失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Video processing failed",
                "message", "处理A2视频失败: " + e.getMessage()
            ));
        }
    }
}
