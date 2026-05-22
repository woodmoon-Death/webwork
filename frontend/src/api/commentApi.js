import http from './http'

export const commentApi = {
  list(postId) {
    return http.get(`/posts/${postId}/comments`)
  },
  create(postId, payload) {
    return http.post(`/posts/${postId}/comments`, payload)
  },
  remove(id) {
    return http.delete(`/comments/${id}`)
  }
}
