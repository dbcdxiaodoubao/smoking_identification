<template>
  <div class="student-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-title">
        <el-icon><User /></el-icon>
        <span>学生管理</span>
      </div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增学生
      </el-button>
    </div>

    <!-- 查询栏 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="班级">
          <el-input
            v-model="queryParams.banji"
            placeholder="请输入班级"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="studentList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: 'var(--color-bg-hover)', color: 'var(--color-text-regular)', fontWeight: 'bold' }"
      >
        <template #empty>
          <el-empty description="暂无学生数据" :image-size="80" />
        </template>
        <el-table-column prop="studentId" label="学生ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small">#{{ row.studentId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="120" align="center">
          <template #default="{ row }">
            <div class="student-name-info">
              <el-icon><User /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="banji" label="班级" min-width="150" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.banji }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sex" label="性别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.sex === 1 ? 'primary' : 'danger'" size="large">
              {{ row.sex === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="incidentCount" label="总事件数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.incidentCount > 0 ? 'warning' : 'info'" size="small">
              {{ row.incidentCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pictureUrl" label="人脸图片" width="120" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.pictureUrl"
              :src="getImageUrl(row.pictureUrl)"
              :preview-src-list="[getImageUrl(row.pictureUrl)]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <el-button
              v-else
              type="primary"
              size="small"
              @click="handleUploadFace(row)"
            >
              上传
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
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

    <!-- 新增学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增学生"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="studentFormRef"
        :model="studentForm"
        :rules="studentRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="班级" prop="banji">
          <el-input v-model="studentForm.banji" placeholder="请输入班级" />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="studentForm.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
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

    <!-- 上传人脸对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传人脸"
      width="600px"
      @close="handleUploadDialogClose"
    >
      <el-form label-width="100px">
        <el-form-item label="学生姓名">
          <span>{{ currentStudent.name }}</span>
        </el-form-item>
        <el-form-item label="班级">
          <span>{{ currentStudent.banji }}</span>
        </el-form-item>
        <el-form-item label="上传方式">
          <el-radio-group v-model="uploadMode" @change="handleUploadModeChange">
            <el-radio label="file">选择文件</el-radio>
            <el-radio label="camera">摄像头拍照</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 文件上传方式 -->
        <el-form-item v-if="uploadMode === 'file'" label="人脸图片">
          <el-upload
            ref="uploadRef"
            class="upload-demo"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            accept="image/*"
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将图片拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传学生正面人脸照片，支持 jpg、png 格式
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <!-- 摄像头拍照方式 -->
        <el-form-item v-if="uploadMode === 'camera'" label="摄像头">
          <div class="camera-container">
            <video
              ref="videoRef"
              class="camera-preview"
              autoplay
              playsinline
              muted
            ></video>
            <canvas ref="canvasRef" class="camera-canvas" style="display: none;"></canvas>
            <div class="camera-buttons">
              <el-button type="primary" @click="startCamera" :disabled="cameraStarted">
                <el-icon><VideoCamera /></el-icon>
                开启摄像头
              </el-button>
              <el-button type="success" @click="capturePhoto" :disabled="!cameraStarted || photoCaptured">
                <el-icon><Camera /></el-icon>
                拍照
              </el-button>
              <el-button type="warning" @click="retakePhoto" :disabled="!photoCaptured">
                <el-icon><Refresh /></el-icon>
                重拍
              </el-button>
            </div>
            <div v-if="photoCaptured" class="photo-preview">
              <img :src="photoDataUrl" alt="拍摄的照片" />
              <p>照片已拍摄，点击"上传"按钮提交</p>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUploadSubmit" :loading="uploadLoading">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Plus,
  Search,
  Refresh,
  Picture,
  Upload,
  Delete,
  UploadFilled,
  VideoCamera,
  Camera
} from '@element-plus/icons-vue'
import { getStudentList, addStudent, uploadFace, deleteStudent } from '@/api/student'
import { getImageUrl } from '@/utils/imageUrl'

// 数据
const loading = ref(false)
const submitLoading = ref(false)
const uploadLoading = ref(false)
const studentList = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  banji: ''
})

// 对话框状态
const dialogVisible = ref(false)
const uploadDialogVisible = ref(false)

// 表单引用
const studentFormRef = ref()
const uploadRef = ref()
const videoRef = ref()
const canvasRef = ref()

// 当前选中的学生
const currentStudent = ref({})

// 上传的文件
const uploadFile = ref(null)

// 上传模式：file 或 camera
const uploadMode = ref('file')

// 摄像头相关
const cameraStarted = ref(false)
const photoCaptured = ref(false)
const photoDataUrl = ref('')
const stream = ref(null)

// 表单数据
const studentForm = reactive({
  name: '',
  banji: '',
  sex: 1
})

// 表单验证规则
const studentRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  banji: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

// 获取学生列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getStudentList(queryParams)
    studentList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取学生列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.banji = ''
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

// 新增学生
const handleAdd = () => {
  dialogVisible.value = true
  resetForm()
}

// 上传人脸
const handleUploadFace = (row) => {
  currentStudent.value = row
  uploadDialogVisible.value = true
  uploadFile.value = null
  uploadMode.value = 'file'
  photoCaptured.value = false
  photoDataUrl.value = ''
  cameraStarted.value = false
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 上传模式切换
const handleUploadModeChange = (mode) => {
  if (mode === 'file') {
    stopCamera()
  }
}

// 开启摄像头
const startCamera = async () => {
  try {
    const mediaStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'user', width: 640, height: 480 }
    })
    stream.value = mediaStream
    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream
      cameraStarted.value = true
      photoCaptured.value = false
      photoDataUrl.value = ''
    }
  } catch (error) {
    console.error('开启摄像头失败:', error)
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

// 停止摄像头
const stopCamera = () => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop())
    stream.value = null
  }
  cameraStarted.value = false
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
}

