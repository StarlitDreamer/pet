import request from '@/utils/request'

export function getCareRecords(orderId) {
  return request.get(`/care/list/${orderId}`)
}

export function addCareRecord(data) {
  return request.post('/care/add', data)
}

export function getCareDetail(id) {
  return request.get(`/care/detail/${id}`)
}
