import http from './http'

export const fileApi = {
  upload(file) {
    const form = new FormData()
    form.append('file', file)
    return http.post('/files/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