// 拍照
const capturePhoto = () => {
  if (!videoRef.value || !canvasRef.value) return
  
  const video = videoRef.value
  const canvas = canvasRef.value
  const context = canvas.getContext('2d')
  
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  
  context.drawImage(video, 0, 0, canvas.width, canvas.height)
  
  photoDataUrl.value = canvas.toDataURL('image/jpeg', 0.9)
  photoCaptured.value = true
  
  ElMessage.success('照片已拍摄')
}

// 重拍
const retakePhoto = () => {
  photoCaptured.value = false
  photoDataUrl.value = ''
}

// 文件变化
const handleFileChange = (file) => {
  uploadFile.value = file.raw
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传1张图片')
}

// 提交上传
const handleUploadSubmit = async () => {
  let file = null
  
  if (uploadMode.value === 'file') {
    if (!uploadFile.value) {
      ElMessage.warning('请选择要上传的图片')
      return
    }
    file = uploadFile.value
  } else {
    if (!photoCaptured.value) {
      ElMessage.warning('请先拍照')
      return
    }
    
    const base64Data = photoDataUrl.value.split(',')[1]
    const byteCharacters = atob(base64Data)
    const byteArrays = []
    
    for (let offset = 0; offset < byteCharacters.length; offset += 512) {
      const slice = byteCharacters.slice(offset, offset + 512)
      const byteNumbers = new Array(slice.length)
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i)
      }
      const byteArray = new Uint8Array(byteNumbers)
      byteArrays.push(byteArray)
    }
    
    file = new File(byteArrays, 'photo.jpg', { type: 'image/jpeg' })
  }

  uploadLoading.value = true
  try {
    await uploadFace(currentStudent.value.studentId, file)
    ElMessage({
      type: 'success',
      message: '上传成功'
    })
    uploadDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('上传失败:', error)
  } finally {
    uploadLoading.value = false
  }
}

// 删除学生
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除学生"${row.name}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteStudent(row.studentId)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
      getList()
    } catch (error) {
      console.error('删除学生失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!studentFormRef.value) return

  await studentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await addStudent(studentForm)
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

// 上传对话框关闭事件
const handleUploadDialogClose = () => {
  uploadFile.value = null
  photoDataUrl.value = ''
  photoCaptured.value = false
  stopCamera()
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 重置表单
const resetForm = () => {
  if (studentFormRef.value) {
    studentFormRef.value.resetFields()
  }
  studentForm.name = ''
  studentForm.banji = ''
  studentForm.sex = 1
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.student-container {
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

.search-card {
  margin-bottom: 20px;
  flex-shrink: 0;
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

.student-name-info {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  background: var(--color-bg-hover);
  color: var(--color-text-secondary);
  font-size: 24px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
  flex-shrink: 0;
}

.upload-demo {
  width: 100%;
}

.el-icon--upload {
  font-size: 67px;
  color: var(--color-primary);
}

.camera-container {
  width: 100%;
}

.camera-preview {
  width: 100%;
  max-width: 480px;
  height: auto;
  border-radius: 8px;
  border: 2px solid var(--color-primary);
  background: #000;
}

.camera-canvas {
  display: none;
}

.camera-buttons {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.photo-preview {
  margin-top: 15px;
  text-align: center;
}

.photo-preview img {
  max-width: 100%;
  max-height: 360px;
  border-radius: 8px;
  border: 2px solid #67C23A;
}

.photo-preview p {
  margin-top: 10px;
  color: #67C23A;
  font-weight: 500;
}
</style>
