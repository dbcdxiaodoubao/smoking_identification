<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <span class="dashboard-title">监测数据总览</span>
      <div class="header-btns">
        <el-button size="small" @click="refreshAllCharts">
          <el-icon><Refresh /></el-icon>
          刷新全部
        </el-button>
        <el-button type="primary" size="small" @click="exportExcel">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: rgba(64, 158, 255, 0.1);">
              <el-icon size="30" color="#409EFF"><DataLine /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">检测总数</div>
              <div class="stat-value">{{ overviewData.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: rgba(103, 194, 58, 0.1);">
              <el-icon size="30" color="#67C23A"><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">低风险</div>
              <div class="stat-value">{{ levelData.low || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: rgba(230, 162, 60, 0.1);">
              <el-icon size="30" color="#E6A23C"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">中风险</div>
              <div class="stat-value">{{ levelData.medium || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: rgba(245, 108, 108, 0.1);">
              <el-icon size="30" color="#F56C6C"><CircleClose /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">高风险</div>
              <div class="stat-value">{{ levelData.high || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>等级分布</span>
              <div class="header-actions">
                <el-button type="text" @click="refreshLevelChart"><el-icon><Refresh /></el-icon></el-button>
                <el-button type="text" @click="exportChart('level')"><el-icon><Download /></el-icon></el-button>
              </div>
            </div>
          </template>
          <div ref="levelChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>地区分布</span>
              <div class="header-actions">
                <el-button type="text" @click="refreshLocationChart"><el-icon><Refresh /></el-icon></el-button>
                <el-button type="text" @click="exportChart('location')"><el-icon><Download /></el-icon></el-button>
              </div>
            </div>
          </template>
          <div ref="locationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>时间分布</span>
              <div class="header-actions">
                <el-button type="text" @click="refreshTimeChart"><el-icon><Refresh /></el-icon></el-button>
                <el-button type="text" @click="exportChart('time')"><el-icon><Download /></el-icon></el-button>
              </div>
            </div>
          </template>
          <div ref="timeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>趋势分析</span>
              <div class="header-actions">
                <el-button type="text" @click="refreshTrendChart"><el-icon><Refresh /></el-icon></el-button>
                <el-button type="text" @click="exportChart('trend')"><el-icon><Download /></el-icon></el-button>
              </div>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>班级分布</span>
              <div class="header-actions">
                <el-button type="text" @click="refreshClassChart"><el-icon><Refresh /></el-icon></el-button>
                <el-button type="text" @click="exportChart('class')"><el-icon><Download /></el-icon></el-button>
              </div>
            </div>
          </template>
          <div ref="classChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI报告按钮 -->
    <div class="ai-report-button">
      <el-button type="primary" circle size="large" @click="showAIReportDialog">
        <el-icon><Document /></el-icon>
      </el-button>
    </div>

    <!-- AI报告对话框 -->
    <el-dialog
      v-model="aiReportDialogVisible"
      title="AI报告"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="ai-report-options">
        <el-card shadow="hover" class="report-option-card" @click="generateDailyReport">
          <div class="report-option-content">
            <el-icon size="40" color="#409EFF"><Calendar /></el-icon>
            <div class="report-option-text">
              <div class="report-option-title">AI日报</div>
              <div class="report-option-desc">生成今日检测分析报告</div>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="hover" class="report-option-card" @click="generateWeeklyReport">
          <div class="report-option-content">
            <el-icon size="40" color="#67C23A"><DataAnalysis /></el-icon>
            <div class="report-option-text">
              <div class="report-option-title">AI周报</div>
              <div class="report-option-desc">生成本周检测分析报告</div>
            </div>
          </div>
        </el-card>
      </div>
    </el-dialog>

    <!-- AI报告内容对话框 -->
    <el-dialog
      v-model="reportContentDialogVisible"
      :title="reportTitle"
      width="800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="report-content">
        <div v-if="reportLoading" class="loading-animation">
          <div class="loading-spinner"></div>
          <p class="loading-text">AI正在分析数据并生成报告...</p>
          <p class="loading-tip">这可能需要几秒钟时间，请耐心等待</p>
        </div>
        <div v-else-if="reportContent" class="report-text" v-html="reportContent"></div>
        <el-empty v-else description="暂无报告内容" />
      </div>
      <template #footer>
        <el-button @click="reportContentDialogVisible = false" :disabled="reportLoading">关闭</el-button>
        <el-button type="primary" @click="copyReportContent" :disabled="reportLoading || !reportContent">复制报告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'
import {
  getLevelDistribution,
  getLocationDistribution,
  getDetectionOverview,
  getTimeDistribution,
  getTrendData,
  getClassDistribution
} from '@/api/echart'
import { 
  generateDailyReport as apiGenerateDailyReport,
  generateWeeklyReport as apiGenerateWeeklyReport
} from '@/api/report'

// 图表引用
const levelChartRef = ref(null)
const locationChartRef = ref(null)
const timeChartRef = ref(null)
const trendChartRef = ref(null)
const classChartRef = ref(null)

// 图表实例
let levelChart = null
let locationChart = null
let timeChart = null
let trendChart = null
let classChart = null

// 数据
const overviewData = ref({})
const levelData = ref({
  low: 0,
  medium: 0,
  high: 0
})

// AI报告相关
const aiReportDialogVisible = ref(false)
const reportContentDialogVisible = ref(false)
const reportTitle = ref('')
const reportContent = ref('')
const reportLoading = ref(false)

// 显示AI报告对话框
const showAIReportDialog = () => {
  aiReportDialogVisible.value = true
}

// 生成日报
const generateDailyReport = async () => {
  aiReportDialogVisible.value = false
  reportTitle.value = 'AI日报'
  reportContent.value = ''
  reportContentDialogVisible.value = true
  reportLoading.value = true
  
  try {
    const res = await apiGenerateDailyReport()
    
    if (res.data) {
      reportContent.value = formatReportContent(res.data)
    } else {
      ElMessage.error('获取日报数据失败')
    }
  } catch (error) {
    console.error('生成日报失败:', error)
    ElMessage.error('生成日报失败，请稍后重试')
  } finally {
    reportLoading.value = false
  }
}

// 生成周报
const generateWeeklyReport = async () => {
  aiReportDialogVisible.value = false
  reportTitle.value = 'AI周报'
  reportContent.value = ''
  reportContentDialogVisible.value = true
  reportLoading.value = true
  
  try {
    const res = await apiGenerateWeeklyReport()
    
    if (res.data) {
      reportContent.value = formatReportContent(res.data)
    } else {
      ElMessage.error('获取周报数据失败')
    }
  } catch (error) {
    console.error('生成周报失败:', error)
    ElMessage.error('生成周报失败，请稍后重试')
  } finally {
    reportLoading.value = false
  }
}

// 格式化报告内容
const formatReportContent = (content) => {
  if (!content) return ''

  const lines = content.split('\n')
  const result = []
  let inList = false

  for (let i = 0; i < lines.length; i++) {
    let line = lines[i].trim()

    // 空行 → 关闭列表 + 段落间隔
    if (!line) {
      if (inList) { result.push('</ul>'); inList = false }
      continue
    }

    // 加粗
    line = line.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')

    // ### 标题
    if (/^###\s/.test(line)) {
      if (inList) { result.push('</ul>'); inList = false }
      result.push(`<h3>${line.replace(/^###\s*/, '')}</h3>`)
      continue
    }
    // ## 标题
    if (/^##\s/.test(line)) {
      if (inList) { result.push('</ul>'); inList = false }
      result.push(`<h2>${line.replace(/^##\s*/, '')}</h2>`)
      continue
    }
    // # 标题
    if (/^#\s/.test(line)) {
      if (inList) { result.push('</ul>'); inList = false }
      result.push(`<h1>${line.replace(/^#\s*/, '')}</h1>`)
      continue
    }

    // 有序列表项 1. 2. 3. ...
    if (/^\d+\.\s/.test(line)) {
      if (inList) { result.push('</ul>'); inList = false }
      result.push(`<p style="font-weight:600;margin:12px 0 4px">${line}</p>`)
      continue
    }

    // 无序列表项 - xxx
    if (/^-\s/.test(line)) {
      if (!inList) { result.push('<ul>'); inList = true }
      result.push(`<li>${line.replace(/^-\s*/, '')}</li>`)
      continue
    }

    // 普通段落
    if (inList) { result.push('</ul>'); inList = false }
    result.push(`<p>${line}</p>`)
  }

  if (inList) result.push('</ul>')

  const timeStr = new Date().toLocaleString('zh-CN')
  result.push(`<p style="color:var(--color-text-secondary);font-size:12px;margin-top:20px">报告生成时间：${timeStr}</p>`)

  return result.join('\n')
}

// 复制报告内容
const copyReportContent = () => {
  const text = reportContent.value
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<[^>]*>/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .trim()
  
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('报告内容已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    // 等级分布图
    if (levelChartRef.value) {
      levelChart = echarts.init(levelChartRef.value)
    } else {
      console.error('等级分布图容器未找到')
    }
    
    // 地区分布图
    if (locationChartRef.value) {
      locationChart = echarts.init(locationChartRef.value)
    }
    
    // 时间分布图
    if (timeChartRef.value) {
      timeChart = echarts.init(timeChartRef.value)
    }
    
    // 趋势图
    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
    }
    // 班级分布图
    if (classChartRef.value) {
      classChart = echarts.init(classChartRef.value)
    }

    // 加载数据
    loadChartData()
  })
}

// 加载图表数据
const loadChartData = async () => {
  try {
    
    // 获取等级分布数据
    const levelRes = await getLevelDistribution()
    updateLevelChart(levelRes.data || [])
    
    // 获取地区分布数据
    const locationRes = await getLocationDistribution()
    updateLocationChart(locationRes.data || [])
    
    // 获取时间分布数据
    const timeRes = await getTimeDistribution()
    updateTimeChart(timeRes.data || [])
    
    // 获取趋势数据
    const trendRes = await getTrendData()
    updateTrendChart(trendRes.data || [])

    // 获取班级分布数据
    const classRes = await getClassDistribution()
    updateClassChart(classRes.data || [])
    
  } catch (error) {
    console.error('加载图表数据失败:', error)
    
    // 设置默认数据，确保页面有内容显示
    overviewData.value = { total: 0 }
    levelData.value = { low: 0, medium: 0, high: 0 }
    
    // 使用模拟数据初始化图表
    updateLevelChart([])
    updateLocationChart([])
    updateTimeChart([])
    updateTrendChart([])
    updateClassChart([])
  }
}

// 更新等级分布图
const updateLevelChart = (data) => {
  
  // 检查图表实例是否存在
  if (!levelChart) {
    console.error('等级分布图实例不存在，尝试重新初始化')
    if (levelChartRef.value) {
      levelChart = echarts.init(levelChartRef.value)
    } else {
      console.error('等级分布图容器未找到')
      return
    }
  }
  
  // 处理数据
  const levelCounts = { 1: 0, 2: 0, 3: 0 }
  if (data && data.length) {
    data.forEach(item => {
      levelCounts[item.level] = (levelCounts[item.level] || 0) + item.incidentCount
    })
  } else {
    // 使用默认数据，确保图表有内容显示
    levelCounts[1] = 1
    levelCounts[2] = 1
    levelCounts[3] = 1
  }
  
  levelData.value = {
    low: levelCounts[1] || 0,
    medium: levelCounts[2] || 0,
    high: levelCounts[3] || 0
  }
  
  // 更新检测总数为高中低风险数量之和
  overviewData.value.total = levelData.value.low + levelData.value.medium + levelData.value.high
  
  // 确保至少有最小值，避免饼图不显示
  if (levelData.value.low === 0 && levelData.value.medium === 0 && levelData.value.high === 0) {
    levelData.value.low = 1 // 设置一个最小值，确保饼图显示
  }
  
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['低风险', '中风险', '高风险']
    },
    series: [
      {
        name: '风险等级',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: levelData.value.low, name: '低风险', itemStyle: { color: '#67C23A' } },
          { value: levelData.value.medium, name: '中风险', itemStyle: { color: '#E6A23C' } },
          { value: levelData.value.high, name: '高风险', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  
  
  // 清空图表并重新设置选项
  levelChart.clear()
  levelChart.setOption(option, true)
  
  // 延迟强制重新渲染
  setTimeout(() => {
    levelChart.resize()
  }, 100)
}

// 更新地区分布图
const updateLocationChart = (data) => {
  
  // 检查图表实例是否存在
  if (!locationChart) {
    console.error('地区分布图实例不存在，尝试重新初始化')
    if (locationChartRef.value) {
      locationChart = echarts.init(locationChartRef.value)
    } else {
      console.error('地区分布图容器未找到')
      return
    }
  }
  
  // 按地区分组统计
  const locationMap = {}
  if (data && data.length) {
    data.forEach(item => {
      if (!locationMap[item.location]) {
        locationMap[item.location] = 0
      }
      locationMap[item.location] += item.incidentCount
    })
  } else {
    locationMap['暂无数据'] = 1
  }
  
  const locations = Object.keys(locationMap)
  const counts = locations.map(loc => locationMap[loc])
  
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: locations
    },
    series: [
      {
        name: '事件数量',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  
  // 清空图表并重新设置选项
  locationChart.clear()
  locationChart.setOption(option, true)
  
  // 延迟强制重新渲染
  setTimeout(() => {
    locationChart.resize()
  }, 100)
}

// 更新时间分布图
const updateTimeChart = (data) => {
  
  // 检查图表实例是否存在
  if (!timeChart) {
    console.error('时间分布图实例不存在，尝试重新初始化')
    if (timeChartRef.value) {
      timeChart = echarts.init(timeChartRef.value)
    } else {
      console.error('时间分布图容器未找到')
      return
    }
  }
  
  const hours = data && data.length ? data.map(item => item.hourSlot) : ['00:00', '06:00', '12:00', '18:00', '23:59']
  const counts = data && data.length ? data.map(item => item.incidentCount) : [0, 0, 0, 0, 0]
  
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: hours
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: counts,
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  
  
  // 清空图表并重新设置选项
  timeChart.clear()
  timeChart.setOption(option, true)
  
  // 延迟强制重新渲染
  setTimeout(() => {
    timeChart.resize()
  }, 100)
}

// 更新趋势图
const updateTrendChart = (data) => {
  
  // 检查图表实例是否存在
  if (!trendChart) {
    console.error('趋势图实例不存在，尝试重新初始化')
    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
    } else {
      console.error('趋势图容器未找到')
      return
    }
  }
  
  // 按日期排序并提取数据
  let dates = []
  let counts = []
  
  if (data && data.length) {
    // 按日期排序
    const sortedData = [...data].sort((a, b) => new Date(a.incidentDate) - new Date(b.incidentDate))
    dates = sortedData.map(item => item.incidentDate)
    counts = sortedData.map(item => item.incidentCount)
  } else {
    // 如果没有数据，使用默认日期
    const today = new Date()
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      dates.push(date.toISOString().split('T')[0])
      counts.push(0)
    }
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>事件数量: {c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '事件数量'
    },
    series: [
      {
        name: '事件数量',
        type: 'line',
        data: counts,
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        lineStyle: {
          width: 3,
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  
  // 清空图表并重新设置选项
  trendChart.clear()
  trendChart.setOption(option, true)
  
  // 延迟强制重新渲染
  setTimeout(() => {
    trendChart.resize()
  }, 100)
}

// 刷新所有图表
const refreshAllCharts = () => {
  loadChartData()
}

// 导出 Excel 报表
const exportExcel = async () => {
  const loading = ElMessage({ message: '正在生成报表...', type: 'info', duration: 0 })
  try {
    const [levelRes, locRes, timeRes, trendRes, classRes] = await Promise.all([
      getLevelDistribution(), getLocationDistribution(),
      getTimeDistribution(), getTrendData(), getClassDistribution()
    ])
    const wb = XLSX.utils.book_new()

    // 等级分布
    const lv = (levelRes.data || []).map(d => ({ 等级: d.level, 事件数: d.incidentCount }))
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(lv), '等级分布')

    // 地区分布
    const lo = (locRes.data || []).map(d => ({ 位置: d.location, 事件数: d.incidentCount }))
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(lo), '地区分布')

    // 时间分布
    const ti = (timeRes.data || []).map(d => ({ 时间段: d.hourSlot, 事件数: d.incidentCount }))
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(ti), '时间分布')

    // 趋势
    const tr = (trendRes.data || []).map(d => ({ 日期: d.incidentDate, 事件数: d.incidentCount }))
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(tr), '趋势分析')

    // 班级分布
    const cl = (classRes.data || []).map(d => ({ 班级: d.banji, 事件数: d.incidentCount }))
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(cl), '班级分布')

    const date = new Date().toISOString().slice(0, 10)
    XLSX.writeFile(wb, `校园无烟慧眼系统_数据分析报表_${date}.xlsx`)
    ElMessage.success('报表已下载到"下载"文件夹')
  } catch (e) {
    ElMessage.error('生成报表失败')
  } finally {
    loading.close()
  }
}

// 刷新图表
const refreshLevelChart = () => {
  getLevelDistribution().then(data => {
    updateLevelChart(data.data || [])
  })
}

const refreshLocationChart = () => {
  getLocationDistribution().then(data => {
    updateLocationChart(data.data || [])
  })
}

const refreshTimeChart = () => {
  getTimeDistribution().then(data => {
    updateTimeChart(data.data || [])
  })
}

// 更新班级分布图
const updateClassChart = (data) => {
  if (!classChart) {
    if (classChartRef.value) classChart = echarts.init(classChartRef.value)
    else return
  }
  const names = (data && data.length) ? data.map(d => d.banji) : ['暂无数据']
  const counts = (data && data.length) ? data.map(d => d.incidentCount) : [0]
  classChart.clear()
  classChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: names },
    series: [{
      name: '事件数', type: 'bar', data: counts,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#67C23A' },
          { offset: 1, color: '#85CE61' }
        ])
      }
    }]
  }, true)
  setTimeout(() => classChart.resize(), 100)
}

