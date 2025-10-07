package com.example.hrai.repository;

import com.example.hrai.entity.VideoToText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoToTextRepository extends JpaRepository<VideoToText, Long> {
    // 可以在这里添加自定义查询方法
}