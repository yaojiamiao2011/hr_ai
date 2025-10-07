package com.example.hrai.service;

import com.example.hrai.entity.VideoToText;
import com.example.hrai.repository.VideoToTextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoToTextService {

    @Autowired
    private VideoToTextRepository videoToTextRepository;

    public List<VideoToText> getAllVideoToTextRecords() {
        return videoToTextRepository.findAll();
    }

    public Optional<VideoToText> getVideoToTextById(Long id) {
        return videoToTextRepository.findById(id);
    }

    public VideoToText saveVideoToText(VideoToText videoToText) {
        return videoToTextRepository.save(videoToText);
    }

    public void deleteVideoToText(Long id) {
        videoToTextRepository.deleteById(id);
    }

    public List<VideoToText> getVideoToTextBySource(String source) {
        // 这里可以根据需要添加自定义查询逻辑
        return videoToTextRepository.findAll();
    }
}