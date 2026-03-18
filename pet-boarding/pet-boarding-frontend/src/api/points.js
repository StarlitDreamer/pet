import request from '@/utils/request'

export function getPointsRecords(params) {
  return request.get('/points/records', { params })
}

export function exchangePoints(data) {
  return request.post('/points/exchange', data)
}
