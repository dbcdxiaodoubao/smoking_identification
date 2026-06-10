<template>
  <div class="rtsp-player">
    <!-- HLS视频播放器 -->
    <video 
      ref="videoElement" 
      class="video-player" 
      controls 
      autoplay 
      muted 
      width="100%" 
      height="400"
    ></video>
    
    <!-- 连接状态和错误信息 -->
    <div v-if="errorMessage" class="error-message">
      <el-alert
        title="视频播放失败"
        type="error"
        :description="errorMessage"
        show-icon
        :closable="false"
      />
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-loading-spinner />
      <p>正在连接视频流...</p>
    </div>
    
    <!-- FFmpeg转码说明 -->
    <div v-if="showHelp" class="help-panel">
      <el-card header="RTSP转HLS配置说明">
        <h4>使用FFmpeg转码（测试用）：</h4>
        <pre>ffmpeg -i "rtsp://你的摄像头地址" -c:v h264 -c:a aac -f hls -hls_time 2 -hls_list_size 3 -hls_flags delete_segments ./hls/stream.m3u8</pre>
        
        <h4>生产环境推荐方案：</h4>
        <ul>
          <li><strong>EasyDarwin</strong>：轻量级流媒体服务器，支持RTSP转HLS/WebRTC</li>
          <li><strong>ZLMediaKit</strong>：高性能流媒体服务器，支持多种协议转换</li>
          <li><strong>SRS</strong>：简单高效的实时视频服务器</li>
        </ul>
        
        <h4>注意事项：</h4>
        <ul>
          <li>转码服务需配置CORS跨域，否则前端无法请求流地址</li>
          <li>转码RTSP流对服务器性能有消耗，建议用专用流媒体服务器</li>
          <li>浏览器政策要求自动播放视频必须静音</li>
        </ul>
        
        <el-button type="primary" @click="showHelp = false">关闭说明</el-button>
      </el-card>
    </div>
    
    <!-- 帮助按钮 -->
    <div class="help-button">
      <el-button size="small" type="info" @click="showHelp = !showHelp">
        {{ showHelp ? '关闭帮助' : '连接帮助' }}
      </el-button>
    </div>
  </div>
</template>

<script>
import Hls from 'hls.js'

export default {
  name: 'RtspPlayer',
  props: {
    // 接收转码后的HLS地址
    hlsUrl: {
      type: String,
      required: true
    },
    // RTSP原始地址（用于显示）
    rtspUrl: {
      type: String,
      default: ''
    },
    // 摄像头名称
    cameraName: {
      type: String,
      default: ''
    },
    // 摄像头位置
    cameraLocation: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      hls: null, // 存储HLS.js实例
      loading: false,
      errorMessage: '',
      showHelp: false
    }
  },
  watch: {
    // 监听hlsUrl变化，重新加载视频
    hlsUrl(newUrl) {
      if (newUrl) {
        this.loadVideo(newUrl)
      }
    }
  },
  mounted() {
    this.initPlayer()
  },
  beforeDestroy() {
    // 销毁HLS实例，避免内存泄漏
    if (this.hls) {
      this.hls.destroy()
      this.hls = null
    }
  },
  methods: {
    initPlayer() {
      if (!this.hlsUrl) {
        this.errorMessage = '未提供HLS视频流地址'
        return
      }
      
      this.loading = true
      this.errorMessage = ''
      
      try {
        // 检查浏览器是否支持HLS.js
        if (Hls.isSupported()) {
          this.hls = new Hls({
            debug: false,
            enableWorker: true,
            lowLatencyMode: true,
            backBufferLength: 90
          })
          
          // 绑定视频元素
          const video = this.$refs.videoElement
          this.hls.attachMedia(video)
          
          // 加载HLS流
          this.loadVideo(this.hlsUrl)
          
          // HLS事件处理
          this.hls.on(Hls.Events.MANIFEST_PARSED, () => {
            console.log('HLS清单解析完成，开始播放')
            video.play().catch(error => {
              console.error('自动播放失败，可能需要用户交互：', error)
              this.loading = false
            })
          })
          
          this.hls.on(Hls.Events.ERROR, (event, data) => {
            console.error('HLS错误：', data)
            this.loading = false
            this.errorMessage = `HLS流加载失败: ${data.details || '未知错误'}`
            this.provideErrorHelp(data)
          })
          
        } else if (this.$refs.videoElement.canPlayType('application/vnd.apple.mpegurl')) {
          // 使用原生HLS支持（Safari等）
          console.log('使用原生HLS支持')
          const video = this.$refs.videoElement
          video.src = this.hlsUrl
          
          video.addEventListener('loadedmetadata', () => {
            console.log('视频元数据加载完成')
            this.loading = false
            video.play().catch(error => {
              console.error('自动播放失败：', error)
            })
          })
          
          video.addEventListener('error', (e) => {
            console.error('视频播放错误：', e)
            this.loading = false
            this.errorMessage = '视频播放失败，可能是流地址不可用'
          })
          
        } else {
          this.loading = false
          this.errorMessage = '浏览器不支持HLS播放'
        }
        
      } catch (error) {
        console.error('初始化播放器失败：', error)
        this.loading = false
        this.errorMessage = `初始化播放器失败: ${error.message}`
      }
    },
    
    loadVideo(url) {
      if (!url) return
      
      this.loading = true
      this.errorMessage = ''
      
      try {
        if (this.hls) {
          // 使用HLS.js加载流
          this.hls.loadSource(url)
        } else if (this.$refs.videoElement) {
          // 使用原生播放器
          this.$refs.videoElement.src = url
        }
      } catch (error) {
        console.error('加载视频流失败：', error)
        this.loading = false
        this.errorMessage = `加载视频流失败: ${error.message}`
      }
    },
    
    provideErrorHelp(errorData) {
      const helpText = `
        === 视频播放失败分析 ===
        HLS地址: ${this.hlsUrl}
        RTSP地址: ${this.rtspUrl}
        错误详情: ${errorData ? errorData.details : '未知'}
        
        可能的原因：
        1. HLS地址不可访问或不存在
        2. 转码服务未启动或配置错误
        3. 网络连接问题
        4. CORS跨域配置问题
        5. 浏览器不支持HLS播放
        
        解决方案：
        1. 检查转码服务是否正常运行
        2. 确认HLS地址格式正确（通常以.m3u8结尾）
        3. 检查网络连接和防火墙设置
        4. 配置转码服务允许跨域访问
        5. 尝试使用其他浏览器
      `
      
      console.log(helpText)
    },
    
    // 手动刷新视频流
    refreshStream() {
      if (this.hlsUrl) {
        this.loadVideo(this.hlsUrl)
      }
    }
  }
}
</script>

<style scoped>
.rtsp-player {
  width: 100%;
  height: 100%;
  background: #000;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.error-message {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  z-index: 10;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  z-index: 5;
}

.loading-overlay p {
  margin-top: 10px;
  font-size: 14px;
}

.help-panel {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  z-index: 20;
  overflow-y: auto;
  padding: 20px;
}

.help-panel pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
}

.help-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 15;
}
</style>