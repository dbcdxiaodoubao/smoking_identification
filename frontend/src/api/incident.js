import request from '@/utils/request'

export function getIncidentList(params) {
  return request({
    url: '/incident',
    method: 'get',
    params
  })
}

export function addIncident(data) {
  return request({
    url: '/incident',
    method: 'post',
    data
  })
}

export function handleIncident(incidentId) {
  return request({
    url: `/incident/${incidentId}`,
    method: 'post'
  })
}

export function deleteIncident(incidentId) {
  return request({
    url: `/incident/${incidentId}`,
    method: 'delete'
  })
}

export function getIncidentDetail(incidentId) {
  return request({
    url: `/incident/dtl/${incidentId}`,
    method: 'get'
  })
}

export function getIncidentsByStudentId(studentId) {
  return request({
    url: '/incident/student',
    method: 'get',
    params: { studentId }
  })
}

export function bindStudent(incidentId, studentId) {
  return request({
    url: `/incident/${incidentId}/student/${studentId}`,
    method: 'put'
  })
}