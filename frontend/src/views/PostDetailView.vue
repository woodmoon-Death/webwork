<template>
  <section class="narrow-page">
    <LoadingState v-if="loading" />
    <template v-else-if="post">
      <PostCard :post="post" expand-content @toggle-like="toggleLike" @delete="deletePost" @visibility="toggleVisibility" />
      <CommentList :comments="comments" @create="createComment" @delete="deleteComment" />
    </template>
    <EmptyState v-else title="分享不存在" description="它可能已被删除或隐藏。" />
    <p v-if="error" class="form-error">{{ error }}</p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { commentApi } from '../api/commentApi'
import { postApi } from '../api/postApi'
import { useUserStore } from '../stores/userStore'
import CommentList from '../components/CommentList.vue'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import PostCard from '../components/PostCard.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const post = ref(null)
const comments = ref([])
const loading = ref(true)
const error = ref('')

onMounted(load)

async function load() {
  loading.value = true
  error.value = ''
  try {
    post.value = await postApi.detail(route.params.id)
    comments.value = await commentApi.list(route.params.id)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function toggleLike() {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再点赞')
    return
  }
  if (post.value.likedByMe) await postApi.unlike(post.value.id)
  else await postApi.like(post.value.id)
  await load()
}

async function createComment(content) {
  await commentApi.create(post.value.id, { content })
  await load()
}

async function deleteComment(comment) {
  if (!confirm('确认删除这条评论吗？')) return
  await commentApi.remove(comment.id)
  await load()
}

async function deletePost() {
  if (!confirm('确认删除这条分享吗？')) return
  await postApi.remove(post.value.id)
  router.push('/')
}

async function toggleVisibility() {
  const visibility = post.value.visibility === 'HIDDEN' ? 'PUBLIC' : 'HIDDEN'
  await postApi.visibility(post.value.id, visibility)
  await load()
}
</script>
