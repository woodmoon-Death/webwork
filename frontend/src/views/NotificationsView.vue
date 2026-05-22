<template>
  <section class="narrow-page notifications-page">
    <div class="panel notifications-head">
      <div>
        <p class="eyebrow">通知中心</p>
        <h1>互动提醒</h1>
        <p class="muted">这里会显示收到的点赞和评论。</p>
      </div>
      <button class="ghost-button" type="button" @click="markAllRead" :disabled="!unreadCount">全部设为已读</button>
    </div>

    <section class="feed-panel">
      <LoadingState v-if="loading" />
      <div v-else-if="notifications.length" class="notification-list">
        <article v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: !item.read }">
          <RouterLink class="user-badge" :to="`/users/${item.actorId}`">
            <img v-if="item.actorAvatarUrl" :src="item.actorAvatarUrl" :alt="`${item.actorName} 的头像`" />
            <span v-else class="avatar-fallback">{{ item.actorName?.slice(0, 1) || 'U' }}</span>
            <span>{{ item.actorName }}</span>
          </RouterLink>
          <div class="notification-content">
            <p>{{ item.content }}</p>
            <div class="notification-foot">
              <span class="muted">{{ formatDate(item.createdAt) }}</span>
              <RouterLink v-if="item.postId" class="text-link" :to="`/posts/${item.postId}`">查看内容</RouterLink>
            </div>
          </div>
        </article>
      </div>
      <EmptyState v-else title="暂无通知" description="有人点赞或评论你时，会出现在这里。" />
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import { useInboxStore } from '../stores/inboxStore'

const inboxStore = useInboxStore()
const loading = ref(false)
const notifications = computed(() => inboxStore.notifications)
const unreadCount = computed(() => inboxStore.unreadNotifications)

onMounted(load)

async function load() {
  loading.value = true
  try {
    await inboxStore.fetchNotifications()
  } finally {
    loading.value = false
  }
}

async function markAllRead() {
  await inboxStore.readAllNotifications()
}

function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}
</script>
