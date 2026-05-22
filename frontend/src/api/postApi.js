import http from './http'

export const postApi = {
  list(params = {}) {
    return http.get('/posts', { params })
  },
  detail(id) {
    return http.get(`/posts/${id}`)
  },
  create(payload) {
    return http.post('/posts', payload)
  },
  update(id, payload) {
    return http.put(`/posts/${id}`, payload)
  },
  remove(id) {
    return http.delete(`/posts/${id}`)
  },
  visibility(id, visibility) {
    return http.patch(`/posts/${id}/visibility`, { visibility })
  },
  like(id) {
    return http.post(`/posts/${id}/like`)
  },
  unlike(id) {
    return http.delete(`/posts/${id}/like`)
  }
}
