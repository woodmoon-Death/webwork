import http from './http'

export const adminApi = {
  stats() {
    return http.get('/admin/stats')
  },
  posts() {
    return http.get('/admin/posts')
  }
}
