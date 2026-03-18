import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/order/create', data)
}

export function getOwnerOrders(params) {
  return request.get('/order/owner/list', { params })
}

export function getProviderOrders(params) {
  return request.get('/order/provider/list', { params })
}

export function getOrderDetail(id) {
  return request.get(`/order/detail/${id}`)
}

export function payOrder(id) {
  return request.put(`/order/pay/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/order/cancel/${id}`)
}

export function acceptOrder(id) {
  return request.put(`/order/accept/${id}`)
}

export function rejectOrder(id, data) {
  return request.put(`/order/reject/${id}`, data)
}

export function checkinOrder(id) {
  return request.put(`/order/checkin/${id}`)
}

export function checkoutOrder(id) {
  return request.put(`/order/checkout/${id}`)
}

export function completeOrder(id) {
  return request.put(`/order/complete/${id}`)
}
