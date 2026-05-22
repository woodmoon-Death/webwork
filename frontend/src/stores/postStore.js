import { defineStore } from 'pinia'
import { postApi } from '../api/postApi'

export const usePostStore = defineStore('post', {
  state: () => ({
    posts: [],
    loading: false,
    error: '',
    keyword: '',
    filter: 'all'
  }),
  actions: {
    async fetchPosts() {
      this.loading = true
      this.error = ''
      try {
        this.posts = await postApi.list({ keyword: this.keyword, filter: this.filter })
      } catch (error) {
        this.error = error.message
      } finally {
        this.loading = false
      }
    }
  }
})
