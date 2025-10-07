<template>
  <div class="video-tool-container">
    <el-card class="tool-card">
      <template #header>
        <div class="card-header">
          <span>视频工具</span>
          <div>
            <el-button type="primary" @click="showAddDialog">新增</el-button>
            <el-button type="success" @click="processDownloadVideo" :loading="processingDownload">处理下载视频</el-button>
            <el-button type="warning" @click="processA1Video" :loading="processingA1">处理A1视频</el-button>
            <el-button type="danger" @click="processA2Video" :loading="processingA2">处理A2视频</el-button>
          </div>
        </div>
      </template>

      <!-- 视频记录列表 -->
      <el-table :data="videoList" style="width: 100%" border>
        <el-table-column prop="source" label="视频来源" width="150" />
        <el-table-column prop="url" label="视频URL" width="300" />
        <el-table-column prop="duration" label="视频长度" width="120" :formatter="formatDuration" />
        <el-table-column prop="textContent" label="视频文本" width="200" show-overflow-tooltip />
        <el-table-column prop="summary" label="摘要信息" width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              @click="viewSummary(scope.row)"
            >查看</el-button>
            <el-button
              size="small"
              type="danger"
              @click="deleteRecord(scope.row.id)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="50%"
        :before-close="handleClose"
      >
        <el-form :model="videoForm" label-width="100px">
          <el-form-item label="视频来源">
            <el-input v-model="videoForm.source" placeholder="请输入视频来源" />
          </el-form-item>
          <el-form-item label="视频URL">
            <el-input
              v-model="videoForm.url"
              placeholder="请输入本地视频文件路径或在线视频地址"
            />
          </el-form-item>
          <el-form-item label="本地上传">
            <el-upload
              drag
              :auto-upload="false"
              :show-file-list="true"
              :on-change="handleUploadChange"
              accept="video/*"
              v-model:file-list="uploadFileList"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将视频文件拖到此处，或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持常见视频格式，如 MP4, AVI, MOV 等
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="processVideo" :loading="processing">处理</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 处理进度 -->
      <div class="progress-section" v-if="processing">
        <el-steps :active="currentStep" finish-status="success" simple>
          <el-step title="上传视频" />
          <el-step title="提取音频" />
          <el-step title="转录文本" />
          <el-step title="生成摘要" />
        </el-steps>

        <div class="step-content">
          <div v-if="currentStep === 1">
            <p>正在选择/上传视频...</p>
          </div>
          <div v-if="currentStep === 2">
            <p>正在使用本地whisper.cpp模型提取音频...</p>
            <p>模型路径: ggml-base.bin</p>
          </div>
          <div v-if="currentStep === 3">
            <p>正在转录音频为文本...</p>
            <p v-if="transcriptionText">{{ transcriptionText }}</p>
          </div>
          <div v-if="currentStep === 4">
            <p>正在调用默认大模型生成摘要...</p>
          </div>
        </div>
      </div>

      <!-- 摘要预览对话框 -->
      <el-dialog
        v-model="summaryVisible"
        title="摘要详情"
        width="70%"
        top="5vh"
      >
        <div v-html="currentSummaryHtml" class="summary-content-preview"></div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="summaryVisible = false">关闭</el-button>
            <el-button type="primary" @click="downloadSummary">下载摘要</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 视频处理结果预览对话框 -->
      <el-dialog
        v-model="showProcessResult"
        :title="processTitle"
        width="70%"
        top="5vh"
      >
        <div class="result-content-preview">
          <pre>{{ processResult }}</pre>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showProcessResult = false">关闭</el-button>
            <el-button type="primary" @click="downloadProcessResult">下载结果</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile, UploadUserFile } from 'element-plus'
import { VideoProcessingService } from './service'

interface VideoToTextRecord {
  id: number
  source: string
  url: string
  duration: number | null
  textContent: string
  summary: string
  createdAt: string
  updatedAt: string
}

interface VideoForm {
  source: string
  url: string
}

interface FileInfo {
  name: string
  path: string
  size: number
}

// 数据相关
const videoList = ref<VideoToTextRecord[]>([])
const dialogVisible = ref(false)
const summaryVisible = ref(false)
const dialogTitle = ref('新增视频')
const videoForm = ref<VideoForm>({
  source: '',
  url: '',
})
const currentSummaryHtml = ref('')
const uploadFileList = ref<UploadUserFile[]>([])

// 流程控制
const processing = ref(false)
const processingDownload = ref(false)
const processingA1 = ref(false)
const processingA2 = ref(false)
const currentStep = ref(0)
const transcriptionText = ref('')

// 显示处理结果
const showProcessResult = ref(false)
const processResult = ref('')
const processTitle = ref('处理结果')

// 文件处理
const selectedFile = ref<FileInfo | null>(null)

