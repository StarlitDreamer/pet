import request from '@/utils/request'

export function addComplaint(data) {
  return request.post('/complaint/add', data)
}

export function getComplaints(params) {
  return request.get('/complaint/list', { params })
}

export function replyComplaint(id, data) {
  return request.put(`/complaint/reply/${id}`, data)
}

export function resolveComplaint(id) {
  return request.put(`/complaint/resolve/${id}`)
}
