<template>
  <div class="incident-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-title">
        <el-icon><Warning /></el-icon>
        <span>预警事件管理</span>
      </div>
      <div class="operation-actions">
        <el-select v-model="queryParams.level" placeholder="预警等级筛选" clearable @change="handleSearch" class="filter-select">
          <el-option label="低风险" :value="1" />
          <el-option label="中风险" :value="2" />
          <el-option label="高风险" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增预警事件
        </el-button>
      </div>
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
          <div class="stat-label">总事件数</div>
        </div>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="incidentList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: 'var(--color-bg-hover)', color: 'var(--color-text-regular)', fontWeight: 'bold' }"
        :row-class-name="getRowClassName"
      >
        <template #empty>
          <el-empty description="暂无预警事件" :image-size="80" />
        </template>
        <el-table-column prop="incidentId" label="事件ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small">#{{ row.incidentId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cameraId" label="摄像头ID" width="120" align="center">
          <template #default="{ row }">
            <div class="camera-info">
              <el-icon><VideoCamera /></el-icon>
              <span>{{ row.cameraId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="150" align="center">
          <template #default="{ row }">
            <div class="location-info">
              <el-icon><Location /></el-icon>
              <span>{{ row.location }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="预警等级" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="large">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="学生名称" width="120" align="center">
          <template #default="{ row }">
            <span 
              v-if="row.name" 
              class="student-name-link"
              @click="handleViewStudentDetail(row)"
            >
              {{ row.name }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发生时间" min-width="180" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="pictureUrl" label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.pictureUrl"
              class="table-image"
              :src="convertImageUrl(row.pictureUrl)"
              :preview-src-list="[convertImageUrl(row.pictureUrl)]"
              fit="cover"
              :preview-teleported="true"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <el-tag v-else type="info" size="small">无图片</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button type="primary" size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button
                type="success"
                size="small"
                :disabled="row.status !== 0"
                @click="row.status === 0 && handleProcess(row)"
              >
                <el-icon><Check /></el-icon>
                处理
              </el-button>
              <el-button
                type="warning"
                size="small"
                :disabled="!!row.name"
                @click="!row.name && handleOpenBind(row)"
              >
                <el-icon><Link /></el-icon>
                绑定
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
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
        ref="incidentFormRef"
        :model="incidentForm"
        :rules="incidentRules"
        label-width="100px"
      >
        <el-form-item label="摄像头ID" prop="cameraId">
          <el-input-number v-model="incidentForm.cameraId" :min="1" placeholder="请输入摄像头ID" />
        </el-form-item>
        
        <el-form-item label="位置" prop="location">
          <el-input v-model="incidentForm.location" placeholder="请输入位置" />
        </el-form-item>
        
        <el-form-item label="预警等级" prop="level">
          <el-select v-model="incidentForm.level" placeholder="请选择预警等级">
            <el-option label="低风险" :value="1" />
            <el-option label="中风险" :value="2" />
            <el-option label="高风险" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="incidentForm.status" placeholder="请选择状态">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="图片URL" prop="pictureUrl">
          <el-input v-model="incidentForm.pictureUrl" placeholder="请输入图片URL" />
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
    <el-dialog v-model="viewDialogVisible" title="预警事件详情" width="800px" :close-on-click-modal="false">
      <div class="detail-content">
        <!-- 预警事件基本信息 -->
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <div class="card-header">
              <el-icon><Warning /></el-icon>
              <span>预警信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="事件ID">
              <el-tag type="primary" size="large">#{{ currentIncident.incidentId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="摄像头ID">
              <div class="camera-info-detail">
                <el-icon><VideoCamera /></el-icon>
                <span>{{ currentIncident.cameraId }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="位置">
              <div class="location-info-detail">
                <el-icon><Location /></el-icon>
                <span>{{ currentIncident.location }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="预警等级">
              <el-tag :type="getLevelType(currentIncident.level)" size="large">
                {{ getLevelText(currentIncident.level) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentIncident.status)" size="large">
                {{ getStatusText(currentIncident.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="发生时间">
              <div class="time-info-detail">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(currentIncident.createTime) }}</span>
              </div>
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 事件照片 -->
          <div v-if="currentIncident.pictureUrl" class="photo-section">
            <el-divider content-position="left">
              <el-icon><Picture /></el-icon>
              <span>事件照片</span>
            </el-divider>
            <div class="photo-container">
              <el-image
                class="photo-image"
                :src="convertImageUrl(currentIncident.pictureUrl)"
                :preview-src-list="[convertImageUrl(currentIncident.pictureUrl)]"
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
        </el-card>
        
        <!-- 处理记录信息 -->
        <el-card v-if="currentIncident.status === 1 && currentIncident.handleRecord" shadow="hover" class="detail-card">
          <template #header>
            <div class="card-header">
              <el-icon><DocumentChecked /></el-icon>
              <span>处理记录</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="处理ID">
              <el-tag type="success" size="large">#{{ currentIncident.handleRecord.handleId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预警事件ID">
              <el-tag type="warning" size="large">{{ currentIncident.handleRecord.incidentId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理人ID">
              <div class="user-info-detail">
                <el-avatar :size="36" icon="User" />
                <span>{{ currentIncident.handleRecord.userId }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="处理时间">
              <div class="time-info-detail">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(currentIncident.handleRecord.handleTime) }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="处理备注" :span="2">
              {{ currentIncident.handleRecord.remark || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </el-dialog>
    
    <!-- 处理事件对话框 -->
    <el-dialog v-model="processDialogVisible" title="处理预警事件" width="500px">
      <el-form
        ref="processFormRef"
        :model="processForm"
        :rules="processRules"
        label-width="100px"
      >
        <el-form-item label="处理人ID" prop="userId">
          <el-input-number v-model="processForm.userId" :min="1" placeholder="请输入处理人ID" />
        </el-form-item>
        
        <el-form-item label="处理备注" prop="remark">
          <el-input
            v-model="processForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleProcessSubmit" :loading="processLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 学生详细信息对话框 -->
    <el-dialog v-model="studentDetailDialogVisible" title="学生详细信息" width="500px" :close-on-click-modal="false">
      <div v-loading="studentDetailLoading" class="student-detail-content">
        <div class="student-header">
          <div class="student-photo">
            <el-image
              v-if="currentStudent.photo"
              :src="convertImageUrl(currentStudent.photo)"
              fit="cover"
              class="photo-image"
            >
              <template #error>
                <div class="photo-placeholder">
                  <el-icon><User /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="photo-placeholder">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <div class="student-name">
            <h3>{{ currentStudent.name }}</h3>
          </div>
        </div>
        
        <el-descriptions :column="1" border class="student-info">
          <el-descriptions-item label="学生ID">
            <el-tag type="info" size="large">#{{ currentStudent.studentId }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            <el-tag :type="currentStudent.gender === '男' ? 'primary' : 'danger'" size="large">
              {{ currentStudent.gender || '-' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="班级">
            <el-tag type="success" size="large">{{ currentStudent.banji || '-' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="studentDetailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 绑定学生对话框 -->
    <el-dialog v-model="bindDialogVisible" title="绑定学生" width="700px">
      <el-input
        v-model="studentSearch"
        placeholder="搜索学生姓名"
        clearable
        style="margin-bottom:12px;width:240px"
        @input="loadStudentOptions"
      />
      <el-table
        v-loading="studentOptionsLoading"
        :data="studentOptions"
        border
        highlight-current-row
        style="width:100%"
        max-height="400"
        @row-click="handleSelectStudent"
      >
        <el-table-column prop="studentId" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="banji" label="班级" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row: s }">
            <el-button type="primary" size="small" @click.stop="handleBindStudent(s)">
              选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { convertImageUrl } from '@/utils/imageUrl'
import {
  getIncidentList,
  addIncident,
  deleteIncident,
  handleIncident,
  bindStudent
} from '@/api/incident'
import { getStudentDetail, getStudentList } from '@/api/student'
import {
  Warning,
  Plus,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Document,
  VideoCamera,
  Location,
  Clock,
  Picture,
  View,
  Check,
  Delete,
  ZoomIn,
  DocumentChecked,
  Link
} from '@element-plus/icons-vue'

// 数据
const loading = ref(false)
const submitLoading = ref(false)
const processLoading = ref(false)
const incidentList = ref([])
const total = ref(0)
const processingIncidentId = ref(null)
const stats = reactive({
  low: 0,
  medium: 0,
  high: 0
}) // 正在处理的事件ID

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  level: null,
  status: 0
})

// 对话框状态
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const processDialogVisible = ref(false)
const studentDetailDialogVisible = ref(false)
const dialogTitle = ref('新增预警事件')

// 表单引用
const incidentFormRef = ref()
const processFormRef = ref()

// 当前选中的事件
const currentIncident = ref({})

// 当前选中的学生
const currentStudent = ref({})

// 学生详情加载状态
const studentDetailLoading = ref(false)

// 绑定学生相关
const bindDialogVisible = ref(false)
const bindingIncident = ref(null)
const studentSearch = ref('')
const studentOptions = ref([])
const studentOptionsLoading = ref(false)

// 表单数据
const incidentForm = reactive({
  incidentId: null,
  cameraId: null,
  location: '',
  level: null,
  status: 0,
  pictureUrl: ''
})

// 处理表单数据
const processForm = reactive({
  incidentId: null,
  userId: null,
  remark: ''
})

// 表单验证规则
const incidentRules = {
  cameraId: [
    { required: true, message: '请输入摄像头ID', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择预警等级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const processRules = {
  userId: [
    { required: true, message: '请输入处理人ID', trigger: 'blur' }
  ]
}

// 获取预警事件列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getIncidentList(queryParams)
    // 转换事件列表中的图片URL
    const incidents = res.rows || []
    incidentList.value = incidents.map(incident => {
      if (incident.pictureUrl) {
        incident.pictureUrl = convertImageUrl(incident.pictureUrl)
      }
      return incident
    })
    total.value = res.total || 0
    
    // 计算统计数据
    stats.low = incidents.filter(item => item.level === 1).length
    stats.medium = incidents.filter(item => item.level === 2).length
    stats.high = incidents.filter(item => item.level === 3).length
  } catch (error) {
    console.error('获取预警事件列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
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

// 新增预警事件
const handleAdd = () => {
  dialogTitle.value = '新增预警事件'
  dialogVisible.value = true
  resetForm()
}

// 查看详情
const handleView = (row) => {
  // 创建新对象，避免直接修改原数据
  const incident = { ...row }
  if (incident.pictureUrl) {
    incident.pictureUrl = convertImageUrl(incident.pictureUrl)
  }
  currentIncident.value = incident
  viewDialogVisible.value = true
}

// 查看学生详细信息
const handleViewStudentDetail = async (row) => {
  if (!row.studentId || !row.name) {
    ElMessage.warning('该记录没有关联学生信息')
    return
  }
  
  studentDetailLoading.value = true
  studentDetailDialogVisible.value = true
  
  try {
    const res = await getStudentDetail(row.studentId)
    const studentData = res.data || res
    currentStudent.value = {
      studentId: studentData.studentId,
      name: studentData.name,
      gender: studentData.sex === 1 ? '男' : (studentData.sex === 2 ? '女' : '-'),
      banji: studentData.banji,
      photo: convertImageUrl(studentData.pictureUrl)
    }
  } catch (error) {
    console.error('获取学生详情失败:', error)
    ElMessage.error('获取学生详情失败')
    currentStudent.value = {
      studentId: row.studentId,
      name: row.name,
      gender: '-',
      banji: '-',
      photo: ''
    }
  } finally {
    studentDetailLoading.value = false
  }
}

// 打开绑定学生对话框
const handleOpenBind = (row) => {
  bindingIncident.value = row
  studentSearch.value = ''
  bindDialogVisible.value = true
  loadStudentOptions()
}

// 搜索学生
const loadStudentOptions = async () => {
  studentOptionsLoading.value = true
  try {
    const res = await getStudentList({ pageNum: 1, pageSize: 50, banji: studentSearch.value || '' })
    studentOptions.value = res.rows || []
  } catch (e) {
    studentOptions.value = []
  } finally {
    studentOptionsLoading.value = false
  }
}

// 点击行选中
const handleSelectStudent = (row) => {
  // 由表格内按钮处理
}

// 执行绑定
const handleBindStudent = async (student) => {
  if (!bindingIncident.value) return
  try {
    await bindStudent(bindingIncident.value.incidentId, student.studentId)
    ElMessage.success(`已将事件 #${bindingIncident.value.incidentId} 绑定到学生 ${student.name}`)
    bindDialogVisible.value = false
    getList()
  } catch (e) {
    // 错误已在拦截器处理
  }
}

// 处理事件
const handleProcess = (row) => {
  processForm.incidentId = row.incidentId
  processForm.userId = null
  processForm.remark = ''
  processDialogVisible.value = true
}

// 提交处理
const handleProcessSubmit = async () => {
  if (!processFormRef.value) return
  
  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      processLoading.value = true
      processingIncidentId.value = processForm.incidentId
      
      try {
        await handleIncident(processForm.incidentId)
        
        ElMessage({
          type: 'success',
          message: '处理成功'
        })
        
        processDialogVisible.value = false
        
        // 显示划掉动画效果，然后从列表中移除
        setTimeout(() => {
          const index = incidentList.value.findIndex(item => item.incidentId === processForm.incidentId)
          if (index !== -1) {
            incidentList.value.splice(index, 1)
            total.value--
          }
          processingIncidentId.value = null
        }, 1000)
      } catch (error) {
        console.error('处理失败:', error)
        processingIncidentId.value = null
      } finally {
        processLoading.value = false
      }
    }
  })
}

// 删除预警事件
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除预警事件"${row.incidentId}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteIncident(row.incidentId)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
      getList()
    } catch (error) {
      console.error('删除预警事件失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!incidentFormRef.value) return
  
  await incidentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await addIncident(incidentForm)
        ElMessage({
          type: 'success',
          message: '添加成功'
        })
        
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
  if (incidentFormRef.value) {
    incidentFormRef.value.resetFields()
  }
  
  Object.keys(incidentForm).forEach(key => {
    if (key === 'status') {
      incidentForm[key] = 0
    } else {
      incidentForm[key] = ''
    }
  })
  incidentForm.incidentId = null
  incidentForm.cameraId = null
  incidentForm.level = null
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
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

// 获取行类名
const getRowClassName = ({ row }) => {
  if (row.status === 1) {
    return 'processed-row'
  }
  return ''
}

// 预览图片
const previewImage = () => {
  if (currentIncident.value && currentIncident.value.pictureUrl) {
    window.open(convertImageUrl(currentIncident.value.pictureUrl), '_blank')
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.incident-container {
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
  color: #E6A23C;
}

.operation-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-select {
  width: 180px;
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

.el-table :deep(.processed-row) {
  background-color: #f6ffed !important;
}

.el-table :deep(.processed-row:hover) {
  background-color: #e6f7d9 !important;
}

.camera-info {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  color: var(--color-text-regular);
}

.camera-info .el-icon {
  color: var(--color-primary);
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

.action-btns {
  display: flex;
  gap: 6px;
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

.table-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.2s;
}

.table-image:hover {
  transform: scale(1.1);
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: var(--color-bg-hover);
  border-radius: 6px;
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
  color: #E6A23C;
}

.camera-info-detail {
  display: flex;
  align-items: center;
  gap: 10px;
}

.camera-info-detail .el-icon {
  color: var(--color-primary);
}

.location-info-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-regular);
}

.location-info-detail .el-icon {
  color: var(--color-primary);
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

.user-info-detail {
  display: flex;
  align-items: center;
  gap: 10px;
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

.mt-10 {
  margin-top: 10px;
}

.handle-record-section {
  margin-top: 20px;
}

.student-name-link {
  color: var(--color-primary);
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s;
  font-weight: 500;
}

.student-name-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.student-detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.student-info {
  margin-bottom: 20px;
}

.student-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  margin-bottom: 20px;
}

.student-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--color-primary);
  margin-bottom: 15px;
  background: var(--color-bg-hover);
}

.student-photo .photo-image {
  width: 100%;
  height: 100%;
}

.photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-hover);
  color: var(--color-text-secondary);
}

.photo-placeholder .el-icon {
  font-size: 48px;
}

.student-name h3 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 20px;
  font-weight: 600;
}
</style>