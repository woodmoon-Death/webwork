import http from './http'

export const userApi = {
  detail(id) {
    return http.get(`/users/${id}`)
  },
  updateMe(payload) {
    return http.put('/users/me', payload)
  },
  posts(id) {
    return http.get(`/users/${id}/posts`)
  }
}
