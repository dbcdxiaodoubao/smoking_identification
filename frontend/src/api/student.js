import request from '@/utils/request'

export function getStudentList(params) {
  return request({
    url: '/student',
    method: 'get',
    params
  })
}

export function getStudentDetail(studentId) {
  return request({
    url: `/student/${studentId}`,
    method: 'get'
  })
}

export function addStudent(data) {
  return request({
    url: '/student',
    method: 'post',
    params: data
  })
}

export function uploadFace(studentId, file) {
  const formData = new FormData()
  formData.append('image', file)
  formData.append('studentId', studentId)

  return request({
    url: '/student/face',
    method: 'post',
    data: formData,
    params: { studentId }
  })
}

export function deleteStudent(studentId) {
  return request({
    url: `/student/${studentId}`,
    method: 'delete'
  })
}
