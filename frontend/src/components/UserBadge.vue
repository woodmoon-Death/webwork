<template>
  <RouterLink class="user-badge" :to="`/users/${userId}`">
    <img v-if="avatarUrl" :src="assetUrl(avatarUrl)" :alt="`${name} 的头像`" />
    <span v-else class="avatar-fallback">{{ name?.slice(0, 1) || 'U' }}</span>
    <span>{{ name }}</span>
  </RouterLink>
</template>

<script setup>
import { assetUrl } from '../utils/assetUrl'

defineProps({
  userId: { type: [Number, String], required: true },
  name: { type: String, required: true },
  avatarUrl: { type: String, default: '' }
})
</script>
