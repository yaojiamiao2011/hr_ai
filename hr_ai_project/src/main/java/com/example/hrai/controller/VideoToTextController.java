package com.example.hrai.controller;

import com.example.hrai.entity.VideoToText;
import com.example.hrai.service.VideoToTextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/video-to-text")
@Tag(name = "Video to Text", description = "APIs for managing video to text records")
public class VideoToTextController {

    @Autowired
    private VideoToTextService videoToTextService;

    @GetMapping
    @Operation(summary = "Get all video to text records")
    public List<VideoToText> getAllVideoToTextRecords() {
        return videoToTextService.getAllVideoToTextRecords();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a video to text record by ID")
    public ResponseEntity<VideoToText> getVideoToTextById(@PathVariable Long id) {
        Optional<VideoToText> videoToText = videoToTextService.getVideoToTextById(id);
        return videoToText.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new video to text record")
    public VideoToText createVideoToText(@RequestBody VideoToText videoToText) {
        return videoToTextService.saveVideoToText(videoToText);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a video to text record")
    public ResponseEntity<VideoToText> updateVideoToText(@PathVariable Long id, @RequestBody VideoToText videoToTextDetails) {
        Optional<VideoToText> videoToText = videoToTextService.getVideoToTextById(id);
        if (videoToText.isPresent()) {
            VideoToText updatedVideoToText = videoToText.get();
            updatedVideoToText.setSource(videoToTextDetails.getSource());
            updatedVideoToText.setUrl(videoToTextDetails.getUrl());
            updatedVideoToText.setDuration(videoToTextDetails.getDuration());
            updatedVideoToText.setTextContent(videoToTextDetails.getTextContent());
            updatedVideoToText.setSummary(videoToTextDetails.getSummary());
            return ResponseEntity.ok(videoToTextService.saveVideoToText(updatedVideoToText));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a video to text record")
    public ResponseEntity<Void> deleteVideoToText(@PathVariable Long id) {
        Optional<VideoToText> videoToText = videoToTextService.getVideoToTextById(id);
        if (videoToText.isPresent()) {
            videoToTextService.deleteVideoToText(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}