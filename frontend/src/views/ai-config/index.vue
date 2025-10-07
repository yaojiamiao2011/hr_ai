<template>
  <div class="ai-config-container">
    <!-- 配置列表 -->
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>大模型配置</span>
          <el-button type="primary" @click="openDialog('add')">
            <el-icon><Plus /></el-icon>
            添加配置
          </el-button>
        </div>
      </template>

      <el-table :data="configList" border style="width: 100%">
        <el-table-column prop="name" label="配置名称" width="150" />
        <el-table-column prop="model" label="模型类型" width="120" />
        <el-table-column prop="modelId" label="模型ID" width="150" />
        <el-table-column prop="apiEndpoint" label="API端点" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row, $index }">
            <el-button size="small" @click="openDialog('edit', row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 'active' ? 'warning' : 'success'"
              @click="toggleStatus(row, $index)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="openTestDialog(row)"
              v-if="row.status === 'active'"
            >
              测试
            </el-button>
            <el-button
              size="small"
              :type="row.isDefault ? 'success' : 'default'"
              @click="setDefaultConfig(row, $index)"
              :disabled="row.isDefault"
            >
              {{ row.isDefault ? '已是默认' : '设为默认' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row, $index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加配置' : '编辑配置'"
      width="600px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="模型类型" prop="model">
          <el-select v-model="formData.model" placeholder="请选择模型类型" style="width: 100%">
            <el-option label="GPT-4" value="gpt-4" />
            <el-option label="GPT-3.5-turbo" value="gpt-3.5-turbo" />
            <el-option label="Claude-3" value="claude-3" />
            <el-option label="LLaMA-3" value="llama-3" />
            <el-option label="文心一言" value="wenxin" />
            <el-option label="通义千问" value="qianwen" />
          </el-select>
        </el-form-item>
        <el-form-item label="模型ID" prop="modelId">
          <el-input v-model="formData.modelId" placeholder="请输入模型ID" />
        </el-form-item>
        <el-form-item label="API端点" prop="apiEndpoint">
          <el-input v-model="formData.apiEndpoint" placeholder="请输入API端点" />
        </el-form-item>
        <el-form-item label="API密钥" prop="apiKey">
          <el-input
            v-model="formData.apiKey"
            placeholder="请输入API密钥"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="超时时间" prop="timeout">
          <el-input-number
            v-model="formData.timeout"
            :min="1000"
            :max="600000"
            :step="1000"
            placeholder="超时时间(毫秒)"
          />
        </el-form-item>
        <el-form-item label="最大token" prop="maxTokens">
          <el-input-number
            v-model="formData.maxTokens"
            :min="100"
            :max="100000"
            :step="100"
            placeholder="最大token数"
          />
        </el-form-item>
        <el-form-item label="温度参数" prop="temperature">
          <el-slider
            v-model="formData.temperature"
            :min="0"
            :max="2"
            :step="0.1"
            show-input
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入配置描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            active-value="active"
            inactive-value="inactive"
            active-color="#67C23A"
            inactive-color="#F56C6C"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

interface ConfigItem {
  id?: number
  name: string
  model: string
  modelId: string
  apiEndpoint: string
  apiKey: string
  timeout: number
  maxTokens: number
  temperature: number
  description: string
  status: 'active' | 'inactive'
  createTime?: string
}

const router = useRouter()
const configList = ref<ConfigItem[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()

const formData = reactive<ConfigItem>({
  name: '',
  model: '',
  modelId: '',
  apiEndpoint: '',
  apiKey: '',
  timeout: 30000,
  maxTokens: 4096,
  temperature: 0.7,
  description: '',
  status: 'inactive'
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入配置名称', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请选择模型类型', trigger: 'change' }
  ],
  modelId: [
    { required: true, message: '请输入模型ID', trigger: 'blur' }
  ],
  apiEndpoint: [
    { required: true, message: '请输入API端点', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入API密钥', trigger: 'blur' }
  ],
  timeout: [
    { required: true, message: '请输入超时时间', trigger: 'blur' }
  ],
  maxTokens: [
    { required: true, message: '请输入最大token数', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '请设置温度参数', trigger: 'blur' }
  ]
}

const openDialog = (type: 'add' | 'edit', row?: ConfigItem) => {
  dialogType.value = type
  if (type === 'edit' && row) {
    Object.assign(formData, row)
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(formData, {
    name: '',
    model: '',
    modelId: '',
    apiEndpoint: '',
    apiKey: '',
    timeout: 30000,
    maxTokens: 4096,
    temperature: 0.7,
    description: '',
    status: 'inactive'
  })
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      if (dialogType.value === 'add') {
        const newConfig = { ...formData, id: Date.now(), createTime: new Date().toLocaleString('zh-CN') }
        configList.value.push(newConfig)
        ElMessage.success('添加配置成功')
      } else {
        const index = configList.value.findIndex(item => item.id === formData.id)
        if (index !== -1) {
          configList.value[index] = { ...formData }
          ElMessage.success('编辑配置成功')
        }
      }
      dialogVisible.value = false
      resetForm()
    }
  })
}

const toggleStatus = (row: ConfigItem, index: number) => {
  configList.value[index].status = row.status === 'active' ? 'inactive' : 'active'
  ElMessage.success(`${row.status === 'active' ? '禁用' : '启用'}成功`)
}

const setDefaultConfig = async (row: ConfigItem, index: number) => {
  try {
    const response = await fetch(`/api/ai-config/set-default/${row.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (response.ok) {
      // 更新所有配置项的默认状态
      configList.value.forEach((config, i) => {
        configList.value[i].isDefault = config.id === row.id
      })
      ElMessage.success('设置默认大模型成功')
    } else {
      ElMessage.error('设置默认大模型失败')
    }
  } catch (error) {
    console.error('设置默认大模型失败:', error)
    ElMessage.error('设置默认大模型失败')
  }
}

const handleDelete = async (row: ConfigItem, index: number) => {
  ElMessageBox.confirm(
    '确定要删除该配置吗？此操作不可恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/ai-config/${row.id}`, {
        method: 'DELETE'
      })

      if (response.ok) {
        configList.value.splice(index, 1)
        ElMessage.success('删除配置成功')
      } else {
        ElMessage.error('删除配置失败')
      }
    } catch (error) {
      console.error('删除配置失败:', error)
      ElMessage.error('删除配置失败')
    }
  })
}

