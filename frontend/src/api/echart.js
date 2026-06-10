import request from '@/utils/request'

// 获取等级分布数据
export function getLevelDistribution() {
  return request({
    url: '/echart/level',
    method: 'get'
  })
}

// 获取地区分布数据
export function getLocationDistribution() {
  return request({
    url: '/echart/location',
    method: 'get'
  })
}

// 获取吸烟行为检测总次数
export function getDetectionOverview() {
  return request({
    url: '/echart/overview',
    method: 'get'
  })
}

// 获取时间分布数据
export function getTimeDistribution() {
  return request({
    url: '/echart/time',
    method: 'get'
  })
}

// 获取趋势数据
export function getTrendData() {
  return request({
    url: '/echart/trend',
    method: 'get'
  })
}

// 获取班级分布数据
export function getClassDistribution() {
  return request({
    url: '/echart/class',
    method: 'get'
  })
}