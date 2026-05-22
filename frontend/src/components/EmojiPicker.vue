<template>
  <div class="emoji-picker">
    <button class="ghost-button compact" type="button" @click="open = !open">
      表情
    </button>
    <div v-if="open" class="emoji-panel" role="listbox" aria-label="选择表情">
      <button v-for="emoji in emojis" :key="emoji" type="button" @click="choose(emoji)">
        {{ emoji }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['select'])
const open = ref(false)
const emojis = ['😊', '😂', '🥰', '👍', '👏', '✨', '🔥', '💡', '📌', '📷', '🌱', '🎯', '💬', '🤝', '❤️']

function choose(emoji) {
  emit('select', emoji)
  open.value = false
}
</script>
