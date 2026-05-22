<template>
  <div class="empty-state">
    <p class="empty-title">{{ title }}</p>
    <p class="muted">{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '暂无内容' },
  description: { type: String, default: '换个筛选条件，或者发布第一条分享。' }
})
</script>
