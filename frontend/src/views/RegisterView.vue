<template>
  <section class="auth-page">
    <form class="auth-card" @submit.prevent="submit">
      <h1>创建账号</h1>
      <p class="muted">加入社区，开始分享你的内容。</p>
      <label>
        用户名
        <input v-model.trim="form.username" type="text" autocomplete="username" />
      </label>
      <label>
        昵称
        <input v-model.trim="form.nickname" type="text" />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" autocomplete="new-password" />
      </label>
      <label>
        确认密码
        <input v-model="form.confirmPassword" type="password" autocomplete="new-password" />
      </label>
      <p v-if="error" class="form-error">{{ error }}</p>
      <button class="primary-button" type="submit" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      <RouterLink class="text-link" to="/login">已有账号？去登录</RouterLink>
    </form>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })
const loading = ref(false)
const error = ref('')

async function submit() {
  error.value = ''
  if (form.username.length < 3) {
    error.value = '用户名至少需要 3 个字符'
    return
  }
  if (!form.nickname) {
    error.value = '昵称不能为空'
    return
  }
  if (form.password.length < 6) {
    error.value = '密码至少需要 6 个字符'
    return
  }
  if (form.password !== form.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }
  loading.value = true
  try {
    await userStore.register(form)
    router.push('/login')
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>
