import http from './http'

export const authApi = {
  register(payload) {
    return http.post('/auth/register', payload)
  },
  login(payload) {
    return http.post('/auth/login', payload)
  },
  logout() {
    return http.post('/auth/logout')
  },
  me() {
    return http.get('/auth/me')
  }
}
