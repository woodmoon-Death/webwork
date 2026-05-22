<template>
  <button class="icon-button" :class="{ active: liked }" type="button" @click="$emit('toggle')">
    <svg aria-hidden="true" viewBox="0 0 24 24" class="button-icon">
      <path
        :fill="liked ? 'currentColor' : 'none'"
        stroke="currentColor"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M20.8 4.6c-1.7-1.7-4.5-1.7-6.2 0L12 7.2 9.4 4.6c-1.7-1.7-4.5-1.7-6.2 0s-1.7 4.5 0 6.2L12 19.6l8.8-8.8c1.7-1.7 1.7-4.5 0-6.2z"
      />
    </svg>
    <span>{{ count }}</span>
  </button>
</template>

<script setup>
defineProps({
  liked: { type: Boolean, default: false },
  count: { type: Number, default: 0 }
})
defineEmits(['toggle'])
</script>
