import request from '@/utils/request'

export function addReview(data) {
  return request.post('/review/add', data)
}

export function getShopReviews(shopId, params) {
  return request.get(`/review/shop/${shopId}`, { params })
}

export function getOrderReview(orderId) {
  return request.get(`/review/order/${orderId}`)
}

export function replyReview(id, data) {
  return request.put(`/review/reply/${id}`, data)
}
