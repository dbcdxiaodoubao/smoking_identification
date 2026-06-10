import request from '@/utils/request'

// 获取处理事件列表
export function getDisposeList(params) {
  return request({
    url: '/dispose',
    method: 'get',
    params
  })
}

// 获取处理事件详情
export function getDisposeDetail(disposeId) {
  return request({
    url: `/dispose/${disposeId}`,
    method: 'get'
  })
}

// 新增处理事件
export function addDispose(data) {
  return request({
    url: '/dispose',
    method: 'post',
    data
  })
}

// 更新处理事件
export function updateDispose(data) {
  return request({
    url: '/dispose',
    method: 'put',
    data
  })
}

// 删除处理事件
export function deleteDispose(disposeId) {
  return request({
    url: `/dispose/${disposeId}`,
    method: 'delete'
  })
}

// 导出处理API对象
export const disposeApi = {
  getDisposeList,
  getDisposeDetail,
  addDispose,
  updateDispose,
  deleteDispose
}