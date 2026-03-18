import request from '@/utils/request'

const ORDER_STATUS_MAP = {
  pending_pay: 0,
  pending_accept: 1,
  accepted: 2,
  checked_in: 3,
  boarding: 4,
  checked_out: 5,
  completed: 6,
  cancelled: 7,
  rejected: 8
}

function normalizeOrderListParams(params = {}) {
  const normalized = { ...params }

  if (normalized.page != null && normalized.pageNum == null) {
    normalized.pageNum = normalized.page
  }
  if (normalized.size != null && normalized.pageSize == null) {
    normalized.pageSize = normalized.size
  }
  delete normalized.page
  delete normalized.size

  if (typeof normalized.status === 'string' && ORDER_STATUS_MAP[normalized.status] != null) {
    normalized.status = ORDER_STATUS_MAP[normalized.status]
  }

  return normalized
}

function normalizeRejectPayload(data = {}) {
  if (data.rejectReason != null || data.reason == null) {
    return data
  }
  return {
    ...data,
    rejectReason: data.reason
  }
}

export function createOrder(data) {
  return request.post('/order/create', data)
}

export function getOwnerOrders(params) {
  return request.get('/order/owner/list', { params: normalizeOrderListParams(params) })
}

export function getProviderOrders(params) {
  return request.get('/order/provider/list', { params: normalizeOrderListParams(params) })
}

export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
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
  return request.put(`/order/reject/${id}`, normalizeRejectPayload(data))
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
