import request from '@/utils/request'

export function getAttendanceList(params) {
  return request.get('/attendance/list', { params })
}

export function clockIn(data) {
  return request.post('/attendance/clockin', data)
}

export function clockOut(data) {
  return request.post('/attendance/clockout', data)
}
