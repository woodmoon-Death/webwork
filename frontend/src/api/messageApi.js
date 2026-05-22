import http from './http'

export const messageApi = {
  threads() {
    return http.get('/messages/threads')
  },
  conversation(userId) {
    return http.get(`/messages/with/${userId}`)
  },
  send(payload) {
    return http.post('/messages', payload)
  },
  hideThread(userId) {
    return http.delete(`/messages/threads/${userId}`)
  }
}
