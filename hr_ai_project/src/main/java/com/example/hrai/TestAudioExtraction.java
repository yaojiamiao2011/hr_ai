package com.example.hrai;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;

public class TestAudioExtraction {
    public static void main(String[] args) {
        try {
            String videoPath = "hr_ai_project/src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4";
            String audioPath = "test_output.wav";

            System.out.println("Testing audio extraction from: " + videoPath);

            // Check if video file exists
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                System.out.println("Video file does not exist: " + videoPath);
                return;
            }

            System.out.println("Video file exists, size: " + videoFile.length() + " bytes");

            // Extract audio
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(videoPath);
            grabber.start();

            System.out.println("Video duration: " + grabber.getLengthInTime() / 1000000 + " seconds");
            System.out.println("Audio channels: " + grabber.getAudioChannels());
            System.out.println("Sample rate: " + grabber.getSampleRate());

            // Generate WAV format audio file
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(audioPath, 1); // mono
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_NONE); // no video
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE); // 16bit PCM
            recorder.setSampleRate(16000); // whisper recommended sample rate
            recorder.start();

            System.out.println("Extracting audio...");

            int frameCount = 0;
            while (true) {
                var frame = grabber.grab();
                if (frame == null) break;

                // Check if frame contains audio data
                if (frame.samples != null && frame.samples.length > 0) {
                    recorder.record(frame);
                    frameCount++;
                }
            }

            recorder.stop();
            grabber.stop();
            grabber.release();
            recorder.release();

            System.out.println("Audio extraction completed. Frames processed: " + frameCount);

            // Check if audio file was created
            File audioFile = new File(audioPath);
            if (audioFile.exists()) {
                System.out.println("Audio file created successfully: " + audioPath);
                System.out.println("Audio file size: " + audioFile.length() + " bytes");
            } else {
                System.out.println("Audio file was not created");
            }

        } catch (Exception e) {
            System.err.println("Error during audio extraction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}