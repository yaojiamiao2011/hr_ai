import { ElMessage } from 'element-plus'

/**
 * 视频处理服务
 */
export class VideoProcessingService {
  /**
   * 上传视频文件并提取音频文本
   * @param file 视频文件
   * @returns 提取的文本内容和记录ID
   */
  static async uploadAndExtractAudioText(file: File): Promise<{ text: string, recordId: number }> {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch('/api/video-tool/upload-and-process', {
        method: 'POST',
        body: formData
        // 注意：不要设置 Content-Type，让浏览器自动设置
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
      }

      const data = await response.json();
      return {
        text: data.text || '',
        recordId: data.recordId || 0
      }
    } catch (error) {
      console.error('上传并提取音频文本失败:', error);
      throw new Error('上传并提取音频文本失败: ' + (error as Error).message);
    }
  }

  /**
   * 使用本地whisper.cpp模型提取音频文本
   * @param videoPath 视频文件路径
   * @returns 提取的文本内容和记录ID
   */
  static async extractAudioText(videoPath: string): Promise<{ text: string, recordId: number }> {
    try {
      // 调用后端API处理视频
      const response = await fetch('/api/video-tool/extract-text', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ videoPath })
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
      }

      const data = await response.json()
      return {
        text: data.text || '',
        recordId: data.recordId || 0
      }
    } catch (error) {
      console.error('提取音频文本失败:', error)
      throw new Error('提取音频文本失败: ' + (error as Error).message)
    }
  }

  /**
   * 调用默认大模型生成摘要
   * @param text 输入文本
   * @returns 生成的HTML格式摘要
   */
  static async generateSummary(text: string, recordId?: number): Promise<string> {
    try {
      // 调用后端API生成摘要
      const payload: any = { text };
      if (recordId) {
        payload.recordId = recordId;
      }

      const response = await fetch('/api/video-tool/generate-summary', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
      }

      const data = await response.json()
      return data.summary || data.content || ''
    } catch (error) {
      console.error('生成摘要失败:', error)
      throw new Error('生成摘要失败: ' + (error as Error).message)
    }
  }

  /**
   * 保存文件到本地
   * @param content 文件内容
   * @param filename 文件名
   * @param mimeType 文件类型
   */
  static saveFile(content: string, filename: string, mimeType: string = 'text/plain') {
    const blob = new Blob([content], { type: mimeType })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }
}