const refreshClassChart = () => {
  getClassDistribution().then(data => updateClassChart(data.data || []))
}

const refreshTrendChart = () => {
  getTrendData().then(data => {
    updateTrendChart(data.data || [])
  })
}

// 导出图表
const exportChart = (chartType) => {
  let chart = null
  let fileName = ''

  switch (chartType) {
    case 'level':
      chart = levelChart
      fileName = '等级分布图'
      break
    case 'location':
      chart = locationChart
      fileName = '地区分布图'
      break
    case 'time':
      chart = timeChart
      fileName = '时间分布图'
      break
    case 'trend':
      chart = trendChart
      fileName = '趋势分析图'
      break
    case 'class':
      chart = classChart
      fileName = '班级分布图'
      break
    default:
      ElMessage.error('未知的图表类型')
      return
  }

  if (!chart) {
    ElMessage.error('图表实例不存在，无法导出')
    return
  }

  try {
    const url = chart.getDataURL({
      type: 'png',
      pixelRatio: 2,
      backgroundColor: '#fff'
    })

    const link = document.createElement('a')
    link.download = `${fileName}_${new Date().toLocaleString('zh-CN').replace(/[\/\s:]/g, '-')}.png`
    link.href = url
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success(`${fileName}导出成功`)
  } catch (error) {
    console.error('导出图表失败:', error)
    ElMessage.error('导出图表失败，请稍后重试')
  }
}