const openTestDialog = (row: ConfigItem) => {
  try {
    // 使用 router.resolve 构建测试页面URL
    const routeUrl = router.resolve({
      name: 'AIConfigTest',
      params: { id: row.id?.toString() }
    })

    // 在新标签页中打开测试页面
    window.open(routeUrl.href, '_blank')
  } catch (error) {
    console.error('打开测试页面失败:', error)
    ElMessage.error('打开测试页面失败')
  }
}

const fetchConfigs = async () => {
  try {
    const response = await fetch('/api/ai-config')
    if (response.ok) {
      const data = await response.json()
      configList.value = data.map((item: any) => ({
        id: item.id,
        name: item.provider + ' ' + item.modelName,
        model: item.provider.toLowerCase(),
        modelId: item.modelName,
        apiEndpoint: item.modelEndpoint,
        apiKey: item.apiKey,
        timeout: 30000,
        maxTokens: 4096,
        temperature: 0.7,
        description: item.provider + '大模型配置',
        status: item.isActive ? 'active' : 'inactive',
        isDefault: item.isDefault || false,
        createTime: new Date(item.createdAt).toLocaleString('zh-CN')
      }))
    }
  } catch (error) {
    console.error('获取配置失败:', error)
    ElMessage.error('获取配置失败')
  }
}

onMounted(() => {
  fetchConfigs()
})
</script>

<style scoped>
.ai-config-container {
  width: 100%;
  height: 100%;
}

@media (max-width: 768px) {
  .config-card :deep(.el-card__body) {
    padding: 10px;
  }
}

@media (max-width: 576px) {
  .el-dialog {
    width: 90% !important;
  }

  .el-form-item {
    margin-bottom: 15px;
  }
}

.config-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>