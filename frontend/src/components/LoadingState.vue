<template>
  <div class="loading-state" aria-live="polite">
    <span class="loader" aria-hidden="true"></span>
    <span>{{ text }}</span>
  </div>
</template>

<script setup>
defineProps({
  text: { type: String, default: '加载中...' }
})
</script>
