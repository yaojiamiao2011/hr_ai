# HR AI 前端项目

基于 Vue 3 + TypeScript + Element Plus 的现代化前端项目。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - JavaScript 的超集，提供静态类型检查
- **Element Plus** - 基于 Vue 3 的组件库
- **Vue Router** - Vue.js 官方路由
- **Pinia** - Vue 状态管理库
- **Vite** - 下一代前端构建工具
- **Axios** - HTTP 客户端

## 项目结构

```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── layout/          # 布局组件
│   ├── router/          # 路由配置
│   ├── views/           # 页面组件
│   ├── stores/          # Pinia 状态管理
│   ├── utils/           # 工具函数
│   ├── style.css        # 全局样式
│   ├── main.ts          # 应用入口
│   └── App.vue          # 根组件
├── index.html           # HTML 模板
├── package.json         # 项目依赖
├── tsconfig.json        # TypeScript 配置
├── vite.config.ts       # Vite 配置
└── README.md           # 项目说明
```

## 主要功能

### 1. 首页
- 系统概览展示
- 数据统计卡片
- 快捷操作入口
- 系统状态监控

### 2. 大模型配置
- 支持多种大模型类型（GPT-4、文心一言、通义千问等）
- 完整的配置管理（增删改查）
- 实时状态切换
- 配置参数验证

### 3. 响应式设计
- 移动端适配
- 平板端适配
- 桌面端优化

## 开发指南

### 环境要求
- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0

### 安装依赖
```bash
cd frontend
npm install
# 或
yarn install
```

### 启动开发服务器
```bash
npm run dev
# 或
yarn dev
```

### 构建生产版本
```bash
npm run build
# 或
yarn build
```

### 预览生产版本
```bash
npm run preview
# 或
yarn preview
```

## 代码规范

项目使用 TypeScript 进行开发，遵循以下规范：

- 使用 Composition API
- 优先使用 `<script setup>` 语法
- 组件名使用 PascalCase
- 文件名使用 kebab-case
- 使用 ESLint 进行代码检查

## 部署说明

构建完成后，生成的静态文件位于 `dist` 目录，可以部署到任何静态文件服务器。

## 注意事项

1. 本项目使用了 Element Plus 的自动导入功能，无需手动引入组件
2. 图标通过 `@element-plus/icons-vue` 提供
3. 路径别名 `@` 指向 `src` 目录
4. 支持热更新开发