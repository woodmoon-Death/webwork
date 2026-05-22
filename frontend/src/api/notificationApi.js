import http from './http'

export const notificationApi = {
  list() {
    return http.get('/notifications')
  },
  summary() {
    return http.get('/notifications/summary')
  },
  readAll() {
    return http.post('/notifications/read-all')
  }
}
