<template>
  <section class="home-page">
    <div class="home-ambient home-ambient-left" aria-hidden="true">
      <button
        v-for="topic in ambientTopics"
        :key="topic.label"
        type="button"
        class="ambient-topic-chip"
        :class="{ active: postStore.filter === `tag:${topic.label}` }"
        :style="topicStyle(topic)"
        @click="filterTopic(topic.label)"
      >
        <span class="ambient-topic-hash">#</span>
        <span class="ambient-topic-text">{{ topic.label }}</span>
      </button>
    </div>

    <section class="home-hero">
      <div>
        <p class="eyebrow">Xinyu Stack Community</p>
        <h1>把今天值得记住的内容分享出来</h1>
        <p class="muted">每一次分享，都是把片刻经验交给他人；在回应与共鸣里，日常会沉淀成彼此可见的成长。</p>
      </div>
      <RouterLink v-if="userStore.isLoggedIn" class="primary-button" to="/publish">发布新分享</RouterLink>
      <RouterLink v-else class="primary-button" to="/login">登录后参与</RouterLink>
    </section>

    <section class="home-grid">
      <aside class="side-stack">
        <div class="side-panel">
          <h2>社区概览</h2>
          <div class="mini-stats">
            <div><strong>{{ postStore.posts.length }}</strong><span>当前分享</span></div>
            <div><strong>{{ totalLikes }}</strong><span>点赞</span></div>
            <div><strong>{{ totalComments }}</strong><span>评论</span></div>
          </div>
        </div>

        <div class="side-panel">
          <h2>热门话题</h2>
          <div class="topic-list">
            <button
              v-for="topic in topics"
              :key="topic"
              type="button"
              :class="{ active: postStore.filter === `tag:${topic}` }"
              @click="filterTopic(topic)"
            >
              # {{ topic }}
            </button>
          </div>
        </div>

        <div class="side-panel">
          <h2>当前权限</h2>
          <p v-if="userStore.isLoggedIn" class="muted">
            你正在以 <strong>{{ userStore.user.nickname }}</strong> 身份参与，可以发布、评论和点赞。
          </p>
          <PermissionNotice v-else message="游客只能浏览公开内容，登录后可以互动。" />
        </div>
      </aside>

      <section class="feed-panel">
        <div class="composer-card" v-if="userStore.isLoggedIn">
          <div class="composer-avatar">
            <img v-if="userStore.user.avatarUrl" :src="userStore.user.avatarUrl" :alt="`${userStore.user.nickname}的头像`" />
            <span v-else>{{ userStore.user.nickname.slice(0, 1) }}</span>
          </div>
          <RouterLink to="/publish" class="composer-input">分享一段文字或图片...</RouterLink>
          <RouterLink class="primary-button compact" to="/publish">发布</RouterLink>
        </div>

        <div class="feed-status-bar">
          <div>
            <p class="feed-status-label">当前内容流</p>
            <strong>{{ currentFeedLabel }}</strong>
          </div>
          <button v-if="postStore.filter !== 'all' || postStore.keyword" class="ghost-button" type="button" @click="resetFilters">清除筛选</button>
        </div>

        <div class="toolbar">
          <label class="search-box">
            搜索
            <input v-model.trim="postStore.keyword" type="search" placeholder="标题、正文或作者" @keyup.enter="postStore.fetchPosts" />
          </label>
          <div class="segmented" aria-label="筛选信息流">
            <button v-for="item in filters" :key="item.value" type="button" :class="{ active: postStore.filter === item.value }" @click="setFilter(item.value)">
              {{ item.label }}
            </button>
          </div>
          <button class="ghost-button" type="button" @click="postStore.fetchPosts">刷新</button>
        </div>

        <div v-if="postStore.error" class="form-error form-error-inline">
          <span>{{ postStore.error }}</span>
          <button class="ghost-button" type="button" @click="postStore.fetchPosts">重新加载</button>
        </div>
        <LoadingState v-if="postStore.loading" />
        <div v-else-if="postStore.posts.length" class="post-list">
          <PostCard
            v-for="post in postStore.posts"
            :key="post.id"
            :post="post"
            @toggle-like="toggleLike"
            @delete="deletePost"
            @visibility="toggleVisibility"
          />
        </div>
        <EmptyState v-else />
      </section>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePostStore } from '../stores/postStore'
import { useUserStore } from '../stores/userStore'
import { postApi } from '../api/postApi'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import PermissionNotice from '../components/PermissionNotice.vue'
import PostCard from '../components/PostCard.vue'

const postStore = usePostStore()
const userStore = useUserStore()

const filters = [
  { label: '全部', value: 'all' },
  { label: '图片', value: 'image' },
  { label: '热门', value: 'hot' },
  { label: '我的', value: 'mine' }
]
const topics = ['校园日常', '学习记录', '图片分享', '生活灵感', '项目展示']
const ambientTopics = [
  { label: '校园日常', top: 16, left: 0, delay: 0.0 },
  { label: '学习记录', top: 34, left: 24, delay: 0.8 },
  { label: '图片分享', top: 56, left: 8, delay: 1.6 },
  { label: '生活灵感', top: 78, left: 28, delay: 2.4 }
]

const totalLikes = computed(() => postStore.posts.reduce((sum, post) => sum + (post.likeCount || 0), 0))
const totalComments = computed(() => postStore.posts.reduce((sum, post) => sum + (post.commentCount || 0), 0))
const currentFeedLabel = computed(() => {
  if (postStore.filter?.startsWith('tag:')) {
    return `话题 ${postStore.filter.slice(4)}`
  }
  const selected = filters.find((item) => item.value === postStore.filter)
  if (postStore.keyword) {
    return `搜索“${postStore.keyword}”`
  }
  return selected ? selected.label : '全部分享'
})

onMounted(() => postStore.fetchPosts())

function setFilter(value) {
  postStore.filter = value
  postStore.fetchPosts()
}

function filterTopic(topic) {
  postStore.keyword = ''
  setFilter(`tag:${topic}`)
}

function resetFilters() {
  postStore.keyword = ''
  setFilter('all')
}

function topicStyle(topic) {
  return {
    top: `${topic.top}%`,
    left: `${topic.left}px`,
    animationDelay: `${topic.delay}s`
  }
}

async function toggleLike(post) {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再点赞')
    return
  }
  if (post.likedByMe) {
    await postApi.unlike(post.id)
  } else {
    await postApi.like(post.id)
  }
  await postStore.fetchPosts()
}

async function deletePost(post) {
  if (!confirm(`确认删除《${post.title}》吗？`)) return
  await postApi.remove(post.id)
  await postStore.fetchPosts()
}

async function toggleVisibility(post) {
  const visibility = post.visibility === 'HIDDEN' ? 'PUBLIC' : 'HIDDEN'
  await postApi.visibility(post.id, visibility)
  await postStore.fetchPosts()
}
</script>
