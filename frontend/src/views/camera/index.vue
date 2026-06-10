<template>
  <div class="camera-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-title">
        <el-icon><VideoCamera /></el-icon>
        <span>摄像头管理</span>
      </div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增摄像头
      </el-button>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-bar">
      <div class="stat-item stat-item-low">
        <div class="stat-icon">
          <el-icon><SuccessFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.low }}</div>
          <div class="stat-label">低风险</div>
        </div>
      </div>
      <div class="stat-item stat-item-medium">
        <div class="stat-icon">
          <el-icon><WarningFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.medium }}</div>
          <div class="stat-label">中风险</div>
        </div>
      </div>
      <div class="stat-item stat-item-high">
        <div class="stat-icon">
          <el-icon><CircleCloseFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.high }}</div>
          <div class="stat-label">高风险</div>
        </div>
      </div>
      <div class="stat-item stat-item-total">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总摄像头数</div>
        </div>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="cameraList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: 'var(--color-bg-hover)', color: 'var(--color-text-regular)', fontWeight: 'bold' }"
      >
        <template #empty>
          <el-empty description="暂无摄像头数据" :image-size="80" />
        </template>
      <el-table-column prop="cameraId" label="ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small">#{{ row.cameraId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cameraName" label="摄像头名称" min-width="150" align="center">
          <template #default="{ row }">
            <div class="camera-name-info">
              <el-icon><VideoCamera /></el-icon>
              <span>{{ row.cameraName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="180" align="center">
          <template #default="{ row }">
            <div class="location-info">
              <el-icon><Location /></el-icon>
              <span>{{ row.location }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="预警等级" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="large">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="cameraFormRef"
        :model="cameraForm"
        :rules="cameraRules"
        label-width="100px"
      >
        <el-form-item label="摄像头名称" prop="cameraName">
          <el-input v-model="cameraForm.cameraName" placeholder="请输入摄像头名称" />
        </el-form-item>
        
        <el-form-item label="位置" prop="location">
          <el-input v-model="cameraForm.location" placeholder="请输入位置" />
        </el-form-item>
        
        <el-form-item label="预警等级" prop="level">
          <el-select v-model="cameraForm.level" placeholder="请选择预警等级">
            <el-option label="低风险" :value="1" />
            <el-option label="中风险" :value="2" />
            <el-option label="高风险" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="RTSP地址" prop="rtsp">
          <el-input v-model="cameraForm.rtsp" placeholder="请输入RTSP地址" />
        </el-form-item>
        
        <el-form-item label="描述" prop="dtl">
          <el-input
            v-model="cameraForm.dtl"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="摄像头详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentCamera.cameraId }}</el-descriptions-item>
        <el-descriptions-item label="摄像头名称">{{ currentCamera.cameraName }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ currentCamera.location }}</el-descriptions-item>
        <el-descriptions-item label="预警等级">
          <el-tag :type="getLevelType(currentCamera.level)">
            {{ getLevelText(currentCamera.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="RTSP地址" :span="2">
          {{ currentCamera.rtsp || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          {{ currentCamera.dtl || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  VideoCamera,
  Plus,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Document,
  Location,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { 
  getCameraList, 
  addCamera, 
  updateCamera, 
  deleteCamera,
  getCameraDetail
} from '@/api/camera'

// 数据
const loading = ref(false)
const submitLoading = ref(false)
const cameraList = ref([])
const total = ref(0)
const stats = reactive({
  low: 0,
  medium: 0,
  high: 0
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 对话框状态
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增摄像头')
const isEdit = ref(false)

// 表单引用
const cameraFormRef = ref()

// 当前选中的摄像头
const currentCamera = ref({})

// 表单数据
const cameraForm = reactive({
  cameraId: null,
  cameraName: '',
  location: '',
  level: 1,
  dtl: ''
})

// 表单验证规则
const cameraRules = {
  cameraName: [
    { required: true, message: '请输入摄像头名称', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择预警等级', trigger: 'change' }
  ]
}

// 获取摄像头列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCameraList(queryParams)
    const cameras = res.rows || []
    cameraList.value = cameras
    total.value = res.total || 0
    
    // 计算统计数据
    stats.low = cameras.filter(item => item.level === 1).length
    stats.medium = cameras.filter(item => item.level === 2).length
    stats.high = cameras.filter(item => item.level === 3).length
  } catch (error) {
    console.error('获取摄像头列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 新增摄像头
const handleAdd = () => {
  dialogTitle.value = '新增摄像头'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑摄像头
const handleEdit = async (row) => {
  try {
    const res = await getCameraDetail(row.cameraId)
    const detailData = res.data || row
    
    dialogTitle.value = '编辑摄像头'
    isEdit.value = true
    dialogVisible.value = true
    
    // 填充表单数据
    cameraForm.cameraId = detailData.cameraId
    cameraForm.cameraName = detailData.cameraName || ''
    cameraForm.location = detailData.location || ''
    cameraForm.level = detailData.level || 1
    cameraForm.rtsp = detailData.rtsp || ''
    cameraForm.dtl = detailData.dtl || ''
  } catch (error) {
    console.error('获取摄像头详情失败:', error)
    ElMessage.error('获取摄像头详情失败')
    
    // 如果获取详情失败，使用列表中的数据
    dialogTitle.value = '编辑摄像头'
    isEdit.value = true
    dialogVisible.value = true
    
    Object.keys(cameraForm).forEach(key => {
      cameraForm[key] = row[key] || ''
    })
    cameraForm.cameraId = row.cameraId
  }
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getCameraDetail(row.cameraId)
    currentCamera.value = res.data || row
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取摄像头详情失败:', error)
    currentCamera.value = row
    viewDialogVisible.value = true
  }
}

// 删除摄像头
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除摄像头"${row.cameraName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCamera(row.cameraId)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
      getList()
    } catch (error) {
      console.error('删除摄像头失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!cameraFormRef.value) return
  
  await cameraFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          // 编辑
          await updateCamera(cameraForm)
          ElMessage({
            type: 'success',
            message: '更新成功'
          })
        } else {
          // 新增
          await addCamera(cameraForm)
          ElMessage({
            type: 'success',
            message: '添加成功'
          })
        }
        
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 对话框关闭事件
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  if (cameraFormRef.value) {
    cameraFormRef.value.resetFields()
  }
  
  Object.keys(cameraForm).forEach(key => {
    if (key === 'level') {
      cameraForm[key] = 1
    } else {
      cameraForm[key] = ''
    }
  })
  cameraForm.cameraId = null
}

// 获取等级类型
const getLevelType = (level) => {
  const levelMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return levelMap[level] || 'info'
}

// 获取等级文本
const getLevelText = (level) => {
  const levelMap = {
    1: '低风险',
    2: '中风险',
    3: '高风险'
  }
  return levelMap[level] || '未知'
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.camera-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  background: var(--color-bg-hover);
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.operation-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.operation-title .el-icon {
  font-size: 24px;
  color: var(--color-primary);
}

.stats-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.stat-item {
  flex: 1;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-item-low {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.stat-item-medium {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
  box-shadow: 0 4px 12px rgba(250, 173, 20, 0.3);
}

.stat-item-high {
  background: linear-gradient(135deg, #f5222d 0%, #ff4d4f 100%);
  box-shadow: 0 4px 12px rgba(245, 34, 45, 0.3);
}

.stat-item-total {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.stat-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.table-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.el-table {
  flex: 1;
  min-height: 0;
}

.el-table :deep(.el-table__body-wrapper) {
  height: 100% !important;
  overflow-y: auto !important;
}

.el-table :deep(.el-table__row) {
  transition: all 0.3s;
}

.el-table :deep(.el-table__row:hover) {
  background-color: var(--color-bg-active) !important;
}

.camera-name-info {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  color: var(--color-text-regular);
}

.location-info .el-icon {
  color: var(--color-primary);
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
  flex-shrink: 0;
}
</style>