// 窗口大小变化时重新调整图表
const resizeCharts = () => {
  if (levelChart) {
    levelChart.resize()
  }
  if (locationChart) {
    locationChart.resize()
  }
  if (timeChart) {
    timeChart.resize()
  }
  if (trendChart) {
    trendChart.resize()
  }
  if (classChart) {
    classChart.resize()
  }
}

onMounted(() => {
  // 延迟初始化图表，确保DOM完全渲染
  setTimeout(() => {
    initCharts()
    window.addEventListener('resize', resizeCharts)
  }, 100)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  // 销毁图表实例
  if (levelChart) levelChart.dispose()
  if (locationChart) locationChart.dispose()
  if (timeChart) timeChart.dispose()
  if (trendChart) trendChart.dispose()
  if (classChart) classChart.dispose()
})
</script>

<style scoped>
.dashboard-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  overflow-y: auto;
  position: relative;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
}
.header-btns {
  display: flex;
  gap: 10px;
}
.dashboard-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.stats-row { flex-shrink: 0; margin-bottom: 16px; }

.chart-row { margin-bottom: 8px; flex-shrink: 0; }

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.stat-card { margin-bottom: 20px; }

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--color-text-primary);
}

.chart-card { margin-bottom: var(--spacing-md); }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.chart-container {
  height: 300px;
  width: 100%;
  min-height: 300px;
}

