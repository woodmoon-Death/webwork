<template>
  <div class="notice">
    <strong>{{ title }}</strong>
    <span>{{ message }}</span>
    <RouterLink v-if="to" :to="to">{{ actionText }}</RouterLink>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '需要权限' },
  message: { type: String, default: '请登录后继续操作。' },
  to: { type: String, default: '/login' },
  actionText: { type: String, default: '去登录' }
})
</script>
