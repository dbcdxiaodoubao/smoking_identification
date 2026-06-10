import request from '@/utils/request'

// 生成AI日报（分析近24小时）
export function generateDailyReport() {
  return request({
    url: '/ai/1',
    method: 'get'
  })
}

// 生成AI周报（分析近7天）
export function generateWeeklyReport() {
  return request({
    url: '/ai/2',
    method: 'get'
  })
}
