import request from '@/utils/request'

export function getServiceList(shopId) {
  return request.get('/service/list', {
    params: { shopId }
  })
}

export function addService(data) {
  return request.post('/service/add', data)
}

export function updateService(data) {
  return request.put('/service/update', data)
}

export function toggleServiceStatus(id) {
  return request.put(`/service/status/${id}`)
}
