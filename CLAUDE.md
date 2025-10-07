# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

HR AI 项目是一个基于 Spring Boot 的应用，主要用于管理和转录 AI 模型配置以及处理视频转录任务。项目包含后端 Spring Boot 服务和前端 Vue3 界面，使用 SQLite 作为数据库，支持 VOSK 的离线语音识别功能。

## 项目结构

- `hr_ai_project/` - 后端 Spring Boot 应用
  - `src/main/java/com/example/hrai/` - 主要 Java 源码
    - `entity/` - JPA 实体类
    - `repository/` - 数据访问层
    - `service/` - 业务逻辑服务
    - `controller/` - REST API 控制器
    - `config/` - 配置类
    - `service/impl/` - 服务实现类，包括 `VoskAudioTranscriptionService.java` 和 `AudioTranscriptionService.java`
  - `src/main/resources/` - 配置文件和资源
    - `Models/` - VOSK 模型目录
    - `vide/` - 存放待处理的视频文件
    - `txt/` - 存放转录输出的文本文件
    - `application.properties` - 应用程序配置
- `frontend/` - Vue3 前端应用

## 技术栈

- **后端**: Spring Boot 3.2.0, Java 21
- **数据库**: SQLite，使用 JPA/Hibernate 进行数据访问
- **前端**: Vue3, TypeScript (pending implementation)
- **离线语音识别**: VOSK
- **API 文档**: Swagger UI
- **构建工具**: Maven

## 重要开发规范

### 端口配置
- 后端端口：8081 (在 `application.properties` 中配置)
- 前端端口：8000 (规范中定义)
- 不要随意更改已配置的端口，遇到冲突应查找 kill 占用进程而非修改端口

### 地址规范
- 禁止使用 localhost，必须使用 127.0.0.1 作为本地网络地址

### Node.js 进程管理
- 不得使用会终止所有 node 进程的命令 (如 `killall node` 或 `taskkill /f /im node.exe`)
- 应只终止特定的 node 进程，避免影响其他项目

## 常用开发命令

### 构建和运行

```bash
# 编译后端项目
cd hr_ai_project && mvn clean install -DskipTests

# 运行后端服务
cd hr_ai_project && mvn spring-boot:run

# 前端安装依赖
cd frontend && npm install

# 运行前端开发服务器
cd frontend && npm run dev
```

### 数据库设置

- 默认使用 SQLite 数据库: `hr_ai.db`
- 位置: 项目根目录
- 会自动创建，无需手动设置

### VOSK 语音识别配置

- 离线语音识别模型目录: `hr_ai_project/src/main/resources/Models/`
- 目前使用 `vosk-model-small-cn-0.22` 中文语音模型
- 模型文件必须解压并在配置路径中配置正确路径

### API 测试

```bash
# 获取 Swagger UI 文档
# http://localhost:8081/swagger-ui.html

# 测试 API 端点脚本
curl -X POST http://localhost:8081/api/video-tool/process-a1-video-to-txt
curl -X POST http://localhost:8081/api/video-tool/process-a2-video-to-txt
```

## 重要业务功能

### AI 配置管理

API 端点:
- `GET /api/ai-configs` - 获取所有 AI 模型配置
- `POST /api/ai-configs` - 创建 AI 模型配置
- `PUT /api/ai-configs/{id}` - 更新 AI 模型配置
- `DELETE /api/ai-configs/{id}` - 删除 AI 模型配置

### 视频转录服务

核心服务类: `VoskAudioTranscriptionService.java`

API 端点:
- `POST /api/video-tool/extract-text` - 从视频中提取文本
- `POST /api/video-tool/process-a1-video-to-txt` - 转录 A1 视频为文本
- `POST /api/video-tool/process-a2-video-to-txt` - 转录 A2 视频为文本
- `POST /api/video-tool/process-download-video-to-txt` - 转录下载视频为文本
- `POST /api/video-tool/upload-and-process` - 上传并处理视频

### 架构模式

1. **Mvc模式**: 使用 Spring MVC 模式处理 API 请求
2. **分层架构**: Controller → Service → Entity
3. **JavaCV处理**: 使用 FFmpeg 进行音频提取
4. **VOSK离线处理**: 不依赖云端，提供本地语音识别能力

## 开发注意事项

- 项目同时支持 SQLite 数据库和 VOSK 语音识别功能
- VOSK 模型需要预先下载和配置
- 视频转录端点 `/api/video-tool/process-a1-video-to-txt` 和 `/api/video-tool/process-a2-video-to-txt` 是关键功能点
- 禁用 Whisper 依赖，完全使用 VOSK 进行离线语音识别
- 后端在 8081 端口运行