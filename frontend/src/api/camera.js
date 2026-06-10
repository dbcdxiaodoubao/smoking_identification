import request from '@/utils/request'

// 获取摄像头列表
export function getCameraList(params) {
  return request({
    url: '/camera',
    method: 'get',
    params
  })
}

// 获取摄像头详情
export function getCameraDetail(cameraId) {
  return request({
    url: `/camera/${cameraId}`,
    method: 'get'
  })
}

// 新增摄像头
export function addCamera(data) {
  return request({
    url: '/camera',
    method: 'post',
    data
  })
}

// 更新摄像头
export function updateCamera(data) {
  return request({
    url: '/camera',
    method: 'put',
    data
  })
}

// 删除摄像头
export function deleteCamera(cameraId) {
  return request({
    url: `/camera/${cameraId}`,
    method: 'delete'
  })
}