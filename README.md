# HR AI 项目

这是一个基于 Spring Boot 的项目，集成了 AI 功能、SQLite 数据库存储和 Swagger API 文档，并包含一个基于 Vue3 + Vite 的前端界面。

## 技术要求
- Spring Boot
- JDK 21
- Vue3 + Vite
- SQLite 数据库
- Spring Boot AI 集成

## 功能特性
1. 在数据库中存储 AI 模型配置
2. 集成 Swagger 用于 API 文档
3. RESTful API 用于管理 AI 模型配置
4. 基于 Vue3 + Vite 的现代化前端界面

## 项目结构
- `entity/` - 用于数据库映射的 JPA 实体
- `repository/` - 用于数据库操作的 Spring Data Jpa 仓储
- `service/` - 业务逻辑层
- `controller/` - REST API 端点
- `config/` - 配置类
- `frontend/` - Vue3 + Vite 前端项目

## API 接口
- GET `/api/ai-config` - 获取所有 AI 模型配置
- GET `/api/ai-config/{id}` - 根据 ID 获取 AI 模型配置
- POST `/api/ai-config` - 创建新的 AI 模型配置
- PUT `/api/ai-config/{id}` - 更新现有的 AI 模型配置
- DELETE `/api/ai-config/{id}` - 删除 AI 模型配置
- GET `/api/ai-config/active` - 获取所有激活的 AI 模型配置

## Swagger UI
启动应用程序后，在以下地址访问 Swagger UI：
- http://localhost:8080/swagger-ui.html

## 数据库
应用程序使用 SQLite 作为数据库。当应用程序启动时，数据库文件 `hr_ai.db` 将自动创建。