// 格式化时间
const formatDate = (row: VideoToTextRecord, column: any, cellValue: string) => {
  // 尝试解析字符串格式的时间
  try {
    const date = new Date(cellValue);
    return cellValue && !isNaN(date.getTime()) ? date.toLocaleString() : cellValue;
  } catch {
    return cellValue || '-';
  }
};

// 格式化持续时间
const formatDuration = (row: VideoToTextRecord, column: any, cellValue: number | null) => {
  if (!cellValue) return '-'
  const minutes = Math.floor(cellValue / 60)
  const seconds = Math.floor(cellValue % 60)
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

// 接口调用函数
const fetchVideoList = async () => {
  try {
    // 从后端API获取视频记录列表
    const response = await fetch('/api/video-to-text')

    if (!response.ok) {
      throw new Error(`获取视频记录失败: ${response.status}`)
    }

    const data = await response.json()
    videoList.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('获取视频记录失败:', error)
    ElMessage.error('获取视频记录失败: ' + (error as Error).message)
  }
}

// 初始化页面
onMounted(() => {
  fetchVideoList()
})

// 新增/编辑对话框
const showAddDialog = () => {
  dialogTitle.value = '新增视频'
  videoForm.value = {
    source: '',
    url: '',
  }
  selectedFile.value = null
  uploadFileList.value = []
  dialogVisible.value = true
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetDialog()
}

// 重置对话框
const resetDialog = () => {
  videoForm.value = {
    source: '',
    url: '',
  }
  uploadFileList.value = []
  selectedFile.value = null
  currentStep.value = 0
}

// 文件上传处理
const handleUploadChange = (file: UploadFile, files: UploadUserFile[]) => {
  if (files.length > 1) {
    uploadFileList.value = [file]
  }

  // 保存文件信息
  selectedFile.value = {
    name: file.name,
    path: (file.raw as any)?.path || file.name,
    size: file.size || 0
  }
}

// 主要视频处理流程 - 修改为从对话框中处理
const processVideo = async () => {
  if (!selectedFile.value && !videoForm.value.url) {
    ElMessage.warning('请至少输入视频URL或上传一个本地视频')
    return
  }

  processing.value = true
  currentStep.value = 1
  transcriptionText.value = ''

  try {
    // 确保source不为空
    if (!videoForm.value.source) {
      videoForm.value.source = selectedFile.value ? '本地上传' : '在线视频'
    }

    let result: { text: string, recordId: number };

    // 步骤1: 显示当前状态
    currentStep.value = 1
    await new Promise(resolve => setTimeout(resolve, 500))

    // 根据是上传文件还是URL来选择不同的处理方式
    if (selectedFile.value) {
      // 使用上传的文件
      currentStep.value = 1
      ElMessage.info('正在上传视频文件...')
      console.log('Uploading file:', selectedFile.value)

      // 获取原始文件对象
      const rawFile = uploadFileList.value[0]?.raw;
      if (!rawFile) {
        throw new Error('无法获取上传的文件');
      }

      // 使用上传服务方法
      result = await VideoProcessingService.uploadAndExtractAudioText(rawFile);

      // 使用原始文件名作为URL参考
      videoForm.value.url = selectedFile.value.name;
    } else if (videoForm.value.url) {
      // 使用URL路径
      currentStep.value = 2
      ElMessage.info('正在使用本地whisper.cpp模型处理...')
      console.log('Processing video path:', videoForm.value.url)
      result = await VideoProcessingService.extractAudioText(videoForm.value.url)
    } else {
      throw new Error('无法确定视频源');
    }

    // 保存到编辑表单的数据
    if (result.text) {
      transcriptionText.value = result.text
    }

    // 记录ID（如果从数据库获取）
    console.log('Record ID:', result.recordId)
    currentStep.value = 3

    // 步骤3: 显示转录文本
    await new Promise(resolve => setTimeout(resolve, 500))
    currentStep.value = 4

    // 步骤4: 调用默认大模型生成摘要
    ElMessage.info('正在调用大模型生成摘要...')
    console.log('Generating summary for text:', result.text)
    const summary = await VideoProcessingService.generateSummary(result.text, result.recordId)
    console.log('Generated summary:', summary)

    // 获取更新后的列表
    await fetchVideoList()

    ElMessage.success('视频处理完成！')
  } catch (error) {
    console.error('处理视频时出错:', error)
    ElMessage.error('处理视频时出错: ' + (error as Error).message)
  } finally {
    processing.value = false
    currentStep.value = 0
    dialogVisible.value = false
    resetDialog()
  }
}

// 查看摘要详情
const viewSummary = async (row: VideoToTextRecord) => {
  // 如果摘要已存在，直接显示
  if (row.summary) {
    currentSummaryHtml.value = row.summary
  } else if (row.textContent) {
    // 如果没有摘要但有文本内容，可以使用内容生成一个简单版
    ElMessage.info('调用AI生成摘要...')
    try {
      const summary = await VideoProcessingService.generateSummary(row.textContent, row.id)
      currentSummaryHtml.value = summary
      // 更新列表以反映已生成的摘要
      await fetchVideoList()
    } catch (error) {
      console.error('生成摘要失败:', error)
      currentSummaryHtml.value = '<p>无法生成摘要: ' + (error as Error).message + '</p>'
    }
  }

  summaryVisible.value = true
}

// 删除记录
const deleteRecord = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认删除该视频记录？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await fetch(`/api/video-to-text/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      throw new Error(`删除失败: ${response.status}`)
    }

    // 更新列表
    await fetchVideoList()
    ElMessage.success('删除成功')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败: ' + (err as Error).message)
    }
  }
}

// 处理下载视频
const processDownloadVideo = async () => {
  processingDownload.value = true
  try {
    ElMessage.info('正在处理下载视频...')

    // 调用后端API处理下载视频
    const response = await fetch('/api/video-tool/test-download-video-to-txt', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || '处理下载视频失败')
    }

    const result = await response.json()

    // 检查响应中是否有转录文本
    if (result.text) {
      processResult.value = result.text;
      showProcessResult.value = true;
      processTitle.value = "下载视频转录结果";
    }

    ElMessage.success(result.message)

    // 更新视频列表
    await fetchVideoList()
  } catch (error) {
    console.error('处理下载视频时出错:', error)
    ElMessage.error('处理下载视频时出错: ' + (error as Error).message)
  } finally {
    processingDownload.value = false
  }
}

// 下载摘要
const downloadSummary = () => {
  VideoProcessingService.saveFile(
    `
    <html>
      <head>
        <meta charset="utf-8">
        <title>视频摘要</title>
      </head>
      <body>
        ${currentSummaryHtml.value}
      </body>
    </html>
    `,
    '视频摘要.html',
    'text/html'
  )
  ElMessage.success('摘要已下载')
}

// 处理A1视频
const processA1Video = async () => {
  processingA1.value = true
  try {
    ElMessage.info('正在处理A1视频...')

    // 调用后端API处理A1视频
    const response = await fetch('/api/video-tool/process-a1-video-to-txt', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || '处理A1视频失败')
    }

    const result = await response.json()

    // 检查响应中是否有转录文本
    if (result.text) {
      processResult.value = result.text;
      showProcessResult.value = true;
      processTitle.value = "A1视频转录结果";
    }

    ElMessage.success(result.message)

    // 更新视频列表
    await fetchVideoList()
  } catch (error) {
    console.error('处理A1视频时出错:', error)
    ElMessage.error('处理A1视频时出错: ' + (error as Error).message)
  } finally {
    processingA1.value = false
  }
}

// 处理A2视频
const processA2Video = async () => {
  processingA2.value = true
  try {
    ElMessage.info('正在处理A2视频...')

    // 调用后端API处理A2视频
    const response = await fetch('/api/video-tool/process-a2-video-to-txt', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || '处理A2视频失败')
    }

    const result = await response.json()

    // 检查响应中是否有转录文本
    if (result.text) {
      processResult.value = result.text;
      showProcessResult.value = true;
      processTitle.value = "A2视频转录结果";
    }

    ElMessage.success(result.message)

    // 更新视频列表
    await fetchVideoList()
  } catch (error) {
    console.error('处理A2视频时出错:', error)
    ElMessage.error('处理A2视频时出错: ' + (error as Error).message)
  } finally {
    processingA2.value = false
  }
}

// 下载处理结果
const downloadProcessResult = () => {
  VideoProcessingService.saveFile(
    processResult.value,
    `${processTitle.value}.txt`,
    'text/plain'
  )
  ElMessage.success('处理结果已下载')
}
</script>

<style scoped>
.video-tool-container {
  width: 100%;
  height: 100%;
}

.tool-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-section {
  margin-bottom: 30px;
}

.process-section {
  margin-top: 20px;
  text-align: center;
}

.progress-section {
  margin: 30px 0;
}

.step-content {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.step-content p {
  margin: 5px 0;
}

.result-section {
  margin-top: 30px;
}

.summary-content {
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  min-height: 200px;
}

.summary-content ol {
  margin: 10px 0;
  padding-left: 20px;
}

.summary-content li {
  margin: 8px 0;
}

.result-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.summary-content-preview {
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  min-height: 300px;
  max-height: 60vh;
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .tool-card {
    margin: 0 10px;
  }

  .result-actions {
    flex-direction: column;
  }
}

.result-content-preview {
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  min-height: 300px;
  max-height: 60vh;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Consolas, monospace;
  font-size: 14px;
  line-height: 1.5;
}
</style>