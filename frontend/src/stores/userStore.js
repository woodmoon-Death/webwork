import { defineStore } from 'pinia'
import { authApi } from '../api/authApi'
import { useInboxStore } from './inboxStore'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    initialized: false
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.user),
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },
  actions: {
    async refreshInboxSummary() {
      const inboxStore = useInboxStore()
      try {
        await inboxStore.fetchSummary()
      } catch {
        inboxStore.unreadNotifications = 0
        inboxStore.unreadMessages = 0
      }
    },
    async fetchMe(force = false) {
      const token = localStorage.getItem('shareflow_token')
      if (!token) {
        this.user = null
        this.initialized = true
        useInboxStore().reset()
        return
      }
      if (!force && this.initialized && this.user) {
        return
      }
      try {
        this.user = await authApi.me()
        await this.refreshInboxSummary()
      } catch {
        localStorage.removeItem('shareflow_token')
        this.user = null
        useInboxStore().reset()
      } finally {
        this.initialized = true
      }
    },
    async login(payload) {
      const data = await authApi.login(payload)
      localStorage.setItem('shareflow_token', data.token)
      this.user = data.user
      this.initialized = true
      await this.refreshInboxSummary()
    },
    async register(payload) {
      await authApi.register(payload)
    },
    logout() {
      localStorage.removeItem('shareflow_token')
      this.user = null
      useInboxStore().reset()
    }
  }
})
