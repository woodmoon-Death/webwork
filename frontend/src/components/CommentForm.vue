<template>
  <form class="comment-form" @submit.prevent="submit">
    <label class="sr-only" for="comment-content">发表评论</label>
    <textarea id="comment-content" ref="contentInput" v-model.trim="content" rows="3" maxlength="300" placeholder="写一条评论"></textarea>
    <div class="comment-tools">
      <EmojiPicker @select="insertEmoji" />
      <button class="primary-button compact" type="submit" :disabled="!content || submitting">
        {{ submitting ? '发送中...' : '评论' }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import EmojiPicker from './EmojiPicker.vue'

const emit = defineEmits(['submit'])
const content = ref('')
const submitting = ref(false)
const contentInput = ref(null)

async function insertEmoji(emoji) {
  const input = contentInput.value
  if (!input) {
    content.value += emoji
    return
  }
  const start = input.selectionStart
  const end = input.selectionEnd
  content.value = `${content.value.slice(0, start)}${emoji}${content.value.slice(end)}`
  await nextTick()
  input.focus()
  input.setSelectionRange(start + emoji.length, start + emoji.length)
}

async function submit() {
  if (!content.value) return
  submitting.value = true
  try {
    await emit('submit', content.value)
    content.value = ''
  } finally {
    submitting.value = false
  }
}
</script>
