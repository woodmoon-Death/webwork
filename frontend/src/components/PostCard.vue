<template>
  <article class="post-card post-card-refined">
    <header class="post-card-head">
      <div class="post-meta">
        <UserBadge :user-id="post.userId" :name="post.authorName" :avatar-url="post.avatarUrl" />
        <span class="muted post-origin">
          <span>{{ formatDate(post.createdAt) }}</span>
          <span>IP属地 {{ post.ipLocation || '未知' }}</span>
        </span>
      </div>
      <span v-if="post.visibility === 'HIDDEN'" class="status-pill danger">已隐藏</span>
    </header>

    <RouterLink class="post-title" :to="`/posts/${post.id}`">{{ post.title }}</RouterLink>
    <div v-if="post.tags?.length" class="post-tags" aria-label="帖子标签">
      <span v-for="tag in post.tags" :key="tag"># {{ tag }}</span>
    </div>
    <p class="post-content" :class="{ expanded: expandContent }">{{ post.content }}</p>

    <button
      v-if="post.imageUrl"
      type="button"
      class="post-image-frame"
      @click="previewOpen = true"
      :aria-label="`查看《${post.title}》的大图`"
    >
      <img class="post-image" :src="assetUrl(post.imageUrl)" :alt="post.title" />
      <span class="post-image-hint">查看大图</span>
    </button>

    <footer class="post-actions">
      <LikeButton :liked="post.likedByMe" :count="post.likeCount" @toggle="$emit('toggle-like', post)" />
      <RouterLink class="icon-link" :to="`/posts/${post.id}`">评论 {{ post.commentCount }}</RouterLink>
      <button v-if="canDelete" class="ghost-button danger-text" type="button" @click="$emit('delete', post)">删除</button>
      <button v-if="isAdmin" class="ghost-button" type="button" @click="$emit('visibility', post)">
        {{ post.visibility === 'HIDDEN' ? '恢复公开' : '隐藏' }}
      </button>
    </footer>

    <teleport to="body">
      <div v-if="previewOpen" class="image-lightbox" @click.self="previewOpen = false">
        <button type="button" class="image-lightbox-close" @click="previewOpen = false">关闭</button>
        <img class="image-lightbox-image" :src="assetUrl(post.imageUrl)" :alt="post.title" />
      </div>
    </teleport>
  </article>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useUserStore } from '../stores/userStore'
import LikeButton from './LikeButton.vue'
import UserBadge from './UserBadge.vue'
import { assetUrl } from '../utils/assetUrl'

const props = defineProps({
  post: { type: Object, required: true },
  expandContent: { type: Boolean, default: false }
})

defineEmits(['toggle-like', 'delete', 'visibility'])

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const canDelete = computed(() => userStore.isAdmin || userStore.user?.id === props.post.userId)
const previewOpen = ref(false)

function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}
</script>
