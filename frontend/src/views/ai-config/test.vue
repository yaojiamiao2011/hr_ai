<template>
  <div class="model-test-container">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <div class="model-info">
            <h3>模型测试</h3>
            <p v-if="modelConfig" class="model-description">
              {{ modelConfig.provider }} - {{ modelConfig.modelName }}
            </p>
          </div>
          <el-button @click="$router.go(-1)">返回</el-button>
        </div>
      </template>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 测试界面 -->
      <div v-else-if="modelConfig" class="test-interface">
        <!-- 模型信息展示 -->
        <div class="model-details">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="模型名称">{{ modelConfig.modelName }}</el-descriptions-item>
            <el-descriptions-item label="提供商">{{ modelConfig.provider }}</el-descriptions-item>
            <el-descriptions-item label="API端点">{{ modelConfig.modelEndpoint }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="modelConfig.isActive ? 'success' : 'danger'">
                {{ modelConfig.isActive ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 测试输入区域 -->
        <div class="input-section">
          <h4>请输入提示词</h4>
          <el-input
            v-model="prompt"
            type="textarea"
            :rows="4"
            placeholder="请输入您想测试的问题或提示词..."
            :disabled="processing"
            @keydown.enter="handleEnterKey"
          />
          <div class="input-actions">
            <el-button
              type="primary"
              @click="sendPrompt"
              :loading="processing"
              :disabled="!prompt.trim()"
            >
              发送测试
            </el-button>
            <el-button @click="clearConversation">清空对话</el-button>
          </div>
        </div>

        <!-- 对话历史和结果 -->
        <div class="conversation-section">
          <h4>对话记录</h4>
          <div class="conversation-container">
            <div
              v-for="(message, index) in conversation"
              :key="index"
              class="message-item"
              :class="message.role"
            >
              <div class="message-header">
                <span class="message-role">
                  {{ message.role === 'user' ? '👤 用户' : '🤖 助手' }}
                </span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-content">
                <pre>{{ message.content }}</pre>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-if="processing" class="message-item assistant">
              <div class="message-header">
                <span class="message-role">🤖 助手</span>
                <span class="message-time">正在思考...</span>
              </div>
              <div class="message-content">
                <el-skeleton :rows="2" animated />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else class="error-container">
        <el-result
          icon="warning"
          title="模型配置加载失败"
          :sub-title="errorMessage"
        >
          <template #extra>
            <el-button type="primary" @click="loadModelConfig">重新加载</el-button>
            <el-button @click="$router.go(-1)">返回</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

interface ModelConfig {
  id: number
  modelName: string
  provider: string
  modelEndpoint: string
  apiKey: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

interface Message {
  role: 'user' | 'assistant'
  content: string
  time: string
}

const route = useRoute()
const router = useRouter()

const modelConfig = ref<ModelConfig | null>(null)
const prompt = ref('')
const conversation = ref<Message[]>([])
const loading = ref(true)
const processing = ref(false)
const errorMessage = ref('')

// 加载模型配置
const loadModelConfig = async () => {
  try {
    loading.value = true
    const configId = route.params.id
    const response = await fetch(`/api/ai-config/${configId}`)

    if (response.ok) {
      const data = await response.json()
      modelConfig.value = data

      if (!data.isActive) {
        ElMessage.warning('该模型配置当前处于禁用状态，可能无法正常测试')
      }
    } else {
      errorMessage.value = '无法获取模型配置'
      modelConfig.value = null
    }
  } catch (error) {
    console.error('加载模型配置失败:', error)
    errorMessage.value = '网络错误，请检查后端服务是否正常'
    modelConfig.value = null
  } finally {
    loading.value = false
  }
}

// 发送提示词
const sendPrompt = async () => {
  if (!prompt.value.trim() || !modelConfig.value) {
    return
  }

  // 添加用户消息到对话历史
  conversation.value.push({
    role: 'user',
    content: prompt.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN')
  })

  const userPrompt = prompt.value.trim()
  prompt.value = ''
  processing.value = true

  try {
    const response = await fetch(`/api/ai-config/test/${modelConfig.value.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        prompt: userPrompt
      })
    })

    if (response.ok) {
      const data = await response.json()

      // 添加助手回复到对话历史
      conversation.value.push({
        role: 'assistant',
        content: data.content || data.message || '收到响应但内容为空',
        time: new Date().toLocaleTimeString('zh-CN')
      })
    } else {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
    }
  } catch (error) {
    console.error('发送测试失败:', error)
    const errorMsg = error instanceof Error ? error.message : '未知错误'

    // 添加错误消息到对话历史
    conversation.value.push({
      role: 'assistant',
      content: `❌ 错误: ${errorMsg}`,
      time: new Date().toLocaleTimeString('zh-CN')
    })

    ElMessage.error(`测试失败: ${errorMsg}`)
  } finally {
    processing.value = false
  }
}

// 清空对话
const clearConversation = () => {
  conversation.value = []
  prompt.value = ''
  ElMessage.success('对话已清空')
}

// 处理回车键事件
const handleEnterKey = (event: KeyboardEvent) => {
  // 如果按的是Enter键且没有按住Shift键，则发送消息
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault() // 防止换行
    if (prompt.value.trim() && !processing.value) {
      sendPrompt()
    }
  }
  // 如果按住Shift键+Enter，则允许换行（默认行为）
}

// 页面加载时获取配置
onMounted(() => {
  loadModelConfig()
})
</script>

<style scoped>
.model-test-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #f5f5f5;
}

.test-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.model-info h3 {
  margin: 0 0 8px 0;
  color: #303133;
}

.model-description {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.loading-container {
  padding: 40px;
}

.test-interface {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.model-details {
  background-color: #fafafa;
  padding: 16px;
  border-radius: 8px;
}

.input-section h4,
.conversation-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.input-actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
}

.conversation-section {
  min-height: 400px;
}

.conversation-container {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 16px;
  max-height: 600px;
  overflow-y: auto;
  background-color: #fff;
}

.message-item {
  margin-bottom: 16px;
  padding: 12px;
  border-radius: 8px;
}

.message-item.user {
  background-color: #e1f5fe;
  margin-left: 10%;
}

.message-item.assistant {
  background-color: #f1f8e9;
  margin-right: 10%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.message-role {
  font-weight: 600;
  color: #606266;
}

.message-time {
  color: #909399;
  font-size: 12px;
}

.message-content {
  line-height: 1.6;
}

.message-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  color: #303133;
}

.error-container {
  padding: 40px;
}

@media (max-width: 768px) {
  .model-test-container {
    padding: 10px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .input-actions {
    flex-direction: column;
  }

  .message-item.user,
  .message-item.assistant {
    margin-left: 0;
    margin-right: 0;
  }
}
</style>