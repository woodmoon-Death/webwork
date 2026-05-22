<template>
  <section class="admin-page">
    <div class="stats-grid">
      <div class="stat-card"><span>用户</span><strong>{{ stats.userCount || 0 }}</strong></div>
      <div class="stat-card"><span>分享</span><strong>{{ stats.postCount || 0 }}</strong></div>
      <div class="stat-card"><span>评论</span><strong>{{ stats.commentCount || 0 }}</strong></div>
      <div class="stat-card"><span>点赞</span><strong>{{ stats.likeCount || 0 }}</strong></div>
      <div class="stat-card"><span>隐藏</span><strong>{{ stats.hiddenPostCount || 0 }}</strong></div>
    </div>

    <section class="feed-panel">
      <div class="section-heading">
        <h1>内容管理</h1>
        <p class="muted">删除违规内容，或切换公开状态。</p>
      </div>
      <LoadingState v-if="loading" />
      <div v-else class="post-list">
        <PostCard v-for="post in posts" :key="post.id" :post="post" @delete="deletePost" @visibility="toggleVisibility" />
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/adminApi'
import { postApi } from '../api/postApi'
import LoadingState from '../components/LoadingState.vue'
import PostCard from '../components/PostCard.vue'

const stats = ref({})
const posts = ref([])
const loading = ref(false)

onMounted(load)

async function load() {
  loading.value = true
  stats.value = await adminApi.stats()
  posts.value = await adminApi.posts()
  loading.value = false
}

async function deletePost(post) {
  if (!confirm(`确认删除「${post.title}」吗？`)) return
  await postApi.remove(post.id)
  await load()
}

async function toggleVisibility(post) {
  await postApi.visibility(post.id, post.visibility === 'HIDDEN' ? 'PUBLIC' : 'HIDDEN')
  await load()
}
</script>
