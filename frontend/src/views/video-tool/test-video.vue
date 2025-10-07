<template>
  <div class="test-video-container">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>测试视频转文本功能</span>
        </div>
      </template>

      <div class="content" data-testid="descriptions-content">
        <el-descriptions class="margin-top descriptions" :column="1" border>
          <el-descriptions-item data-testid="test-video-info" label="测试视频">
            hr_ai_project\src\main\resources\vide\ef6730ac-c64d-406a-bcda-ea3d015781af.mp4
          </el-descriptions-item>
          <el-descriptions-item data-testid="model-info" label="模型">
            ggml-base.bin (whisper.cpp本地模型)
          </el-descriptions-item>
          <el-descriptions-item data-testid="transcription-info" label="转录方式">
            使用本地whisper.cpp模型进行转录
          </el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-button
            type="primary"
            @click="processTestVideo"
            :loading="loading"
            size="large"
            :icon="VideoPlay"
            data-testid="process-test-video-btn"
          >
            {{ loading ? '正在处理中...' : '开始处理测试视频' }}
          </el-button>
          <p class="tips" data-testid="processing-tips">
            此功能将使用ggml-base.bin本地模型处理指定的测试视频，转录完成后会显示结果文本。
          </p>
        </div>

        <!-- 进度显示 -->
        <div class="progress-container" v-if="showProgress">
          <el-steps :active="currentStep" finish-status="success" simple>
            <el-step title="准备视频" />
            <el-step title="加载模型" />
            <el-step title="音频提取" />
            <el-step title="语音转录" />
          </el-steps>
        </div>

        <!-- 结果展示 -->
        <div class="result-container" v-if="resultText">
          <h3>转录结果：</h3>
          <div class="result-text">
            <pre>{{ resultText }}</pre>
          </div>
          <div class="result-actions">
            <el-button @click="copyResult" :icon="CopyDocument">复制文本</el-button>
            <el-button @click="downloadResult" :icon="Download">下载文本</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { VideoCameraFilled, Clock, Collection, VideoPlay, CopyDocument, Download } from '@element-plus/icons-vue'

// 状态变量
const loading = ref(false)
const showProgress = ref(false)
const resultText = ref('')
const currentStep = ref(0)

// 处理测试视频
const processTestVideo = async () => {
  loading.value = true
  showProgress.value = true
  resultText.value = ''
  currentStep.value = 0

  try {
    // 调用后端API处理测试视频
    const videoPath = 'hr_ai_project/src/main/resources/vide/ef6730ac-c64d-406a-bcda-ea3d015781af.mp4'

    // 第一步: 准备视频信息
    currentStep.value = 1
    ElMessage.info('已开始处理测试视频...')
    await new Promise(resolve => setTimeout(resolve, 500))

    // 第二步: 加载模型
    currentStep.value = 2
    ElMessage.info('加载whisper.cpp模型中...')
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 第三步: 提取音频
    currentStep.value = 3
    ElMessage.info('正在提取视频音频...')
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 第四步: 进行语音转录
    currentStep.value = 4
    ElMessage.info('使用本地whisper.cpp模型进行转录...')

    const response = await fetch('/api/video-tool/extract-text', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        videoPath: videoPath
      })
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
    }

    const data = await response.json()
    resultText.value = data.text || ''
    ElMessage.success('视频转录完成！')

    ElNotification({
      title: '处理成功',
      message: '视频转文本处理完成！',
      type: 'success',
      duration: 3000
    })
  } catch (error) {
    console.error('处理测试视频失败:', error)
    ElMessage.error('处理测试视频失败: ' + (error as Error).message)
  } finally {
    loading.value = false
    showProgress.value = false
  }
}

// 复制结果
const copyResult = () => {
  if (resultText.value) {
    navigator.clipboard.writeText(resultText.value)
      .then(() => {
        ElMessage.success('文本已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制')
      })
  }
}

// 下载结果
const downloadResult = () => {
  if (resultText.value) {
    const blob = new Blob([resultText.value], { type: 'text/plain;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '视频转录结果.txt'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success('文件已下载')
  }
}
</script>

<style scoped>
.test-video-container {
  padding: 20px;
}

.test-card {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.content {
  padding: 20px 0;
}

.margin-top {
  margin-top: 20px;
  margin-bottom: 30px;
}

.cell-item {
  display: flex;
  align-items: center;
}

.actions {
  text-align: center;
  margin: 30px 0;
}

.actions .tips {
  margin-top: 15px;
  color: #606266;
  font-size: 14px;
}

.progress-container {
  margin: 30px 0;
}

.result-container {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fafafa;
}

.result-text {
  margin: 20px 0;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
}

.result-text pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  line-height: 1.6;
}

.result-actions {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}
</style>