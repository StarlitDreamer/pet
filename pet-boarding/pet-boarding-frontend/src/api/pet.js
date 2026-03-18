import request from '@/utils/request'

export function getPetList() {
  return request.get('/pet/list')
}

export function addPet(data) {
  return request.post('/pet/add', data)
}

export function updatePet(data) {
  return request.put('/pet/update', data)
}

export function deletePet(id) {
  return request.delete(`/pet/${id}`)
}

export function getPetDetail(id) {
  return request.get(`/pet/${id}`)
}
