<template>
  <header class="topbar">
    <RouterLink class="brand" to="/">
      <span class="brand-mark" aria-hidden="true">
        <svg viewBox="0 0 48 48" role="img">
          <path d="M13 12h16c7 0 12 4.7 12 10.9S36 33.8 29 33.8h-4.7L16.7 39v-5.2H13c-7 0-12-4.7-12-10.9S6 12 13 12Z" />
          <path class="brand-heart" d="M23.8 28.5c-4.2-2.8-6.5-5.1-6.5-8 0-2 1.4-3.5 3.3-3.5 1.2 0 2.4.6 3.2 1.7.8-1.1 2-1.7 3.2-1.7 1.9 0 3.3 1.5 3.3 3.5 0 2.9-2.3 5.2-6.5 8Z" />
        </svg>
      </span>
      <span>心语栈</span>
    </RouterLink>

    <nav class="nav-links" aria-label="主导航">
      <RouterLink to="/">信息流</RouterLink>
      <RouterLink :to="publishLink">发布</RouterLink>
      <RouterLink :to="profileLink">我的主页</RouterLink>
      <RouterLink v-if="userStore.isLoggedIn" class="nav-link-badge" to="/notifications">
        通知
        <span v-if="inboxStore.unreadNotifications" class="nav-count">{{ inboxStore.unreadNotifications }}</span>
      </RouterLink>
      <RouterLink v-if="userStore.isLoggedIn" class="nav-link-badge" to="/messages">
        私信
        <span v-if="inboxStore.unreadMessages" class="nav-count">{{ inboxStore.unreadMessages }}</span>
      </RouterLink>
      <RouterLink v-if="userStore.isAdmin" to="/admin">管理</RouterLink>
    </nav>

    <div class="account-actions">
      <template v-if="userStore.isLoggedIn">
        <span class="user-chip">
          {{ userStore.user.nickname }}
          <span v-if="inboxStore.totalUnread" class="user-chip-count">{{ inboxStore.totalUnread }}</span>
        </span>
        <button class="ghost-button" type="button" @click="logout">退出</button>
      </template>
      <template v-else>
        <RouterLink class="ghost-button" to="/login">登录</RouterLink>
        <RouterLink class="primary-button compact" to="/register">注册</RouterLink>
      </template>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useInboxStore } from '../stores/inboxStore'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const inboxStore = useInboxStore()
const publishLink = computed(() => (userStore.isLoggedIn ? '/publish' : { name: 'login', query: { redirect: '/publish' } }))
const profileLink = computed(() => (
  userStore.user ? `/users/${userStore.user.id}` : { name: 'login', query: { redirect: '/' } }
))

function logout() {
  userStore.logout()
  router.push('/')
}
</script>
