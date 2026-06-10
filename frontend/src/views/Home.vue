<template>
  <div class="home-container">
    <div class="home-content">
      <el-row :gutter="20" class="full-height">
        <el-col :span="16" class="left-section">
          <el-card shadow="hover" class="carousel-card">
            <el-carousel :interval="5000" arrow="always" height="511px" indicator-position="none">
              <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
                <div class="carousel-item">
                  <img :src="item.image" :alt="item.title" class="carousel-image" @error="handleImageError" @load="handleImageLoad">
                </div>
              </el-carousel-item>
            </el-carousel>
          </el-card>
        </el-col>

        <el-col :span="8" class="right-section">
          <el-card shadow="hover" class="info-card" v-loading="statsLoading">
            <template #header>
              <div class="card-header">
                <el-icon><DataAnalysis /></el-icon>
                <span>系统概况</span>
              </div>
            </template>
            <div class="stats-grid">
              <div class="stat-item stat-cameras">
                <div class="stat-icon-wrap">
                  <el-icon size="22"><VideoCamera /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ systemStats.cameraCount }}</div>
                  <div class="stat-label">监控摄像头</div>
                </div>
              </div>
              <div class="stat-item stat-total">
                <div class="stat-icon-wrap">
                  <el-icon size="22"><Warning /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ systemStats.todayDetections }}</div>
                  <div class="stat-label">总检测数量</div>
                </div>
              </div>
              <div class="stat-item stat-handled">
                <div class="stat-icon-wrap">
                  <el-icon size="22"><DocumentChecked /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ systemStats.handledCount }}</div>
                  <div class="stat-label">已处理</div>
                </div>
              </div>
              <div class="stat-item stat-pending">
                <div class="stat-icon-wrap">
                  <el-icon size="22"><CircleClose /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ systemStats.pendingCount }}</div>
                  <div class="stat-label">待处理</div>
                </div>
              </div>
            </div>
          </el-card>

          <el-card shadow="hover" class="notice-card">
            <template #header>
              <div class="card-header">
                <el-icon><Bell /></el-icon>
                <span>系统公告</span>
              </div>
            </template>
            <div class="notice-list">
              <div v-if="notices.length === 0" class="empty-placeholder">
                <el-empty description="暂无公告" :image-size="60" />
              </div>
              <div v-for="(notice, index) in notices" :key="index" class="notice-item">
                <el-tag :type="notice.type" size="small">{{ notice.tag }}</el-tag>
                <span class="notice-text">{{ notice.content }}</span>
                <span class="notice-time">{{ notice.time }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="bottom-section">
        <el-col :span="8">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><Reading /></el-icon>
                <span>禁烟知识</span>
              </div>
            </template>
            <div class="knowledge-list">
              <div v-for="(item, index) in knowledgeItems" :key="index" class="knowledge-item">
                <el-icon class="knowledge-icon" :color="item.color"><component :is="item.icon" /></el-icon>
                <div class="knowledge-content">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.content }}</p>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><Grid /></el-icon>
                <span>系统功能</span>
              </div>
            </template>
            <div class="function-grid">
              <div v-for="(func, index) in functions" :key="index" class="function-item" @click="navigateTo(func.path)">
                <div class="function-icon" :style="{ backgroundColor: func.bgColor }">
                  <el-icon :color="func.iconColor"><component :is="func.icon" /></el-icon>
                </div>
                <span>{{ func.name }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>最新预警</span>
              </div>
            </template>
            <div class="incident-list">
              <div v-if="recentIncidents.length === 0" class="empty-placeholder">
                <el-empty description="暂无预警事件" :image-size="60" />
              </div>
              <div v-for="(incident, index) in recentIncidents" :key="index" class="incident-item" @click="handleIncidentClick(incident)">
                <div class="incident-level" :class="`level-${incident.level}`">
                  {{ incident.level === 1 ? '低' : incident.level === 2 ? '中' : '高' }}
                </div>
                <div class="incident-info">
                  <div class="incident-location">{{ incident.location }}</div>
                  <div class="incident-time">{{ formatTime(incident.time) }}</div>
                </div>
                <el-tag :type="incident.status === 'pending' ? 'warning' : 'success'" size="small">
                  {{ incident.status === 'pending' ? '待处理' : '已处理' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 预警详情弹窗 -->
    <el-dialog v-model="incidentDialogVisible" title="预警详情" width="560px">
      <el-descriptions v-if="incidentDetail" :column="2" border>
        <el-descriptions-item label="事件ID">#{{ incidentDetail.incidentId }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ incidentDetail.location }}</el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag :type="incidentDetail.level === 1 ? 'success' : incidentDetail.level === 2 ? 'warning' : 'danger'" size="small">
            {{ incidentDetail.level === 1 ? '低风险' : incidentDetail.level === 2 ? '中风险' : '高风险' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="incidentDetail.status === 0 ? 'danger' : 'success'" size="small">
            {{ incidentDetail.status === 0 ? '未处理' : '已处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatTime(incidentDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="摄像头">#{{ incidentDetail.cameraId }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="incidentDetail && incidentDetail.pictureUrl" style="margin-top:12px">
        <el-image :src="incidentDetail.pictureUrl" fit="contain" style="width:100%;max-height:300px" :preview-src-list="[incidentDetail.pictureUrl]" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCameraList } from '@/api/camera'
import { getIncidentList, getIncidentDetail } from '@/api/incident'
import dayjs from 'dayjs'

const router = useRouter()

const statsLoading = ref(true)

const systemStats = ref({
  cameraCount: 0,
  todayDetections: 0,
  handledCount: 0,
  pendingCount: 0
})

const carouselItems = ref([
  {
    image: '/carousel/image1.png',
    title: '创建无烟校园，守护健康未来',
    description: '校园无烟慧眼系统，智能识别吸烟行为，共建清新校园环境'
  },
  {
    image: '/carousel/image2.png',
    title: '科技助力禁烟管理',
    description: 'AI智能识别，实时监控预警，高效处理违规行为'
  },
  {
    image: '/carousel/image3.png',
    title: '人人参与，共建文明校园',
    description: '携手共创无烟环境，让校园更加美好'
  },
  {
    image: '/carousel/image4.png',
    title: '智能监控，实时预警',
    description: '全天候监控覆盖，及时发现并处理违规行为'
  },
  {
    image: '/carousel/image5.png',
    title: '数据分析，科学决策',
    description: '基于大数据分析，为校园管理提供科学依据'
  }
])

const notices = ref([
  { tag: '系统', type: 'primary', content: '系统已升级至V2.0版本，新增智能识别功能', time: '2小时前' },
  { tag: '通知', type: 'warning', content: '请各管理员及时处理待处理的预警事件', time: '5小时前' },
  { tag: '维护', type: 'info', content: '系统将于本周日凌晨2:00-4:00进行维护', time: '1天前' },
  { tag: '公告', type: 'success', content: '本月禁烟宣传活动即将开始，请积极参与', time: '2天前' }
])

const knowledgeItems = ref([
  { icon: 'WarningFilled', color: '#F56C6C', title: '吸烟危害', content: '吸烟会导致多种疾病，包括肺癌、心脏病等' },
  { icon: 'CircleCheckFilled', color: '#67C23A', title: '戒烟好处', content: '戒烟后身体机能逐渐恢复，降低患病风险' },
  { icon: 'InfoFilled', color: '#409EFF', title: '校园禁烟', content: '校园内全面禁止吸烟，共同维护清新环境' },
  { icon: 'StarFilled', color: '#E6A23C', title: '健康生活', content: '保持健康生活方式，远离烟草危害' }
])

const functions = ref([
  { name: '实时监控', icon: 'VideoCamera', path: '/monitor', bgColor: 'rgba(64, 158, 255, 0.1)', iconColor: '#409EFF' },
  { name: '摄像头管理', icon: 'Camera', path: '/camera', bgColor: 'rgba(103, 194, 58, 0.1)', iconColor: '#67C23A' },
  { name: '预警管理', icon: 'Warning', path: '/incident', bgColor: 'rgba(230, 162, 60, 0.1)', iconColor: '#E6A23C' },
  { name: '处理事件', icon: 'DocumentChecked', path: '/dispose', bgColor: 'rgba(245, 108, 108, 0.1)', iconColor: '#F56C6C' },
  { name: '数据分析', icon: 'DataAnalysis', path: '/dashboard', bgColor: 'rgba(103, 194, 58, 0.1)', iconColor: '#67C23A' },
  { name: '学生管理', icon: 'User', path: '/student', bgColor: 'rgba(64, 158, 255, 0.1)', iconColor: '#409EFF' }
])

const recentIncidents = ref([])

// 预警详情弹窗
const incidentDialogVisible = ref(false)
const incidentDetail = ref(null)

const handleIncidentClick = async (incident) => {
  incidentDetail.value = {
    incidentId: incident.incidentId,
    location: incident.location,
    level: incident.level,
    status: incident.status === 'pending' ? 0 : 1,
    createTime: incident.time,
    cameraId: incident.cameraId,
    pictureUrl: incident.pictureUrl || ''
  }
  incidentDialogVisible.value = true
  // 异步补全图片
  try {
    const res = await getIncidentDetail(incident.incidentId)
    const detail = res.data || res
    if (detail.pictureUrl) {
      incidentDetail.value.pictureUrl = detail.pictureUrl
    }
  } catch (e) { /* ignore */ }
}

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const navigateTo = (path) => {
  router.push(path)
}

const handleImageLoad = (event) => {
}

const handleImageError = (event) => {
  console.error('图片加载失败:', event.target.src)
  console.error('轮播图数据:', carouselItems.value)
}

const loadSystemStats = async () => {
  statsLoading.value = true
  try {
    const cameraRes = await getCameraList({ pageNum: 1, pageSize: 100 })
    systemStats.value.cameraCount = cameraRes.total || 0

    const incidentRes = await getIncidentList({ pageNum: 1, pageSize: 100 })
    const incidents = incidentRes.rows || []
    
    systemStats.value.handledCount = incidents.filter(i => i.status === 1).length
    systemStats.value.pendingCount = incidents.filter(i => i.status === 0).length
    systemStats.value.todayDetections = systemStats.value.handledCount + systemStats.value.pendingCount

    recentIncidents.value = incidents
      .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      .slice(0, 5)
      .map(incident => ({
        incidentId: incident.incidentId,
        level: incident.level,
        location: incident.location,
        time: incident.createTime,
        status: incident.status === 1 ? 'handled' : 'pending',
        cameraId: incident.cameraId,
        pictureUrl: incident.pictureUrl
      }))
  } catch (error) {
    console.error('加载系统统计失败:', error)
  } finally {
    statsLoading.value = false
  }
}

onMounted(() => {
  loadSystemStats()
})
</script>

<style scoped>
.home-container {
  height: 100%;
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.home-content {
  display: flex;
  flex-direction: column;
}

.full-height {
  flex: 1.3;
  height: 0;
  margin-bottom: 16px;
  min-height: 0;
}

.bottom-section {
  flex: 0.7;
  min-height: 0;
  height: 0;
}

.left-section {
  height: 100%;
}

.right-section {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.carousel-card {
  height: 100%;
  overflow: hidden;
}

.carousel-card :deep(.el-card__body) {
  height: 100%;
  padding: 0;
}

.carousel-item {
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-card { flex-shrink: 0; }

.notice-card { flex: 1; display: flex; flex-direction: column; }
.notice-card :deep(.el-card__body) { flex: 1; overflow-y: auto; overflow-x: hidden; }


.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: var(--radius-base);
  background: var(--color-bg-hover);
  border: 1px solid var(--color-border-lighter);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-item:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.stat-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 18px;
}

.stat-cameras .stat-icon-wrap { background: var(--color-primary-bg); color: var(--color-primary); }
.stat-total .stat-icon-wrap    { background: var(--color-success-bg); color: var(--color-success); }
.stat-handled .stat-icon-wrap   { background: rgba(64, 158, 255, 0.1); color: var(--color-primary); }
.stat-pending .stat-icon-wrap   { background: var(--color-warning-bg); color: var(--color-warning); }

.stat-content { flex: 1; }

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 11px;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: var(--color-bg-hover);
  border-radius: 6px;
  transition: all 0.3s;
}

.notice-item:hover {
  background: var(--color-bg-active);
}

.notice-text {
  flex: 1;
  font-size: 14px;
  color: var(--color-text-regular);
}

.notice-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.feature-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.feature-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.knowledge-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.knowledge-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  background: var(--color-bg-hover);
  border-radius: 6px;
}

.knowledge-item:hover {
  background: var(--color-bg-active);
}

.knowledge-icon { font-size: 20px; flex-shrink: 0; }

.knowledge-content h4 {
  margin: 0 0 2px 0;
  font-size: 13px;
  color: var(--color-text-primary);
}

.knowledge-content p {
  margin: 0;
  font-size: 11px;
  color: var(--color-text-secondary);
  line-height: 1.4;
}

.function-grid {
  height: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 10px;
}
.function-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 8px;
  background: var(--color-bg-hover);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.function-item:hover {
  background: var(--color-bg-active);
  transform: translateY(-2px);
}

.function-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.function-item span {
  font-size: 12px;
  color: var(--color-text-regular);
  text-align: center;
}

.incident-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.incident-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: var(--color-bg-hover);
  border-radius: 6px;
}

.incident-item:hover {
  background: var(--color-bg-active);
}

.incident-level {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
}

.level-1 {
  background: #67C23A;
}

.level-2 {
  background: #E6A23C;
}

.level-3 {
  background: #F56C6C;
}

.incident-info {
  flex: 1;
}

.empty-placeholder {
  padding: 20px 0;
}

.incident-location {
  font-size: 14px;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.incident-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}
</style>
