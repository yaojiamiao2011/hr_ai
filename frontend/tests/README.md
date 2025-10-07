# Playwright E2E 测试

本项目集成了 Playwright 用于端到端测试，以自动化验证前端功能和后端API集成。

## 安装依赖

如果尚未安装 Playwright 的浏览器驱动：

```bash
npx playwright install
```

## 运行测试

### 运行所有测试
```bash
npm run test
```

### 运行特定测试
```bash
npx playwright test tests/video-transcription.spec.ts
```

### 在UI模式下运行
```bash
npm run test:ui
```

### 生成测试报告
```bash
npm run test:report
```

## 测试配置

Playwright 配置文件 (`playwright.config.ts`) 配置了以下内容：

- 同时启动前端 (端口 8000) 和后端 (端口 8081) 服务器
- 多浏览器测试 (Chrome, Firefox, Safari)
- 自动截图、视频录制和跟踪功能，以帮助调试测试失败

## HTTP 代理配置

后续可添加 HTTP 拦截配置，用于 Mocker 和请求处理，以实现全方位的MCP服务集成。