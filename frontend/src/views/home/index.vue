<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎使用 HR AI 智能人力资源系统</h1>
          <p class="subtitle">智能化人力资源解决方案，提升管理效率</p>
        </div>
        <div class="welcome-image">
          <el-icon class="welcome-icon"><TrendCharts /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 系统统计 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon users">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">1,234</div>
                <div class="stat-label">员工总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon positions">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">89</div>
                <div class="stat-label">职位数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon applications">
                <el-icon><Files /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">456</div>
                <div class="stat-label">待处理申请</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon ai">
                <el-icon><Robot /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ aiConfigCount }}</div>
                <div class="stat-label">AI模型配置</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>快捷操作</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="action-item" @click="navigateToAIConfig">
              <div class="action-icon">
                <el-icon><Setting /></el-icon>
              </div>
              <div class="action-text">
                <h3>AI配置</h3>
                <p>配置大模型参数</p>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="action-item">
              <div class="action-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="action-text">
                <h3>员工管理</h3>
                <p>管理员工信息</p>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="action-item">
              <div class="action-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="action-text">
                <h3>数据分析</h3>
                <p>查看统计报表</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 系统状态 -->
    <div class="system-status">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>系统状态</span>
            <el-tag :type="systemStatus.type" size="small">{{ systemStatus.text }}</el-tag>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="status-item">
              <span class="status-label">AI服务状态：</span>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="status-item">
              <span class="status-label">数据库连接：</span>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
          </el-col>
          <el-col :span="12" class="mt-10">
            <div class="status-item">
              <span class="status-label">系统版本：</span>
              <span class="status-value">v1.0.0</span>
            </div>
          </el-col>
          <el-col :span="12" class="mt-10">
            <div class="status-item">
              <span class="status-label">最后更新：</span>
              <span class="status-value">{{ lastUpdateTime }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const aiConfigCount = ref(3)
const lastUpdateTime = new Date().toLocaleString('zh-CN')
const systemStatus = ref({
  type: 'success',
  text: '正常运行'
})

const navigateToAIConfig = () => {
  router.push('/ai-config')
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100%;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-card :deep(.el-card__body) {
  padding: 40px;
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
  }

  .welcome-icon {
    margin-top: 20px;
  }
}

.welcome-text h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #606266;
}

.welcome-icon {
  font-size: 80px;
  color: #409EFF;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

@media (max-width: 768px) {
  .stat-card {
    height: 80px;
    margin-bottom: 10px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .stat-number {
    font-size: 20px;
  }

  .stat-label {
    font-size: 12px;
  }
}

@media (max-width: 576px) {
  .stat-card {
    height: 70px;
  }
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
  height: 100%;
}

.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.positions {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.applications {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.ai {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.quick-actions {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.action-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

@media (max-width: 768px) {
  .action-item {
    flex-direction: column;
    text-align: center;
    padding: 15px;
  }

  .action-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}

@media (max-width: 576px) {
  .action-item {
    padding: 10px;
  }
}

.action-item:hover {
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  color: white;
}

.action-text h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 5px;
}

.action-text p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.status-item {
  display: flex;
  align-items: center;
}

.status-label {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.status-value {
  font-size: 14px;
  color: #303133;
}
</style>