package com.example.hrai;

import com.example.hrai.service.impl.AudioTranscriptionService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestApiController {
    public static void main(String[] args) {
        try {
            AudioTranscriptionService service = new AudioTranscriptionService();

            // Test with the same path used in the VideoToolController
            String videoPath = "hr_ai_project/src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";
            File videoFile = new File(videoPath);

            System.out.println("Testing transcription with video: " + videoPath);
            System.out.println("Video file exists: " + videoFile.exists());

            if (!videoFile.exists()) {
                // Try alternative path
                String alternativePath = "src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";
                File alternativeVideoFile = new File(alternativePath);
                System.out.println("Alternative path: " + alternativePath);
                System.out.println("Alternative video file exists: " + alternativeVideoFile.exists());

                if (alternativeVideoFile.exists()) {
                    videoPath = alternativePath;
                }
            }

            String result = service.transcribeAudio(videoPath);
            System.out.println("Transcription result: \"" + result + "\"");
            System.out.println("Result length: " + (result != null ? result.length() : "null"));

            // Check for generated txt file
            String baseName = videoPath.substring(0, videoPath.lastIndexOf('.'));
            String txtPath = baseName + ".txt";
            File txtFile = new File(txtPath);
            System.out.println("Generated txt file path: " + txtPath);
            System.out.println("Generated txt file exists: " + txtFile.exists());

            if (txtFile.exists()) {
                String content = new String(Files.readAllBytes(txtFile.toPath()));
                System.out.println("Txt file content: \"" + content + "\"");
                System.out.println("Txt file content length: " + content.length());
            }

        } catch (Exception e) {
            System.err.println("Error during transcription test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}