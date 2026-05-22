<template>
  <section class="messages-page">
    <aside class="panel message-sidebar">
      <div class="message-sidebar-head">
        <div>
          <p class="eyebrow">私信</p>
          <h2>会话列表</h2>
        </div>
      </div>
      <LoadingState v-if="loadingThreads" />
      <div v-else-if="displayThreads.length" class="message-thread-list">
        <button
          v-for="thread in displayThreads"
          :key="thread.userId"
          type="button"
          class="message-thread-item"
          :class="{ active: activeUserId === thread.userId }"
          @click="openThread(thread.userId)"
        >
          <div class="message-thread-meta">
            <div class="composer-avatar">
              <img v-if="thread.avatarUrl" :src="assetUrl(thread.avatarUrl)" :alt="`${thread.nickname} 的头像`" />
              <span v-else>{{ thread.nickname?.slice(0, 1) || 'U' }}</span>
            </div>
            <div class="message-thread-copy">
              <strong>{{ thread.nickname }}</strong>
              <span class="muted">{{ thread.lastMessage }}</span>
            </div>
          </div>
          <div class="message-thread-side">
            <span class="muted">{{ formatDate(thread.lastMessageAt, true) }}</span>
            <span v-if="thread.unreadCount" class="status-pill">{{ thread.unreadCount }}</span>
            <button class="icon-button thread-delete-button" type="button" title="删除会话" @click.stop="hideThread(thread.userId)">×</button>
          </div>
        </button>
      </div>
      <EmptyState v-else title="还没有私信" description="去别人的主页点“发私信”，就会在这里建立会话。" />
    </aside>

    <section class="feed-panel message-panel">
      <div class="message-panel-head">
        <div v-if="activeThread || partner">
          <p class="eyebrow">当前会话</p>
          <h2>{{ activeThread?.nickname || partner?.nickname }}</h2>
        </div>
        <div v-else>
          <p class="eyebrow">当前会话</p>
          <h2>选择一个联系人</h2>
        </div>
      </div>

      <LoadingState v-if="loadingConversation" />
      <template v-else-if="activeUserId">
        <div v-if="messages.length" class="message-bubble-list">
          <article v-for="message in messages" :key="message.id" class="message-bubble" :class="{ mine: message.mine }">
            <p>{{ message.content }}</p>
            <span class="muted">{{ formatDate(message.createdAt) }}</span>
          </article>
        </div>
        <EmptyState v-else title="还没有消息" description="发一条消息，开启这段对话。" />

        <form class="message-composer" @submit.prevent="sendMessage">
          <textarea v-model.trim="draft" rows="4" maxlength="500" placeholder="写点什么，发给对方..." />
          <div class="message-composer-foot">
            <span class="field-help">{{ draft.length }}/500</span>
            <button class="primary-button" type="submit" :disabled="sending || !draft">发送</button>
          </div>
        </form>
      </template>
      <EmptyState v-else title="请选择一个会话" description="从左边点开一位联系人，就可以开始私信。" />
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '../api/userApi'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import { messageApi } from '../api/messageApi'
import { useInboxStore } from '../stores/inboxStore'
import { assetUrl } from '../utils/assetUrl'

const route = useRoute()
const router = useRouter()
const inboxStore = useInboxStore()

const loadingThreads = ref(false)
const loadingConversation = ref(false)
const sending = ref(false)
const threads = computed(() => inboxStore.threads)
const messages = ref([])
const activeUserId = ref(null)
const draft = ref('')
const partner = ref(null)
const displayThreads = computed(() => {
  return threads.value
})
const activeThread = computed(() => displayThreads.value.find((item) => item.userId === activeUserId.value))

onMounted(async () => {
  await loadThreads()
  await syncUserFromQuery()
})

watch(() => route.query.user, async () => {
  await syncUserFromQuery()
})

async function loadThreads() {
  loadingThreads.value = true
  try {
    await inboxStore.fetchThreads()
  } finally {
    loadingThreads.value = false
  }
}

async function syncUserFromQuery() {
  const queryUser = Number(route.query.user)
  if (queryUser) {
    await openThread(queryUser)
  }
}

async function openThread(userId) {
  const targetId = Number(userId)
  activeUserId.value = targetId
  loadingConversation.value = true
  try {
    partner.value = threads.value.find((item) => item.userId === targetId) || await userApi.detail(targetId)
    try {
      messages.value = await messageApi.conversation(targetId)
    } catch {
      messages.value = []
    }
    await inboxStore.fetchThreads()
    await inboxStore.fetchSummary()
    router.replace({ query: { user: targetId } })
  } finally {
    loadingConversation.value = false
  }
}

async function sendMessage() {
  if (!draft.value || !activeUserId.value) return
  sending.value = true
  try {
    await messageApi.send({ receiverId: activeUserId.value, content: draft.value })
    draft.value = ''
    await openThread(activeUserId.value)
  } finally {
    sending.value = false
  }
}

async function hideThread(userId) {
  await messageApi.hideThread(userId)
  await loadThreads()
  if (activeUserId.value === userId) {
    activeUserId.value = null
    partner.value = null
    messages.value = []
    router.replace({ query: {} })
  }
}

function formatDate(value, short = false) {
  if (!value) return ''
  const date = new Date(value)
  return short ? date.toLocaleDateString('zh-CN') : date.toLocaleString('zh-CN', { hour12: false })
}
</script>