.ai-report-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 1000;
  box-shadow: var(--shadow-md);
  transition: transform 0.2s, box-shadow 0.2s;
}

.ai-report-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.ai-report-button .el-button {
  width: 56px;
  height: 56px;
  font-size: 24px;
}

.ai-report-options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-base);
}

.report-option-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.report-option-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.report-option-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.report-option-text {
  margin-left: var(--spacing-base);
  flex: 1;
}

.report-option-title {
  font-size: var(--font-size-md);
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.report-option-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.report-content {
  min-height: 300px;
  max-height: 500px;
  overflow-y: auto;
}

.loading-animation {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  padding: 40px 20px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid var(--color-border-lighter);
  border-top: 4px solid var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: var(--font-size-md);
  color: var(--color-text-primary);
  margin: 0 0 8px 0;
  font-weight: 500;
}

.loading-tip {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.report-text {
  line-height: 1.8;
  color: var(--color-text-primary);
}
.report-text :deep(h3) {
  color: var(--color-primary);
  margin-bottom: var(--spacing-base);
}
.report-text :deep(p) {
  margin-bottom: 12px;
}
.report-text :deep(strong) {
  color: var(--color-text-primary);
  font-weight: 600;
}
.report-text :deep(ul),
.report-text :deep(ol) {
  padding-left: 1.5em;
  margin-bottom: 8px;
}
.report-text :deep(li) {
  margin-bottom: 4px;
  list-style: disc;
}
.report-text :deep(hr) {
  border: none;
  border-top: 1px solid var(--color-border-lighter);
  margin: var(--spacing-base) 0;
}
</style>