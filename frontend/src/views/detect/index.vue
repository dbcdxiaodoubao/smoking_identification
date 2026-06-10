<template>
  <div class="detect-container">
    <el-row :gutter="20">
      <!-- 左侧上传区域 -->
      <el-col :span="12">
        <el-card shadow="hover" class="upload-card">
          <template #header>
            <div class="card-header">
              <span>图片上传</span>
              <el-tag type="info">支持 JPG、PNG 格式</el-tag>
            </div>
          </template>
          
          <div class="upload-area">
            <el-upload
              ref="uploadRef"
              class="upload-demo"
              drag
              :action="uploadAction"
              :headers="uploadHeaders"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :show-file-list="false"
              accept="image/*"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将图片拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传 jpg/png 文件，且不超过 10MB
                </div>
              </template>
            </el-upload>
          </div>
          
          <!-- 上传后的图片预览 -->
          <div v-if="previewImage" class="preview-area">
            <el-divider>图片预览</el-divider>
            <el-image
              :src="previewImage"
              style="width: 100%; max-height: 300px"
              fit="contain"
            />
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧检测结果区域 -->
      <el-col :span="12">
        <el-card shadow="hover" class="result-card">
          <template #header>
            <div class="card-header">
              <span>检测结果</span>
              <el-button 
                v-if="detectResult" 
                type="primary" 
                size="small" 
                @click="handleSaveResult"
              >
                保存结果
              </el-button>
            </div>
          </template>
          
          <!-- 检测中状态 -->
          <div v-if="detecting" class="detecting-status">
            <el-icon class="is-loading"><loading /></el-icon>
            <p>正在检测中，请稍候...</p>
          </div>
          
          <!-- 检测结果 -->
          <div v-else-if="detectResult" class="result-content">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="检测ID">
                {{ detectResult.detectId }}
              </el-descriptions-item>
              <el-descriptions-item label="摄像头ID">
                {{ detectResult.cameraId || '手动上传' }}
              </el-descriptions-item>
              <el-descriptions-item label="位置">
                {{ detectResult.location || '未知位置' }}
              </el-descriptions-item>
              <el-descriptions-item label="预警等级">
                <el-tag :type="getLevelType(detectResult.level)">
                  {{ getLevelText(detectResult.level) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="检测时间">
                {{ formatTime(detectResult.createTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="图片URL">
                <el-link 
                  v-if="detectResult.pictureUrl" 
                  :href="convertImageUrl(detectResult.pictureUrl)" 
                  target="_blank"
                  type="primary"
                >
                  查看图片
                </el-link>
                <span v-else>无图片</span>
              </el-descriptions-item>
            </el-descriptions>
            
            <!-- 检测图片 -->
            <div v-if="detectResult.pictureUrl" class="result-image">
              <el-divider>检测图片</el-divider>
              <el-image
                :src="convertImageUrl(detectResult.pictureUrl)"
                style="width: 100%; max-height: 300px"
                fit="contain"
                :preview-src-list="[convertImageUrl(detectResult.pictureUrl)]"
              />
            </div>
          </div>
          
          <!-- 无结果状态 -->
          <div v-else class="no-result">
            <el-empty description="暂无检测结果" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 历史检测记录 -->
    <el-row class="mt-20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>历史检测记录</span>
              <el-button type="primary" @click="handleRefreshHistory">
                <el-icon><refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <el-table
            v-loading="historyLoading"
            :data="historyList"
            border
            style="width: 100%"
          >
            <el-table-column prop="detectId" label="检测ID" width="100" />
            <el-table-column prop="cameraId" label="摄像头ID" width="100" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="level" label="预警等级" width="120">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)">
                  {{ getLevelText(row.level) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="检测时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleViewHistory(row)">
                  查看详情
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteHistory(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="historyParams.pageNum"
              v-model:page-size="historyParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="historyTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleHistorySizeChange"
              @current-change="handleHistoryCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 保存结果对话框 -->
    <el-dialog v-model="saveDialogVisible" title="保存检测结果" width="500px">
      <el-form
        ref="saveFormRef"
        :model="saveForm"
        :rules="saveRules"
        label-width="100px"
      >
        <el-form-item label="摄像头ID" prop="cameraId">
          <el-input v-model="saveForm.cameraId" placeholder="请输入摄像头ID" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="saveForm.location" placeholder="请输入位置信息" />
        </el-form-item>
        <el-form-item label="预警等级" prop="level">
          <el-select v-model="saveForm.level" placeholder="请选择预警等级">
            <el-option label="低风险" :value="1" />
            <el-option label="中风险" :value="2" />
            <el-option label="高风险" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="saveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UploadFilled, 
  Loading, 
  Refresh 
} from '@element-plus/icons-vue'
import { detectApi } from '@/api/detection'
import { convertImageUrl } from '@/utils/imageUrl'

// 上传相关
const uploadRef = ref()
const previewImage = ref('')
const detecting = ref(false)
const detectResult = ref(null)

// 上传配置
const uploadAction = computed(() => {
  return '/api/detect/upload' // 实际项目中应该从配置中获取
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    'Authorization': `Bearer ${token}`
  }
})

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }

  // 预览图片
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
  reader.readAsDataURL(file)

  detecting.value = true
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  detecting.value = false
  if (response.code === 200) {
    // 转换图片URL
    if (response.data && response.data.pictureUrl) {
      response.data.pictureUrl = convertImageUrl(response.data.pictureUrl)
    }
    detectResult.value = response.data
    ElMessage.success('图片检测完成!')
  } else {
    ElMessage.error(response.message || '检测失败')
  }
}

// 上传失败
const handleUploadError = () => {
  detecting.value = false
  ElMessage.error('图片上传失败，请重试')
}

// 历史记录
const historyLoading = ref(false)
const historyList = ref([])
const historyTotal = ref(0)
const historyParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 获取历史记录
const getHistoryList = async () => {
  historyLoading.value = true
  try {
    const response = await detectApi.getHistory(historyParams)
    if (response.code === 200) {
      // 转换历史记录中的图片URL
      const records = response.data.records || []
      historyList.value = records.map(record => {
        if (record.pictureUrl) {
          record.pictureUrl = convertImageUrl(record.pictureUrl)
        }
        return record
      })
      historyTotal.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取历史记录失败')
    }
  } catch (error) {
    ElMessage.error('获取历史记录失败')
  } finally {
    historyLoading.value = false
  }
}

// 刷新历史记录
const handleRefreshHistory = () => {
  getHistoryList()
}

// 分页处理
const handleHistorySizeChange = (val) => {
  historyParams.pageSize = val
  historyParams.pageNum = 1
  getHistoryList()
}

const handleHistoryCurrentChange = (val) => {
  historyParams.pageNum = val
  getHistoryList()
}

// 查看历史详情
const handleViewHistory = (row) => {
  detectResult.value = row
  previewImage.value = convertImageUrl(row.pictureUrl) || ''
}

// 删除历史记录
const handleDeleteHistory = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条检测记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await detectApi.deleteHistory(row.detectId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getHistoryList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存结果
const saveDialogVisible = ref(false)
const saveFormRef = ref()
const saveForm = reactive({
  cameraId: '',
  location: '',
  level: 1
})

const saveRules = {
  cameraId: [
    { required: true, message: '请输入摄像头ID', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置信息', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择预警等级', trigger: 'change' }
  ]
}

const handleSaveResult = () => {
  saveForm.cameraId = detectResult.value.cameraId || ''
  saveForm.location = detectResult.value.location || ''
  saveForm.level = detectResult.value.level || 1
  saveDialogVisible.value = true
}

const handleConfirmSave = async () => {
  if (!saveFormRef.value) return
  
  try {
    await saveFormRef.value.validate()
    
    const response = await detectApi.saveResult({
      detectId: detectResult.value.detectId,
      ...saveForm
    })
    
    if (response.code === 200) {
      ElMessage.success('保存成功')
      saveDialogVisible.value = false
      getHistoryList()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 工具函数
const getLevelType = (level) => {
  const types = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = {
    1: '低风险',
    2: '中风险',
    3: '高风险'
  }
  return texts[level] || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString()
}

// 初始化
onMounted(() => {
  getHistoryList()
})
</script>

<style scoped>
.detect-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
}

.upload-card, .result-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-area {
  text-align: center;
  padding: 20px 0;
}

.preview-area {
  margin-top: 20px;
}

.detecting-status {
  text-align: center;
  padding: 40px 0;
}

.detecting-status .el-icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 10px;
}

.result-content {
  padding: 10px 0;
}

.result-image {
  margin-top: 20px;
}

.no-result {
  text-align: center;
  padding: 40px 0;
}

.mt-20 {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>