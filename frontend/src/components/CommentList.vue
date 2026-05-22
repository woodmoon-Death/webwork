<template>
  <section class="comments">
    <h2>评论</h2>
    <CommentForm v-if="userStore.isLoggedIn" @submit="$emit('create', $event)" />
    <PermissionNotice v-else message="登录后可以发表评论。" />

    <div v-if="comments.length" class="comment-list">
      <article v-for="comment in comments" :key="comment.id" class="comment-item">
        <UserBadge :user-id="comment.userId" :name="comment.authorName" :avatar-url="comment.avatarUrl" />
        <p>{{ comment.content }}</p>
        <div class="comment-foot">
          <span class="muted">{{ formatDate(comment.createdAt) }}</span>
          <button v-if="canDelete(comment)" class="ghost-button danger-text" type="button" @click="$emit('delete', comment)">删除</button>
        </div>
      </article>
    </div>
    <EmptyState v-else title="还没有评论" description="成为第一个参与讨论的人。" />
  </section>
</template>

<script setup>
import { useUserStore } from '../stores/userStore'
import CommentForm from './CommentForm.vue'
import EmptyState from './EmptyState.vue'
import PermissionNotice from './PermissionNotice.vue'
import UserBadge from './UserBadge.vue'

defineProps({
  comments: { type: Array, default: () => [] }
})
defineEmits(['create', 'delete'])

const userStore = useUserStore()

function canDelete(comment) {
  return userStore.isAdmin || userStore.user?.id === comment.userId
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : ''
}
</script>
