<template>
  <div class="dispose-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-title">
        <el-icon><DocumentChecked /></el-icon>
        <span>处理事件管理</span>
      </div>
      <el-button type="primary" @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon">
          <el-icon><DocumentChecked /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总处理数</div>
        </div>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="disposeList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: 'var(--color-bg-hover)', color: 'var(--color-text-regular)', fontWeight: 'bold' }"
      >
        <template #empty>
          <el-empty description="暂无处理记录" :image-size="80" />
        </template>
        <el-table-column prop="disposeId" label="处理ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small">#{{ row.disposeId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="incidentId" label="预警事件ID" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="warning" size="small">{{ row.incidentId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="处理人ID" width="120" align="center">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" icon="User" />
              <span>{{ row.userId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="处理时间" min-width="180" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看详情
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
    
    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="处理事件详情" width="800px" :close-on-click-modal="false">
      <div class="detail-content">
        <!-- 处理事件基本信息 -->
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <div class="card-header">
              <el-icon><DocumentChecked /></el-icon>
              <span>处理信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="处理ID">
              <el-tag type="primary" size="large">#{{ currentDispose.disposeId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预警事件ID">
              <el-tag type="warning" size="large">{{ currentDispose.incidentId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理人ID">
              <div class="user-info-detail">
                <el-avatar :size="36" icon="User" />
                <span>{{ currentDispose.userId }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="处理时间">
              <div class="time-info-detail">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(currentDispose.createTime) }}</span>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <!-- 关联的预警事件信息 -->
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <div class="card-header">
              <el-icon><Warning /></el-icon>
              <span>关联预警事件</span>
            </div>
          </template>
          <div v-if="relatedIncidentLoading" class="loading-section">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="relatedIncident" class="incident-detail">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="事件ID">
                <el-tag type="warning">{{ relatedIncident.incidentId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="预警等级">
                <el-tag :type="getLevelType(relatedIncident.level)" size="large">
                  {{ getLevelText(relatedIncident.level) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="位置">
                <div class="location-info">
                  <el-icon><Location /></el-icon>
                  <span>{{ relatedIncident.location }}</span>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="发生时间">
                <div class="time-info-detail">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatTime(relatedIncident.createTime) }}</span>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="状态" :span="2">
                <el-tag :type="getStatusType(relatedIncident.status)" size="large">
                  {{ getStatusText(relatedIncident.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
            
            <!-- 预警事件照片 -->
            <div v-if="relatedIncident.pictureUrl" class="photo-section">
              <el-divider content-position="left">
                <el-icon><Picture /></el-icon>
                <span>事件照片</span>
              </el-divider>
              <div class="photo-container">
                <el-image
                  class="photo-image"
                  :src="convertImageUrl(relatedIncident.pictureUrl)"
                  :preview-src-list="[convertImageUrl(relatedIncident.pictureUrl)]"
                  fit="cover"
                  :preview-teleported="true"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>图片加载失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="photo-actions">
                  <el-button type="primary" size="small" @click="previewImage">
                    <el-icon><ZoomIn /></el-icon>
                    查看大图
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-data-section">
            <el-empty description="未找到关联的预警事件信息" :image-size="120">
              <el-button type="primary" @click="handleView(currentDispose)">重新加载</el-button>
            </el-empty>
          </div>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { convertImageUrl } from '@/utils/imageUrl'
import { getDisposeList } from '@/api/dispose'
import { getIncidentDetail } from '@/api/incident'

// 数据
const loading = ref(false)
const disposeList = ref([])
const total = ref(0)
const relatedIncident = ref(null)
const relatedIncidentLoading = ref(false)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 对话框状态
const viewDialogVisible = ref(false)

// 当前选中的处理事件
const currentDispose = ref({})

// 获取处理事件列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getDisposeList(queryParams)
    disposeList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取处理事件列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 刷新数据
const handleRefresh = () => {
  queryParams.pageNum = 1
  getList()
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

// 查看详情
const handleView = async (row) => {
  currentDispose.value = row
  relatedIncident.value = null
  relatedIncidentLoading.value = true
  
  try {
    const res = await getIncidentDetail(row.incidentId)
    
    if (res.data) {
      const incident = res.data
      if (incident.pictureUrl) {
        incident.pictureUrl = convertImageUrl(incident.pictureUrl)
      }
      relatedIncident.value = incident
    }
  } catch (error) {
    console.error('获取关联预警事件失败:', error)
  } finally {
    relatedIncidentLoading.value = false
  }
  
  viewDialogVisible.value = true
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 获取预警等级类型
const getLevelType = (level) => {
  const levelMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return levelMap[level] || 'info'
}

// 获取预警等级文本
const getLevelText = (level) => {
  const levelMap = {
    1: '低风险',
    2: '中风险',
    3: '高风险'
  }
  return levelMap[level] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'danger',
    1: 'success'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '未处理',
    1: '已处理'
  }
  return statusMap[status] || '未知'
}

// 预览图片
const previewImage = () => {
  if (relatedIncident.value && relatedIncident.value.pictureUrl) {
    window.open(convertImageUrl(relatedIncident.value.pictureUrl), '_blank')
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.dispose-container {
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
  max-width: 300px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  color: var(--color-text-regular);
}

.time-info .el-icon {
  color: var(--color-text-secondary);
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
  flex-shrink: 0;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.detail-card {
  margin-bottom: 20px;
}

.detail-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.card-header .el-icon {
  color: var(--color-primary);
}

.user-info-detail {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-info-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-regular);
}

.time-info-detail .el-icon {
  color: var(--color-text-secondary);
}

.location-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-regular);
}

.location-info .el-icon {
  color: var(--color-primary);
}

.loading-section {
  padding: 20px;
}

.incident-detail {
  padding: 10px 0;
}

.photo-section {
  margin-top: 20px;
}

.photo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: var(--color-bg-hover);
  border-radius: 8px;
}

.photo-image {
  width: 100%;
  max-width: 400px;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.photo-actions {
  display: flex;
  gap: 10px;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--color-text-secondary);
  gap: 10px;
}

.image-error .el-icon {
  font-size: 40px;
}

.no-data-section {
  padding: 40px 20px;
}
</style>