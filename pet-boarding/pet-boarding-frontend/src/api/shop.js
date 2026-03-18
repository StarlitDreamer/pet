import request from '@/utils/request'

export function getShopList(params) {
  return request.get('/shop/list', { params })
}

export function getShopDetail(id) {
  return request.get(`/shop/detail/${id}`)
}

export function addShop(data) {
  return request.post('/shop/add', data)
}

export function updateShop(data) {
  return request.put('/shop/update', data)
}

export function getMyShop() {
  return request.get('/shop/my')
}
