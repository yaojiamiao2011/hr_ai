# 服务可用性测试代理 (Service Availability Test Agent)

此代理用于测试 HR AI 应用程序中前端和后端服务的健康状况与连通性。

## 配置信息
- 前端服务: http://localhost:8001 (或 8000 - 配置为准)
- 后端服务: http://localhost:8081
- API 代理: 前端通过 /api 路径代理到后端

## 功能说明

### 1. 服务健康检查
- 验证前端页面是否正常加载
- 检查后端 API 服务是否可用
- 测试前后端之间的通信接口

### 2. 前端功能验证
- 测试网页界面加载状态
- 验证基本导航功能
- 测试 UI 元素是否正常工作

### 3. 后端 API 检测
- 健康检查端点验证
- 主要 API 路径可达性测试
- 数据接口功能验证

### 4. 集成服务测试
- 前端通过代理访问后端服务
- 测试前后端互动功能

## 可测试的端点

### 后端 API 路径
```
GET /api/ai-config      - AI配置服务
GET /api/video-to-text  - 视频转文本服务
POST /api/video-tool/extract-text - 提取视频文本
POST /api/video-tool/generate-summary - 生成摘要
```

### 前端页面路径
```
/                - 主页
/ai-config       - AI配置页面
/video-tool      - 视频工具页面
/video-tool/test-video - 测试视频转文本页面
```

## 启动方法

集成的 Playwright 配置会自动启动前后端服务:

```bash
# 安装 Playwright 浏览器驱动（首次运行）
npx playwright install

# 运行服务可用性测试
npm run test

# 或运行特定测试
npx playwright test tests/service-availability.spec.ts

# 在UI模式下运行
npm run test:ui

# 查看详细报告
npm run test:report
```

## Playwright 配置
playwright.config.ts 包含以下服务启动配置:

```typescript
webServer: [
  {
    command: 'npm run dev',           // 前端启动命令
    port: 8000,                      // 前端端口
    reuseExistingServer: !process.env.CI
  },
  {
    command: 'cd ../hr_ai_project && mvn spring-boot:run',  // 后端启动命令
    port: 8081,                                            // 后端端口
    reuseExistingServer: !process.env.CI,
    timeout: 120000
  }
]
```

## 测试资源

服务可用性测试文件位于:
- `tests/service-availability.spec.ts` - 全面服务健康检测
- `tests/example.spec.ts` - 基本页面加载测试
- `tests/video-transcription.spec.ts` - 视频转文本专项测试

## 验证指标

### 前端验证指标
- 页面加载状态 (200 OK)
- 元素可见性
- 标题文本匹配
- 导航功能正常

### 后端验证指标
- API 端点响应状态
- HTTP 请求/响应处理
- 数据库连接健康度
- 业务服务可用性

## 使用场景

### CI/CD 流程
在持续集成流程中使用此测试确认部署成功，确保服务启动正常。

### 开发环境验证
开发人员在本地验证前后端服务是否按预期工作。

### 生产环境健康检查
用于验证已部署的应用程序是否运行正常。

## 注意事项

1. 确保前后端端口在测试环境中未被占用
2. API 测试可能会访问外部资源或执行数据写入操作
3. 服务启动超时设置为2分钟，请确保环境能在此时间内完成启动

---
*此代理通过 Playwright 自动化测试工具执行全面的服务可用性检查*