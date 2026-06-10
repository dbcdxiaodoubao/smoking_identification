<template>
  <div class="monitor-container">
    <div class="page-header">
      <h1>实时监控</h1>
      <p>查看摄像头实时画面</p>
    </div>

    <div class="monitor-content">
      <!-- 大监控显示屏 -->
      <div class="monitor-display">
        <div class="camera-grid" :class="getGridClass()">
          <div 
            v-for="(displayCamera, index) in displayCameras" 
            :key="displayCamera.id"
            class="camera-grid-item"
            :class="{ 'dragging': draggingIndex === index, 'drag-over': dragOverIndex === index }"
            draggable="true"
            @click="handleCameraClick(displayCamera)"
            @contextmenu.prevent="handleRightClick($event, displayCamera, index)"
            @dragstart="handleDragStart($event, index)"
            @dragover.prevent="handleDragOver($event, index)"
            @dragleave="handleDragLeave"
            @drop="handleDrop($event, index)"
            @dragend="handleDragEnd"
          >
            <div class="camera-item-wrapper">
              <!-- 网络摄像头 -->
              <RtspPlayer
                v-if="displayCamera.type === 'network'"
                :hlsUrl="convertRtspToHls(displayCamera.rtspUrl)"
                :rtspUrl="displayCamera.rtspUrl"
                :cameraName="displayCamera.cameraName"
                :cameraLocation="displayCamera.location"
              />
              
              <!-- 本地摄像头 -->
              <div v-if="displayCamera.type === 'local'" class="local-camera-display">
                <video 
                  :ref="el => setLocalVideoRef(el, index)"
                  autoplay 
                  muted 
                  playsinline
                  class="local-video"
                ></video>
              </div>
              
              <div class="camera-item-info">
                <span class="camera-name">{{ displayCamera.cameraName }}</span>
                <span class="camera-location">{{ displayCamera.location }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 摄像头列表 -->
      <div class="camera-list">
        <h3>摄像头列表</h3>
        <el-table :data="allCameras" style="width: 100%" v-loading="loading">
          <el-table-column prop="cameraName" label="名称" />
          <el-table-column prop="location" label="位置" />
          <el-table-column prop="level" label="预警等级">
            <template #default="scope">
              <el-tag :type="getLevelType(scope.row.level)">
                {{ getLevelText(scope.row.level) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态">
            <template #default="scope">
              <el-tag :type="isCameraDisplayed(scope.row) ? 'success' : 'info'">
                {{ isCameraDisplayed(scope.row) ? '显示中' : '未显示' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <!-- 右键菜单 -->
    <div 
      v-if="contextMenu.visible" 
      class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      @click.self="contextMenu.visible = false"
    >
      <div class="context-menu-title">切换摄像头</div>
      <div 
        v-for="camera in availableCameras" 
        :key="camera.id"
        class="context-menu-item"
        @click.stop="switchCamera(camera)"
      >
        <span class="camera-name">{{ camera.cameraName }}</span>
        <span class="camera-location">{{ camera.location }}</span>
      </div>
    </div>
    
    <!-- 放大查看对话框 -->
    <el-dialog
      v-model="enlargeDialogVisible"
      title="摄像头画面"
      width="60%"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      @close="closeEnlargeDialog"
      class="enlarge-dialog"
    >
      <div class="enlarge-display">
        <!-- 网络摄像头 -->
        <RtspPlayer
          v-if="enlargedCamera && enlargedCamera.type === 'network'"
          :hlsUrl="convertRtspToHls(enlargedCamera.rtspUrl)"
          :rtspUrl="enlargedCamera.rtspUrl"
          :cameraName="enlargedCamera.cameraName"
          :cameraLocation="enlargedCamera.location"
        />
        
        <!-- 本地摄像头 -->
        <div v-if="enlargedCamera && enlargedCamera.type === 'local'" class="local-camera-enlarge">
          <video 
            ref="enlargedLocalVideo"
            autoplay 
            muted 
            playsinline
            class="local-video-enlarge"
          ></video>
          <canvas 
            ref="enlargedOverlayCanvas"
            class="enlarged-overlay-canvas"
          ></canvas>
          <canvas 
            ref="enlargedCaptureCanvas"
            class="enlarged-capture-canvas"
            style="display: none;"
          ></canvas>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getCameraList, getCameraDetail } from '@/api/camera'
import { detectImage } from '@/api/detection'
import RtspPlayer from '@/components/RtspPlayer.vue'

// 响应式数据
const connecting = ref(false)
const connected = ref(false)
const loading = ref(false)
const currentCameraName = ref('')
const currentCameraLocation = ref('')
const showPlayer = ref(false)
const hlsUrl = ref('')
const rtspPlayer = ref(null)

// 视频源类型
const videoSource = ref('network')

// 多摄像头模式
const multiCameraMode = ref(true)

// 视频元素引用
const localVideo = ref(null)
const overlayCanvas = ref(null)
const captureCanvas = ref(null)
const localCameras = ref([])
const selectedLocalCamera = ref('')
const selectedLocalCameraName = ref('')
const currentStream = ref(null)
const detectionActive = ref(false)
const detectionResults = ref([])
const detectionInterval = ref(null)
const lastDetectionTime = ref(0) // 记录最后一次收到检测响应的时间
const detectionTimeout = ref(null) // 检测超时定时器
// 视频播放状态
const videoPlaying = ref(false)

// 监控表单
const monitorForm = ref({
  cameraId: '',
  rtspUrl: ''
})

// 摄像头列表
const cameraList = ref([])

// 新增：大监控显示屏相关数据
const allCameras = ref([])
const displayCameras = ref([])
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  targetIndex: -1
})
const enlargeDialogVisible = ref(false)
const enlargedCamera = ref(null)
const enlargedLocalVideo = ref(null)
const enlargedOverlayCanvas = ref(null)
const enlargedCaptureCanvas = ref(null)
const localVideoRefs = ref([])

// 放大对话框检测相关状态
const enlargedDetectionActive = ref(false)
const enlargedDetectionResults = ref([])
const enlargedDetectionInterval = ref(null)
const enlargedLastDetectionTime = ref(0)
const enlargedDetectionTimeout = ref(null)

// 拖拽相关状态
const draggingIndex = ref(-1)
const dragOverIndex = ref(-1)

// RTSP转换提示状态
const rtspConvertWarningShown = ref(false)

// 连接摄像头
const connectCamera = () => {
  if (videoSource.value === 'network') {
    connectNetworkCamera()
  } else if (videoSource.value === 'local') {
    connectLocalCamera()
  }
}

// 连接网络摄像头
const connectNetworkCamera = () => {
  console.log('connectCamera被调用')
  console.log('monitorForm.rtspUrl:', monitorForm.value.rtspUrl)
  
  if (!monitorForm.value.rtspUrl) {
    ElMessage.error('请先选择摄像头')
    return
  }

  connecting.value = true
  
  try {
    // 将RTSP地址转换为HLS地址
    const rtspUrl = monitorForm.value.rtspUrl
    const convertedHlsUrl = convertRtspToHls(rtspUrl)
    
    console.log('原始RTSP地址:', rtspUrl)
    console.log('转换后的HLS地址:', convertedHlsUrl)
    hlsUrl.value = convertedHlsUrl
    
    // 显示播放器
    showPlayer.value = true
    connected.value = true
    
    ElMessage.success('摄像头连接成功，使用HLS流播放')
    
    // 提示用户需要转码服务
    ElMessage({
      message: '注意：需要配置RTSP转HLS服务才能播放视频',
      type: 'info',
      duration: 5000,
      showClose: true
    })
  } catch (error) {
    console.error('连接摄像头失败:', error)
    ElMessage.error('连接摄像头失败: ' + error.message)
  } finally {
    connecting.value = false
  }
}

// 连接本地摄像头
const connectLocalCamera = async () => {
  connecting.value = true
  
  try {
    // 停止当前流
    if (currentStream.value) {
      currentStream.value.getTracks().forEach(track => track.stop())
    }
    
    // 如果还没有选择摄像头，自动选择前置摄像头
    if (!selectedLocalCamera.value && localCameras.value.length > 0) {
      const frontCamera = localCameras.value.find(camera => camera.isFrontCamera)
      if (frontCamera) {
        selectedLocalCamera.value = frontCamera.deviceId
        selectedLocalCameraName.value = frontCamera.label
      } else {
        selectedLocalCamera.value = localCameras.value[0].deviceId
        selectedLocalCameraName.value = localCameras.value[0].label
      }
    }
    
    // 使用选定的摄像头获取视频流 - 简化约束，参考HTML文件
    const constraints = {
      video: {
        width: { ideal: 640 },
        height: { ideal: 480 },
        deviceId: selectedLocalCamera.value ? { exact: selectedLocalCamera.value } : undefined,
        facingMode: selectedLocalCamera.value ? undefined : 'user' // 只有在没有指定deviceId时才使用facingMode
      },
      audio: false
    }
    
    console.log('请求摄像头约束:', constraints)
    currentStream.value = await navigator.mediaDevices.getUserMedia(constraints)
    console.log('获取到视频流:', currentStream.value)
    
    // 等待DOM更新
    await nextTick()
    
    // 设置视频源
    if (localVideo.value) {
      console.log('找到视频元素:', localVideo.value)
      console.log('视频元素尺寸:', localVideo.value.width, 'x', localVideo.value.height)
      console.log('视频元素样式:', localVideo.value.style.cssText)
      
      localVideo.value.srcObject = currentStream.value
      
      // 等待视频元数据加载
      localVideo.value.onloadedmetadata = () => {
        console.log('视频元数据已加载')
        console.log('视频尺寸:', localVideo.value.videoWidth, 'x', localVideo.value.videoHeight)
        console.log('视频元素可见性:', window.getComputedStyle(localVideo.value).visibility)
        console.log('视频元素显示状态:', window.getComputedStyle(localVideo.value).display)
        console.log('视频元素z-index:', window.getComputedStyle(localVideo.value).zIndex)
        console.log('视频元素位置:', window.getComputedStyle(localVideo.value).position)
        
        // 设置覆盖层canvas尺寸与视频显示尺寸一致
        if (overlayCanvas.value) {
          console.log('找到覆盖层画布元素:', overlayCanvas.value)
          overlayCanvas.value.width = 640  // 固定为显示尺寸
          overlayCanvas.value.height = 480 // 固定为显示尺寸
          console.log('设置覆盖层canvas尺寸:', overlayCanvas.value.width, 'x', overlayCanvas.value.height)
        } else {
          console.error('找不到覆盖层画布元素')
        }
        
        // 设置高清捕获canvas尺寸为实际视频尺寸
          if (captureCanvas.value) {
            console.log('找到捕获画布元素:', captureCanvas.value)
            captureCanvas.value.width = localVideo.value.videoWidth
            captureCanvas.value.height = localVideo.value.videoHeight
            console.log('设置捕获canvas尺寸:', captureCanvas.value.width, 'x', captureCanvas.value.height)
            
            // 初始化捕获canvas - 清除画布内容
            const ctx = captureCanvas.value.getContext('2d')
            ctx.clearRect(0, 0, captureCanvas.value.width, captureCanvas.value.height)
            console.log('捕获canvas已初始化并清除')
          } else {
            console.error('找不到捕获画布元素')
          }
        
        // 播放视频
        localVideo.value.play().then(() => {
          console.log('视频开始播放')
          console.log('视频当前时间:', localVideo.value.currentTime)
          console.log('视频就绪状态:', localVideo.value.readyState)
          
          // 设置视频播放状态
          videoPlaying.value = true
          
          // 立即测试绘制一帧到覆盖层canvas
          if (overlayCanvas.value) {
            const ctx = overlayCanvas.value.getContext('2d')
            ctx.drawImage(localVideo.value, 0, 0, overlayCanvas.value.width, overlayCanvas.value.height)
            console.log('已绘制测试帧到覆盖层canvas')
          }
          
          // 检查视频是否真的在播放
          setTimeout(() => {
            console.log('1秒后视频状态:')
            console.log('- 当前时间:', localVideo.value.currentTime)
            console.log('- 就绪状态:', localVideo.value.readyState)
            console.log('- 是否暂停:', localVideo.value.paused)
            console.log('- 是否结束:', localVideo.value.ended)
            
            // 如果视频没有播放，更新状态
            if (localVideo.value.paused || localVideo.value.currentTime === 0) {
              videoPlaying.value = false
              console.warn('视频未正常播放')
            }
            
            // 检查视频元素是否在视口中可见
            const rect = localVideo.value.getBoundingClientRect()
            const isVisible = rect.top >= 0 && rect.left >= 0 && 
                             rect.bottom <= window.innerHeight && 
                             rect.right <= window.innerWidth
            console.log('视频元素在视口中可见:', isVisible)
            console.log('视频元素位置和尺寸:', {
              top: rect.top,
              left: rect.left,
              bottom: rect.bottom,
              right: rect.right,
              width: rect.width,
              height: rect.height
            })
            
            // 检查视频元素是否被其他元素遮挡
            const elementsAtPoint = document.elementsFromPoint(
              rect.left + rect.width / 2, 
              rect.top + rect.height / 2
            )
            console.log('视频中心点上的元素:', elementsAtPoint)
          }, 1000)
          
          connected.value = true
          ElMessage.success(`本地摄像头连接成功: ${selectedLocalCameraName.value}`)
          
          // 原始摄像头显示部分不进行检测，只在放大后进行检测
        }).catch(error => {
          console.error('视频播放失败:', error)
          ElMessage.error('视频播放失败: ' + error.message)
        })
      }
      
      // 添加错误处理
      localVideo.value.onerror = (error) => {
        console.error('视频加载错误:', error)
        console.error('视频错误代码:', localVideo.value.error ? localVideo.value.error.code : '未知')
        console.error('视频错误消息:', localVideo.value.error ? localVideo.value.error.message : '未知')
        ElMessage.error('视频加载错误')
      }
      
      // 添加视频播放状态监听
      localVideo.value.onplay = () => {
        console.log('视频onplay事件触发')
      }
      
      localVideo.value.onpause = () => {
        console.log('视频onpause事件触发')
      }
      
      localVideo.value.onstalled = () => {
        console.log('视频onstalled事件触发')
      }
      
      localVideo.value.onwaiting = () => {
        console.log('视频onwaiting事件触发')
      }
    } else {
      console.error('找不到视频元素')
      ElMessage.error('找不到视频元素')
    }
  } catch (error) {
    console.error('连接本地摄像头失败:', error)
    ElMessage.error('连接本地摄像头失败: ' + error.message)
  } finally {
    connecting.value = false
  }
}

// 开始检测循环
const startDetectionLoop = () => {
  if (detectionInterval.value) {
    clearInterval(detectionInterval.value)
  }
  
  detectionActive.value = true
  
  // 每500ms发送一帧到后端
  detectionInterval.value = setInterval(() => {
    sendFrameToBackend()
  }, 500) // 改为500ms间隔
}

// 发送帧到后端
const sendFrameToBackend = () => {
  if (!localVideo.value || !captureCanvas.value || !connected.value) return
  
  try {
    const video = localVideo.value
    const canvas = captureCanvas.value
    const ctx = canvas.getContext('2d')
    
    // 确保canvas尺寸与视频一致
    if (canvas.width !== video.videoWidth || canvas.height !== video.videoHeight) {
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      console.log('调整canvas尺寸:', canvas.width, 'x', canvas.height)
    }
    
    // 直接使用高清捕获canvas绘制当前视频帧
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    
    // 获取图像数据（转换为Blob）
    canvas.toBlob(async (blob) => {
      if (!blob) {
        console.error('无法创建图像Blob')
        return
      }
      
      try {
        // 创建FormData对象，符合后端API要求
        const formData = new FormData()
        
        // 确保图像文件正确添加到FormData
        // 使用File构造函数确保文件名和类型正确
        const imageFile = new File([blob], 'capture.jpg', { type: 'image/jpeg' })
        formData.append('image', imageFile)
        
        const currentCamera = localCameras.value.find(c => c.deviceId === selectedLocalCamera.value)
        const isFrontCamera = currentCamera ? currentCamera.isFrontCamera : false
        
        const cameraInfo = {
          cameraId: isFrontCamera ? 0 : 1,
          cameraName: selectedLocalCameraName.value || "本地摄像头",
          level: 1,
          location: isFrontCamera ? "前置摄像头" : "本地"
        }
        formData.append('cameraSubmitQuery', JSON.stringify(cameraInfo))
        
        // 调试信息：打印FormData内容
        console.log('发送FormData内容:')
        console.log('- 图像文件:', imageFile.name, imageFile.size, imageFile.type)
        console.log('- 摄像头信息:', JSON.stringify(cameraInfo))
        
        // 使用修改后的detectImage API，直接传入FormData对象
        const data = await detectImage(formData)
        if (Array.isArray(data)) {
          detectionResults.value = data
          // 收到后端返回值时，立即绘制检测框
          drawDetectionBoxes(data)
        }
      } catch (error) {
        console.error('检测请求失败:', error)
        ElMessage.error('检测请求失败: ' + error.message)
      }
    }, 'image/jpeg', 0.9) // 使用0.9质量，提高图像清晰度
  } catch (error) {
    console.error('发送帧到后端失败:', error)
    ElMessage.error('发送帧到后端失败: ' + error.message)
  }
}

// 绘制检测框（修改为响应式绘制）
const drawDetectionBoxes = (results) => {
  if (!overlayCanvas.value || !localVideo.value) return
  
  // 更新最后检测时间
  lastDetectionTime.value = Date.now()
  
  // 清除之前的超时定时器
  if (detectionTimeout.value) {
    clearTimeout(detectionTimeout.value)
  }
  
  // 设置新的超时定时器，如果2秒内没有新的检测结果，清除检测框
  detectionTimeout.value = setTimeout(() => {
    clearDetectionBoxes()
  }, 2000)
  
  const canvas = overlayCanvas.value
  const ctx = canvas.getContext('2d')
  
  // 清除之前的检测框
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 如果没有检测结果，直接返回
  if (!results || results.length === 0) {
    return
  }
  
  // 计算缩放比例（从实际视频尺寸到显示尺寸）
  const scaleX = canvas.width / (captureCanvas.value ? captureCanvas.value.width : 1280)
  const scaleY = canvas.height / (captureCanvas.value ? captureCanvas.value.height : 720)
  
  // 绘制新的检测框
  results.forEach(result => {
    // 处理后端返回的数据格式，可能是class_name或className
    const className = result.class_name || result.className
    const confidence = result.confidence || 0
    const x = result.x || 0
    const y = result.y || 0
    const w = result.w || result.width || 0
    const h = result.h || result.height || 0
    
    // 缩放坐标和尺寸以适应显示尺寸
    const scaledX = x * scaleX
    const scaledY = y * scaleY
    const scaledWidth = w * scaleX
    const scaledHeight = h * scaleY
    
    // 设置样式 - 使用红色框，参考HTML示例
    ctx.strokeStyle = '#FF0000'
    ctx.lineWidth = 2
    ctx.font = '14px Arial'
    ctx.fillStyle = '#FF0000'
    
    // 绘制矩形框
    ctx.strokeRect(scaledX, scaledY, scaledWidth, scaledHeight)
    
    // 绘制标签
    const label = `${className} ${(confidence * 100).toFixed(0)}%`
    const textWidth = ctx.measureText(label).width
    
    // 绘制标签背景
    ctx.fillStyle = 'rgba(255, 0, 0, 0.7)'
    ctx.fillRect(scaledX, scaledY - 20, textWidth + 6, 20)
    
    // 绘制标签文字
    ctx.fillStyle = '#FFFFFF'
    ctx.fillText(label, scaledX + 3, scaledY - 5)
  })
}

// 清除检测框
const clearDetectionBoxes = () => {
  if (!overlayCanvas.value) return
  
  const canvas = overlayCanvas.value
  const ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 清空检测结果
  detectionResults.value = []
  
  console.log('检测框已清除（超时）')
}

// 开始绘制循环
const startDrawingLoop = () => {
  // 确保覆盖层canvas已正确初始化
  if (!overlayCanvas.value) {
    console.error('覆盖层canvas未找到')
    return
  }
  
  // 清除之前的绘制内容
  const ctx = overlayCanvas.value.getContext('2d')
  ctx.clearRect(0, 0, overlayCanvas.value.width, overlayCanvas.value.height)
  
  console.log('覆盖层canvas绘制循环已初始化')
}

// 将RTSP地址转换为HLS地址的函数
// 注意：这只是一个示例转换，实际项目中需要根据您的转码服务来调整
const convertRtspToHls = (rtspUrl) => {
  // 示例转换规则：
  // rtsp://admin:password@192.168.1.100:554/Streaming/Channels/101
  // 转换为：
  // http://192.168.1.100:8080/hls/stream101.m3u8
  
  try {
    // 提取IP地址（排除用户名密码和端口）
    // 使用正则表达式匹配，确保只获取IP部分
    const ipMatch = rtspUrl.match(/rtsp:\/\/(?:[^@]+@)?([^:\/]+)/)
    if (!ipMatch) {
      throw new Error('无效的RTSP地址格式')
    }
    
    const ip = ipMatch[1] // 现在ip只包含IP地址，不包含端口
    
    // 提取通道号
    const channelMatch = rtspUrl.match(/Channels\/(\d+)/)
    const channel = channelMatch ? channelMatch[1] : '1'
    
    // 构建HLS地址
    // 使用固定端口8080作为转码服务端口
    const hlsUrl = `http://${ip}:8080/hls/stream${channel}.m3u8`
    
    return hlsUrl
  } catch (error) {
    console.error('RTSP到HLS地址转换失败:', error)
    
    // 如果转换失败，返回一个默认的示例地址
    if (!rtspConvertWarningShown.value) {
      ElMessage.warning('无法自动转换RTSP地址，使用示例HLS地址')
      rtspConvertWarningShown.value = true
    }
    return 'http://localhost:8080/hls/stream.m3u8'
  }
}

// 测试视频显示
const testVideoDisplay = () => {
  if (!localVideo.value || !overlayCanvas.value) {
    ElMessage.error('视频元素或画布元素不存在')
    return
  }
  
  console.log('开始测试视频显示...')
  
  // 获取视频和canvas的上下文
  const video = localVideo.value
  const canvas = overlayCanvas.value
  const ctx = canvas.getContext('2d')
  
  // 检查视频状态
  console.log('视频状态检查:')
  console.log('- 视频元素存在:', !!video)
  console.log('- 视频源对象存在:', !!video.srcObject)
  console.log('- 视频就绪状态:', video.readyState)
  console.log('- 视频当前时间:', video.currentTime)
  console.log('- 视频是否暂停:', video.paused)
  console.log('- 视频尺寸:', video.videoWidth, 'x', video.videoHeight)
  
  // 检查canvas状态
  console.log('Canvas状态检查:')
  console.log('- Canvas元素存在:', !!canvas)
  console.log('- Canvas尺寸:', canvas.width, 'x', canvas.height)
  
  // 尝试绘制视频到canvas
  try {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    console.log('成功绘制视频帧到canvas')
    
    // 添加一个简单的测试标记
    ctx.fillStyle = 'red'
    ctx.fillRect(10, 10, 50, 50)
    console.log('添加了红色测试方块')
    
    ElMessage.success('视频显示测试完成，请检查是否有红色方块')
  } catch (error) {
    console.error('绘制视频帧失败:', error)
    ElMessage.error('绘制视频帧失败: ' + error.message)
  }
}

// 显示视频调试信息
const showVideoDebugInfo = () => {
  if (!localVideo.value) {
    ElMessage.error('视频元素不存在')
    return
  }
  
  const debugInfo = {
    '视频元素存在': !!localVideo.value,
    '视频源对象': !!localVideo.value.srcObject,
    '视频就绪状态': localVideo.value.readyState,
    '网络状态': localVideo.value.networkState,
    '当前时间': localVideo.value.currentTime,
    '视频时长': localVideo.value.duration,
    '是否暂停': localVideo.value.paused,
    '是否结束': localVideo.value.ended,
    '是否静音': localVideo.value.muted,
    '视频宽度': localVideo.value.videoWidth,
    '视频高度': localVideo.value.videoHeight,
    '元素宽度': localVideo.value.offsetWidth,
    '元素高度': localVideo.value.offsetHeight,
    'CSS显示状态': window.getComputedStyle(localVideo.value).display,
    'CSS可见性': window.getComputedStyle(localVideo.value).visibility,
    'CSS不透明度': window.getComputedStyle(localVideo.value).opacity,
    'CSS z-index': window.getComputedStyle(localVideo.value).zIndex,
    '错误信息': localVideo.value.error ? localVideo.value.error.message : '无'
  }
  
  console.table(debugInfo)
  
  // 创建一个更友好的错误信息
  let errorMessage = '视频调试信息:\n\n'
  for (const [key, value] of Object.entries(debugInfo)) {
    errorMessage += `${key}: ${value}\n`
  }
  
  ElMessage({
    message: errorMessage,
    type: 'info',
    duration: 0,
    showClose: true
  })
}

// 断开摄像头连接
const disconnectCamera = () => {
  if (videoSource.value === 'network') {
    showPlayer.value = false
    connected.value = false
    hlsUrl.value = ''
    ElMessage.info('已断开网络摄像头连接')
  } else if (videoSource.value === 'local') {
    // 停止检测循环
    if (detectionInterval.value) {
      clearInterval(detectionInterval.value)
      detectionInterval.value = null
    }
    
    // 清除检测超时定时器
    if (detectionTimeout.value) {
      clearTimeout(detectionTimeout.value)
      detectionTimeout.value = null
    }
    
    // 停止视频流
    if (currentStream.value) {
      currentStream.value.getTracks().forEach(track => track.stop())
      currentStream.value = null
    }
    
    // 重置视频播放状态
    videoPlaying.value = false
    
    // 清除视频源
    if (localVideo.value) {
      localVideo.value.srcObject = null
    }
    
    // 清除覆盖层canvas
    if (overlayCanvas.value) {
      const ctx = overlayCanvas.value.getContext('2d')
      ctx.clearRect(0, 0, overlayCanvas.value.width, overlayCanvas.value.height)
    }
    
    // 清除捕获canvas
    if (captureCanvas.value) {
      const ctx = captureCanvas.value.getContext('2d')
      ctx.clearRect(0, 0, captureCanvas.value.width, captureCanvas.value.height)
    }
    
    // 清空画布
    if (localCanvas.value) {
      const ctx = localCanvas.value.getContext('2d')
      ctx.clearRect(0, 0, localCanvas.value.width, localCanvas.value.height)
    }
    
    connected.value = false
    detectionActive.value = false
    detectionResults.value = []
    
    ElMessage.info('已断开本地摄像头连接')
  }
}

// 刷新视频流
const refreshStream = () => {
  if (videoSource.value === 'network' && rtspPlayer.value) {
    rtspPlayer.value.refresh()
  }
}

// 切换本地摄像头
const changeLocalCamera = (deviceId) => {
  const camera = localCameras.value.find(item => item.deviceId === deviceId)
  if (camera) {
    selectedLocalCameraName.value = camera.label
    // 如果已连接，自动重新连接
    if (connected.value) {
      disconnectCamera()
      setTimeout(() => {
        connectCamera()
      }, 500)
    }
  }
}

// 切换视频源
const changeVideoSource = (source) => {
  // 如果已连接，先断开
  if (connected.value) {
    disconnectCamera()
  }
  
  // 重置表单
  if (source === 'network') {
    monitorForm.value.cameraId = ''
    monitorForm.value.rtspUrl = ''
  } else if (source === 'local') {
    // 获取本地摄像头列表
    getLocalCameras()
  }
}

// 获取本地摄像头列表
const getLocalCameras = async () => {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices()
    const videoDevices = devices.filter(device => device.kind === 'videoinput')
    
    if (videoDevices.length === 0) {
      console.warn('未检测到可用摄像头')
      return []
    }
    
    // 创建新的摄像头信息数组
    const cameras = videoDevices.map((device, index) => {
      const label = device.label || `摄像头 ${index + 1}`
      const isFrontCamera = label.toLowerCase().includes('front') || 
                           label.toLowerCase().includes('前置') ||
                           label.toLowerCase().includes('user')
      
      return {
        deviceId: device.deviceId,
        label: isFrontCamera ? `${label} (前置)` : label,
        isFrontCamera: isFrontCamera || (index === 0 && videoDevices.length === 1)
      }
    })
    
    // 同时更新 localCameras.value 供其他功能使用
    localCameras.value = cameras
    
    // 自动选择前置摄像头
    const frontCamera = cameras.find(camera => camera.isFrontCamera)
    if (frontCamera) {
      selectedLocalCamera.value = frontCamera.deviceId
      selectedLocalCameraName.value = frontCamera.label
    } else if (cameras.length > 0) {
      selectedLocalCamera.value = cameras[0].deviceId
      selectedLocalCameraName.value = cameras[0].label
    }
    
    return cameras
    
  } catch (error) {
    console.error('获取摄像头权限失败:', error)
    return []
  }
}

// 选择摄像头
const selectCamera = (camera) => {
  monitorForm.value.cameraId = camera.cameraId
  monitorForm.value.rtspUrl = camera.rtspUrl
  currentCameraName.value = camera.cameraName
  currentCameraLocation.value = camera.location
}

// 切换摄像头
const changeCamera = (cameraId) => {
  const camera = cameraList.value.find(item => item.cameraId === cameraId)
  if (camera) {
    selectCamera(camera)
    // 如果已连接，自动重新连接
    if (connected.value) {
      disconnectCamera()
      setTimeout(() => {
        connectCamera()
      }, 500)
    }
  }
}

// 获取摄像头列表
const getList = async () => {
  loading.value = true
  try {
    const queryParams = {
      pageNum: 1,
      pageSize: 100
    }
    const res = await getCameraList(queryParams)
    
    if (res && res.rows && Array.isArray(res.rows)) {
      cameraList.value = res.rows.map(camera => {
        if (camera.cameraId === 0) {
          return {
            cameraId: camera.cameraId,
            cameraName: camera.cameraName,
            location: camera.location,
            level: camera.level,
            type: 'local',
            isFrontCamera: true
          }
        } else {
          return {
            cameraId: camera.cameraId,
            cameraName: camera.cameraName,
            location: camera.location,
            level: camera.level,
            rtspUrl: camera.rtsp || '',
            type: 'network'
          }
        }
      })
    } else {
      cameraList.value = []
    }
    
    if (cameraList.value.length === 0) {
      cameraList.value = [
        {
          cameraId: 0,
          cameraName: '前置摄像头',
          location: '本地设备',
          level: 1,
          type: 'local',
          isFrontCamera: true
        },
        {
          cameraId: 'camera001',
          cameraName: '前门摄像头',
          location: '前门入口',
          rtspUrl: 'rtsp://admin:lmp220607@192.168.0.101:554/Streaming/Channels/1',
          level: 1,
          type: 'network'
        },
        {
          cameraId: 'camera002',
          cameraName: '后门摄像头',
          location: '后门入口',
          rtspUrl: 'rtsp://192.168.1.101:554/Streaming/Channels/101',
          level: 2,
          type: 'network'
        },
        {
          cameraId: 'camera003',
          cameraName: '大厅摄像头',
          location: '大厅中央',
          rtspUrl: 'rtsp://192.168.1.102:554/Streaming/Channels/101',
          level: 1,
          type: 'network'
        }
      ]
    }
  } catch (error) {
    console.error('获取摄像头列表失败:', error)
    ElMessage.error('获取摄像头列表失败')
    
    cameraList.value = [
      {
        cameraId: 0,
        cameraName: '前置摄像头',
        location: '本地设备',
        level: 1,
        type: 'local',
        isFrontCamera: true
      },
      {
        cameraId: 'camera001',
        cameraName: '前门摄像头',
        location: '前门入口',
        rtspUrl: 'rtsp://admin:lmp220607@192.168.0.101:554/Streaming/Channels/1',
        level: 1,
        type: 'network'
      },
      {
        cameraId: 'camera002',
        cameraName: '后门摄像头',
        location: '后门入口',
        rtspUrl: 'rtsp://192.168.1.101:554/Streaming/Channels/101',
        level: 2,
        type: 'network'
      },
      {
        cameraId: 'camera003',
        cameraName: '大厅摄像头',
        location: '大厅中央',
        rtspUrl: 'rtsp://192.168.1.102:554/Streaming/Channels/101',
        level: 1,
        type: 'network'
      }
    ]
  } finally {
    loading.value = false
  }
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

// 切换显示模式
const changeDisplayMode = () => {
  if (multiCameraMode.value) {
    showPlayer.value = false
  }
}

// 组件挂载时获取摄像头列表
onMounted(() => {
  // 初始化监控显示屏
  initializeMonitorDisplay()
})

// 组件卸载时清理资源
onBeforeUnmount(() => {
  // 清理检测循环
  if (detectionInterval.value) {
    clearInterval(detectionInterval.value)
  }
  
  // 清理视频流
  if (currentStream.value) {
    currentStream.value.getTracks().forEach(track => track.stop())
  }
  
  // 清理本地视频流
  localVideoRefs.value.forEach(videoRef => {
    if (videoRef && videoRef.srcObject) {
      videoRef.srcObject.getTracks().forEach(track => track.stop())
    }
  })
  
  // 清理放大对话框中的本地视频流
  if (enlargedLocalVideo.value && enlargedLocalVideo.value.srcObject) {
    enlargedLocalVideo.value.srcObject.getTracks().forEach(track => track.stop())
  }
})

// 初始化监控显示屏
const initializeMonitorDisplay = async () => {
  try {
    loading.value = true
    
    let cameras = []
    
    try {
      const queryParams = {
        pageNum: 1,
        pageSize: 100
      }
      const response = await getCameraList(queryParams)
      if (response && response.rows && Array.isArray(response.rows)) {
        cameras = response.rows.map(camera => {
          if (camera.cameraId === 0) {
            return {
              id: camera.cameraId,
              cameraName: camera.cameraName,
              location: camera.location,
              level: camera.level,
              type: 'local',
              isFrontCamera: true
            }
          } else {
            return {
              id: camera.cameraId,
              cameraName: camera.cameraName,
              location: camera.location,
              level: camera.level,
              rtspUrl: camera.rtsp || '',
              type: 'network'
            }
          }
        })
      }
    } catch (apiError) {
      console.error('获取摄像头列表失败:', apiError)
    }
    
    allCameras.value = cameras
    
    const savedOrder = loadCameraOrder()
    const allCamerasOrdered = savedOrder ? applySavedCameraOrder(allCameras.value, savedOrder) : allCameras.value
    
    const displayCount = Math.min(allCamerasOrdered.length, 9)
    displayCameras.value = allCamerasOrdered.slice(0, displayCount)
    
    await nextTick()
    await initializeLocalVideoStreams()
    
  } catch (error) {
    console.error('初始化监控显示屏失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化本地摄像头视频流
const initializeLocalVideoStreams = async () => {
  for (let i = 0; i < displayCameras.value.length; i++) {
    const camera = displayCameras.value[i]
    if (camera.type === 'local') {
      try {
        const constraints = {
          video: {
            width: { ideal: 640 },
            height: { ideal: 480 },
            facingMode: camera.isFrontCamera ? 'user' : undefined,
            deviceId: (!camera.isFrontCamera && camera.deviceId) ? { exact: camera.deviceId } : undefined
          },
          audio: false
        }
        const stream = await navigator.mediaDevices.getUserMedia(constraints)
        const videoRef = localVideoRefs.value[i]
        if (videoRef) {
          videoRef.srcObject = stream
          await videoRef.play()
        }
      } catch (error) {
        console.error(`初始化本地摄像头 ${camera.cameraName} 失败:`, error)
      }
    }
  }
}

// 设置本地视频引用
const setLocalVideoRef = (el, index) => {
  if (el) {
    localVideoRefs.value[index] = el
  }
}

// 获取网格布局类名
const getGridClass = () => {
  const count = displayCameras.value.length
  if (count <= 1) return 'grid-1'
  if (count <= 4) return 'grid-2x2'
  if (count <= 6) return 'grid-2x3'
  return 'grid-3x3'
}

// 获取可用摄像头（未显示的摄像头）
const availableCameras = computed(() => {
  return allCameras.value.filter(camera => 
    !displayCameras.value.some(displayed => displayed.id === camera.id)
  )
})

// 判断摄像头是否正在显示
const isCameraDisplayed = (camera) => {
  return displayCameras.value.some(displayed => displayed.id === camera.id)
}

// 点击摄像头放大
const handleCameraClick = async (camera) => {
  try {
    enlargedCamera.value = camera
    enlargeDialogVisible.value = true
    
    if (camera.type === 'local') {
      await nextTick()
      if (enlargedLocalVideo.value) {
        let constraints = {
          video: {
            facingMode: camera.isFrontCamera ? 'user' : undefined,
            deviceId: (!camera.isFrontCamera && camera.deviceId) ? { exact: camera.deviceId } : undefined
          },
          audio: false
        }
        
        try {
          const devices = await navigator.mediaDevices.enumerateDevices()
          const videoDevices = devices.filter(device => device.kind === 'videoinput')
          const targetDevice = videoDevices.find(device => {
            if (camera.isFrontCamera) {
              return device.label.toLowerCase().includes('front') || device.label.toLowerCase().includes('前置')
            } else if (camera.deviceId) {
              return device.deviceId === camera.deviceId
            }
            return false
          })
          
          if (targetDevice) {
            const stream = await navigator.mediaDevices.getUserMedia({
              video: {
                deviceId: { exact: targetDevice.deviceId }
              },
              audio: false
            })
            const track = stream.getVideoTracks()[0]
            const capabilities = track.getCapabilities()
            
            if (capabilities.width && capabilities.height) {
              constraints.video.width = { ideal: capabilities.width.max }
              constraints.video.height = { ideal: capabilities.height.max }
              console.log('摄像头最大分辨率:', capabilities.width.max, 'x', capabilities.height.max)
            }
            
            stream.getTracks().forEach(track => track.stop())
          }
        } catch (error) {
          console.error('获取摄像头能力失败:', error)
        }
        
        const stream = await navigator.mediaDevices.getUserMedia(constraints)
        enlargedLocalVideo.value.srcObject = stream
        await enlargedLocalVideo.value.play()
        
        console.log('视频流分辨率:', enlargedLocalVideo.value.videoWidth, 'x', enlargedLocalVideo.value.videoHeight)
        
        await nextTick()
        
        if (enlargedCaptureCanvas.value && enlargedLocalVideo.value) {
          const video = enlargedLocalVideo.value
          const canvas = enlargedCaptureCanvas.value
          canvas.width = video.videoWidth
          canvas.height = video.videoHeight
          console.log('Capture canvas尺寸:', canvas.width, 'x', canvas.height)
        }
        
        startEnlargedDetectionLoop()
      }
    }
  } catch (error) {
    console.error('放大摄像头失败:', error)
    ElMessage.error('放大摄像头失败: ' + error.message)
  }
}

const startEnlargedDetectionLoop = () => {
  if (enlargedDetectionInterval.value) {
    clearInterval(enlargedDetectionInterval.value)
  }
  
  enlargedDetectionActive.value = true
  
  enlargedDetectionInterval.value = setInterval(() => {
    sendEnlargedFrameToBackend()
  }, 500)
}

const sendEnlargedFrameToBackend = () => {
  if (!enlargedLocalVideo.value || !enlargedCaptureCanvas.value || !enlargeDialogVisible.value) return
  
  console.log('开始发送帧到后端...')
  
  try {
    const video = enlargedLocalVideo.value
    const canvas = enlargedCaptureCanvas.value
    const ctx = canvas.getContext('2d')
    
    if (canvas.width !== video.videoWidth || canvas.height !== video.videoHeight) {
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      console.log('调整capture canvas尺寸:', canvas.width, 'x', canvas.height)
    }
    
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    console.log('已绘制视频帧到canvas')
    
    canvas.toBlob(async (blob) => {
      if (!blob) {
        console.error('无法创建图像Blob')
        return
      }
      
      console.log('成功创建图像Blob, 大小:', blob.size, 'bytes')
      
      try {
        const formData = new FormData()
        const imageFile = new File([blob], 'capture.jpg', { type: 'image/jpeg' })
        formData.append('image', imageFile)
        
        const isFrontCamera = enlargedCamera.value.isFrontCamera || false
        
        const cameraInfo = {
          cameraId: isFrontCamera ? 0 : (enlargedCamera.value.id || 1),
          cameraName: enlargedCamera.value.cameraName || "放大摄像头",
          level: enlargedCamera.value.level || 1,
          location: enlargedCamera.value.location || "放大"
        }
        formData.append('cameraSubmitQuery', JSON.stringify(cameraInfo))
        
        console.log('发送检测请求...')
        const data = await detectImage(formData)
        console.log('收到检测响应:', data)
        
        if (Array.isArray(data)) {
          enlargedDetectionResults.value = data
          drawEnlargedDetectionBoxes(data)
        }
      } catch (error) {
        console.error('检测请求失败:', error)
      }
    }, 'image/jpeg', 1.0)
  } catch (error) {
    console.error('发送帧到后端失败:', error)
  }
}

const drawEnlargedDetectionBoxes = (results) => {
  if (!enlargedOverlayCanvas.value || !enlargedLocalVideo.value) return
  
  enlargedLastDetectionTime.value = Date.now()
  
  if (enlargedDetectionTimeout.value) {
    clearTimeout(enlargedDetectionTimeout.value)
  }
  
  enlargedDetectionTimeout.value = setTimeout(() => {
    clearEnlargedDetectionBoxes()
  }, 500)
  
  const canvas = enlargedOverlayCanvas.value
  const ctx = canvas.getContext('2d')
  
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  if (!results || results.length === 0) {
    return
  }
  
  const video = enlargedLocalVideo.value
  const scaleX = canvas.width / video.videoWidth
  const scaleY = canvas.height / video.videoHeight
  
  results.forEach(result => {
    const className = result.class_name || result.className
    const confidence = result.confidence || 0
    const x = result.x || 0
    const y = result.y || 0
    const w = result.w || result.width || 0
    const h = result.h || result.height || 0
    
    const scaledX = x * scaleX
    const scaledY = y * scaleY
    const scaledWidth = w * scaleX
    const scaledHeight = h * scaleY
    
    ctx.strokeStyle = '#FF0000'
    ctx.lineWidth = 3
    ctx.font = '16px Arial'
    ctx.fillStyle = '#FF0000'
    
    ctx.strokeRect(scaledX, scaledY, scaledWidth, scaledHeight)
    
    const label = `${className} ${(confidence * 100).toFixed(0)}%`
    const textWidth = ctx.measureText(label).width
    
    ctx.fillRect(scaledX, scaledY - 24, textWidth + 10, 24)
    ctx.fillStyle = '#FFFFFF'
    ctx.fillText(label, scaledX + 5, scaledY - 7)
  })
}

const clearEnlargedDetectionBoxes = () => {
  if (enlargedOverlayCanvas.value) {
    const ctx = enlargedOverlayCanvas.value.getContext('2d')
    ctx.clearRect(0, 0, enlargedOverlayCanvas.value.width, enlargedOverlayCanvas.value.height)
  }
}

// 关闭放大对话框
const closeEnlargeDialog = () => {
  if (enlargedDetectionInterval.value) {
    clearInterval(enlargedDetectionInterval.value)
    enlargedDetectionInterval.value = null
  }
  
  if (enlargedDetectionTimeout.value) {
    clearTimeout(enlargedDetectionTimeout.value)
    enlargedDetectionTimeout.value = null
  }
  
  enlargedDetectionActive.value = false
  enlargedDetectionResults.value = []
  
  if (enlargedLocalVideo.value && enlargedLocalVideo.value.srcObject) {
    enlargedLocalVideo.value.srcObject.getTracks().forEach(track => track.stop())
    enlargedLocalVideo.value.srcObject = null
  }
  
  enlargedCamera.value = null
  enlargeDialogVisible.value = false
}

// 右键点击处理
const handleRightClick = (event, camera, index) => {
  console.log('右键点击摄像头:', camera)
  console.log('当前显示的摄像头:', displayCameras.value)
  console.log('所有摄像头:', allCameras.value)
  console.log('可用摄像头:', availableCameras.value)
  
  contextMenu.value = {
    visible: true,
    x: event.clientX,
    y: event.clientY,
    targetIndex: index
  }
}

// 切换摄像头
const switchCamera = async (newCamera) => {
  console.log('切换到摄像头:', newCamera)
  
  try {
    const targetIndex = contextMenu.value.targetIndex
    const oldCamera = displayCameras.value[targetIndex]
    
    console.log('目标索引:', targetIndex)
    console.log('旧摄像头:', oldCamera)
    
    // 停止旧摄像头的视频流（如果是本地摄像头）
    if (oldCamera.type === 'local') {
      const oldVideoRef = localVideoRefs.value[targetIndex]
      if (oldVideoRef && oldVideoRef.srcObject) {
        oldVideoRef.srcObject.getTracks().forEach(track => track.stop())
        oldVideoRef.srcObject = null
      }
    }
    
    // 替换摄像头
    displayCameras.value[targetIndex] = newCamera
    
    console.log('替换后的显示摄像头:', displayCameras.value)
    
    // 如果新摄像头是本地摄像头，初始化视频流
    if (newCamera.type === 'local') {
      await nextTick()
      const constraints = {
        video: {
          width: { ideal: 640 },
          height: { ideal: 480 },
          deviceId: newCamera.deviceId ? { exact: newCamera.deviceId } : undefined
        },
        audio: false
      }
      console.log('初始化本地摄像头，约束:', constraints)
      const stream = await navigator.mediaDevices.getUserMedia(constraints)
      const videoRef = localVideoRefs.value[targetIndex]
      if (videoRef) {
        videoRef.srcObject = stream
        await videoRef.play()
        console.log('本地摄像头播放成功')
      }
    }
    
    contextMenu.value.visible = false
    ElMessage.success(`已切换到 ${newCamera.cameraName}`)
  } catch (error) {
    console.error('切换摄像头失败:', error)
    ElMessage.error('切换摄像头失败: ' + error.message)
  }
}

// 拖拽开始
const handleDragStart = (event, index) => {
  draggingIndex.value = index
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('text/plain', index.toString())
}

const saveCameraOrder = () => {
  try {
    const cameraOrder = displayCameras.value.map(camera => camera.id)
    localStorage.setItem('cameraOrder', JSON.stringify(cameraOrder))
    console.log('摄像头顺序已保存到localStorage')
  } catch (error) {
    console.error('保存摄像头顺序失败:', error)
  }
}

const loadCameraOrder = () => {
  try {
    const savedOrder = localStorage.getItem('cameraOrder')
    if (savedOrder) {
      return JSON.parse(savedOrder)
    }
  } catch (error) {
    console.error('加载摄像头顺序失败:', error)
  }
  return null
}

const applySavedCameraOrder = (cameras, savedOrder) => {
  if (!savedOrder || !savedOrder.length) return cameras
  
  const cameraMap = {}
  cameras.forEach(camera => {
    cameraMap[camera.id] = camera
  })
  
  const orderedCameras = []
  savedOrder.forEach(id => {
    if (cameraMap[id]) {
      orderedCameras.push(cameraMap[id])
      delete cameraMap[id]
    }
  })
  
  orderedCameras.push(...Object.values(cameraMap))
  
  return orderedCameras
}

// 拖拽经过
const handleDragOver = (event, index) => {
  if (draggingIndex.value !== index) {
    dragOverIndex.value = index
    event.dataTransfer.dropEffect = 'move'
  }
}

// 拖拽离开
const handleDragLeave = () => {
  dragOverIndex.value = -1
}

// 放置
const handleDrop = async (event, targetIndex) => {
  event.preventDefault()
  const sourceIndex = draggingIndex.value
  
  if (sourceIndex === targetIndex || sourceIndex === -1) {
    return
  }
  
  const sourceCamera = displayCameras.value[sourceIndex]
  const targetCamera = displayCameras.value[targetIndex]
  
  displayCameras.value[sourceIndex] = targetCamera
  displayCameras.value[targetIndex] = sourceCamera
  
  saveCameraOrder()
  
  if (sourceCamera.type === 'local' || targetCamera.type === 'local') {
    await nextTick()
    await initializeLocalVideoStreams()
  }
  
  ElMessage.success('摄像头位置已交换')
}

// 拖拽结束
const handleDragEnd = () => {
  draggingIndex.value = -1
  dragOverIndex.value = -1
}

// 组件卸载时清理资源
onBeforeUnmount(() => {
  // 清理检测循环
  if (detectionInterval.value) {
    clearInterval(detectionInterval.value)
  }
  
  // 清理视频流
  if (currentStream.value) {
    currentStream.value.getTracks().forEach(track => track.stop())
  }
})
</script>

<style scoped lang="scss">
.monitor-container {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 24px;
  h1 {
    margin: 0 0 8px 0;
    color: #1f2329;
    font-weight: 600;
    font-size: 22px;
  }
  p {
    margin: 0;
    color: #86909c;
    font-size: 14px;
  }
}

.monitor-content {
  display: flex;
  gap: 20px;
  flex: 1;
  height: calc(100% - 80px);
}

/* 大监控显示屏 */
.monitor-display {
  flex: 2;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.camera-grid {
  display: grid;
  gap: 16px;
  width: 100%;
  height: 100%;
  flex: 1;

  &.grid-1 {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr;
  }
  &.grid-2x2 {
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, 1fr);
  }
  &.grid-2x3 {
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(2, 1fr);
  }
  &.grid-3x3 {
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(3, 1fr);
  }
}

.camera-grid-item {
  background: #000;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  min-height: 180px;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 2px solid transparent;

  &:hover {
    transform: scale(1.01);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }
  &.dragging {
    opacity: 0.6;
    cursor: grabbing;
    border: 2px dashed #409eff;
  }
  &.drag-over {
    border: 2px solid #409eff;
    background-color: rgba(64, 158, 255, 0.1);
  }
}

.camera-item-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

/* 摄像头信息栏 */
.camera-item-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: #fff;
  padding: 10px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  z-index: 10;
  backdrop-filter: blur(2px);

  .camera-name {
    font-weight: 500;
    max-width: 60%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .camera-location {
    color: #e0e0e0;
    font-size: 12px;
  }
}

/* 本地摄像头显示 */
.local-camera-display {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}
.local-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 摄像头列表 */
.camera-list {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 16px;
  display: flex;
  flex-direction: column;

  h3 {
    margin: 0 0 16px 0;
    color: #1f2329;
    font-size: 16px;
    font-weight: 600;
  }
}

/* 右键菜单 */
.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  z-index: 9999;
  min-width: 220px;
  max-height: 400px;
  overflow-y: auto;
  animation: fadeIn 0.2s ease;

  .context-menu-title {
    padding: 12px 16px;
    font-weight: 600;
    color: #1f2329;
    border-bottom: 1px solid #f0f2f5;
    background: #f8f9fa;
  }
  .context-menu-item {
    padding: 12px 16px;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: background 0.2s;

    &:hover {
      background: #f5f7fa;
    }
    .camera-name {
      font-weight: 500;
      color: #1f2329;
    }
    .camera-location {
      font-size: 12px;
      color: #86909c;
    }
  }
}

/* 放大对话框 */
.enlarge-dialog {
  .el-dialog {
    margin-top: 5vh !important;
  }
  .el-dialog__header {
    width: 100%;
    background: #1f2329;
    color: #fff;
    padding: 16px 20px;
    border-bottom: 1px solid #333;
  }
  .el-dialog__title {
    color: #fff;
    font-weight: 500;
  }
  .el-dialog__close {
    color: #ccc;
    &:hover {
      color: #fff;
    }
  }
  .el-dialog__body {
    padding: 0;
    height: 70vh;
    width: 100%;
  }
}

.enlarge-display {
  width: 100%;
  height: 100%;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.local-camera-enlarge {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
  position: relative;
}
.local-video-enlarge {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.enlarged-overlay-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 10;
}
.enlarged-capture-canvas {
  display: none;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .monitor-content {
    flex-direction: column;
    height: auto;
  }
  .monitor-display {
    height: 500px;
  }
  .camera-list {
    max-height: 300px;
    margin-top: 20px;
  }
}
@media (max-width: 768px) {
  .monitor-container {
    padding: 12px;
  }
  .camera-grid {
    gap: 10px;
  }
  .camera-grid-item {
    min-height: 120px;
  }
}
</style>