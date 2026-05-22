import { defineStore } from 'pinia'
import { messageApi } from '../api/messageApi'
import { notificationApi } from '../api/notificationApi'

export const useInboxStore = defineStore('inbox', {
  state: () => ({
    notifications: [],
    threads: [],
    unreadNotifications: 0,
    unreadMessages: 0
  }),
  getters: {
    totalUnread: (state) => state.unreadNotifications + state.unreadMessages
  },
  actions: {
    reset() {
      this.notifications = []
      this.threads = []
      this.unreadNotifications = 0
      this.unreadMessages = 0
    },
    async fetchSummary() {
      const summary = await notificationApi.summary()
      this.unreadNotifications = summary.unreadNotifications || 0
      this.unreadMessages = summary.unreadMessages || 0
    },
    async fetchNotifications() {
      this.notifications = (await notificationApi.list()).filter((item) => item.type !== 'MESSAGE')
      this.unreadNotifications = this.notifications.filter((item) => !item.read).length
      return this.notifications
    },
    async readAllNotifications() {
      await notificationApi.readAll()
      this.notifications = this.notifications.map((item) => ({ ...item, read: true }))
      this.unreadNotifications = 0
    },
    async fetchThreads() {
      this.threads = await messageApi.threads()
      this.unreadMessages = this.threads.reduce((sum, thread) => sum + (thread.unreadCount || 0), 0)
      return this.threads
    }
  }
})
