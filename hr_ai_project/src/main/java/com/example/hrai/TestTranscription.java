package com.example.hrai;

import com.example.hrai.service.impl.AudioTranscriptionService;

public class TestTranscription {
    public static void main(String[] args) {
        try {
            AudioTranscriptionService service = new AudioTranscriptionService();

            // Test with a sample video file path
            // You'll need to update this path to point to an actual video file
            String videoPath = "src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";

            System.out.println("Testing transcription with video: " + videoPath);
            String result = service.transcribeAudio(videoPath);
            System.out.println("Transcription result: " + result);

        } catch (Exception e) {
            System.err.println("Error during transcription test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}