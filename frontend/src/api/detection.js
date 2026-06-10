import request from '@/utils/request'

// 图片检测接口（支持FormData和文件对象）
export function detectImage(data, cameraInfo = null) {
  // 如果传入的是文件对象，创建FormData
  if (data instanceof File) {
    const formData = new FormData()
    formData.append('image', data)
    
    // 如果提供了摄像头信息，添加到FormData中
    if (cameraInfo) {
      formData.append('cameraSubmitQuery', JSON.stringify(cameraInfo))
    }
    
    return request({
      url: '/detection',
      method: 'post',
      data: formData
    })
  }
  // 如果传入的是FormData，直接使用
  else if (data instanceof FormData) {
    return request({
      url: '/detection',
      method: 'post',
      data: data
    })
  }
  // 其他情况，抛出错误
  else {
    throw new Error('detectImage函数只支持File对象或FormData对象作为参数')
  }
}

// 获取检测历史记录
export function getHistory(params) {
  return request({
    url: '/detect/history',
    method: 'get',
    params
  })
}

// 保存检测结果
export function saveResult(data) {
  return request({
    url: '/detect/save',
    method: 'post',
    data
  })
}

// 删除检测记录
export function deleteHistory(detectId) {
  return request({
    url: `/detect/${detectId}`,
    method: 'delete'
  })
}

// 导出检测API对象
export const detectApi = {
  detectImage,
  getHistory,
  saveResult,
  deleteHistory
}