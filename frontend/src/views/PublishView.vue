<template>
  <section class="narrow-page">
    <PostForm @submit="submit" />
    <p v-if="error" class="form-error">{{ error }}</p>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { postApi } from '../api/postApi'
import PostForm from '../components/PostForm.vue'

const router = useRouter()
const error = ref('')

async function submit(payload) {
  error.value = ''
  try {
    const post = await postApi.create(payload)
    router.push(`/posts/${post.id}`)
  } catch (err) {
    error.value = err.message
  }
}
</script>
