import request from '@/utils/request'

export function getMessages(params) {
  return request.get('/message/list', { params })
}

export function getUnreadCount() {
  return request.get('/message/unread/count')
}

export function markRead(id) {
  return request.put(`/message/read/${id}`)
}

export function markAllRead() {
  return request.put('/message/read/all')
}
