import request from '@/utils/request'

export function getEmployees(params) {
  return request.get('/employee/list', { params })
}

export function addEmployee(data) {
  return request.post('/employee/add', data)
}

export function updateEmployee(data) {
  return request.put('/employee/update', data)
}

export function deleteEmployee(id) {
  return request.delete(`/employee/${id}`)
}
