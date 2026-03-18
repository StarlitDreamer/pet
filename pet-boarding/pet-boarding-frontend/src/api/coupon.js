import request from '@/utils/request'

export function getCoupons(params) {
  return request.get('/coupon/list', { params })
}

export function getShopCoupons(shopId) {
  return request.get(`/coupon/shop/${shopId}`)
}

export function addCoupon(data) {
  return request.post('/coupon/add', data)
}

export function receiveCoupon(id) {
  return request.post(`/coupon/receive/${id}`)
}

export function getMyCoupons(params) {
  return request.get('/coupon/my', { params })
}
