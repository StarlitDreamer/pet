import request from '@/utils/request'

function normalizeAttendanceListParams(params = {}) {
  const nextParams = { ...params }

  if (nextParams.date) {
    nextParams.startDate = nextParams.startDate || nextParams.date
    nextParams.endDate = nextParams.endDate || nextParams.date
    delete nextParams.date
  }

  return nextParams
}

export function getAttendanceList(params) {
  return request.get('/attendance/list', { params: normalizeAttendanceListParams(params) })
}

export function clockIn(data) {
  return request.post('/attendance/clockin', data)
}

export function clockOut(data) {
  return request.put('/attendance/clockout', data)
}
