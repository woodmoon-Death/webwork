<template>
  <form class="panel form-grid" @submit.prevent="submit">
    <div class="section-heading">
      <h1>{{ title }}</h1>
      <p class="muted">分享文字、图片和此刻的想法。</p>
    </div>

    <label>
      标题
      <input v-model.trim="form.title" type="text" maxlength="80" placeholder="给这条分享起一个标题" />
    </label>
    <label>
      正文
      <textarea
        ref="contentInput"
        v-model.trim="form.content"
        rows="7"
        maxlength="1000"
        placeholder="写下你想分享的内容"
      ></textarea>
    </label>
    <div class="form-tools">
      <EmojiPicker @select="insertEmoji" />
      <span class="field-help">{{ form.content.length }}/1000</span>
    </div>
    <fieldset class="tag-selector">
      <legend>选择标签</legend>
      <div class="tag-options">
        <label v-for="tag in availableTags" :key="tag" :class="{ active: form.tags.includes(tag) }">
          <input
            v-model="form.tags"
            type="checkbox"
            :value="tag"
            :disabled="!form.tags.includes(tag) && form.tags.length >= 3"
          />
          # {{ tag }}
        </label>
      </div>
      <p class="field-help">最多选择 3 个标签，方便信息流分类浏览。</p>
    </fieldset>
    <ImageUploader :preview-url="form.imageUrl" @uploaded="form.imageUrl = $event" />
    <p v-if="error" class="form-error">{{ error }}</p>
    <button class="primary-button" type="submit" :disabled="submitting">
      {{ submitting ? '发布中...' : buttonText }}
    </button>
  </form>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import EmojiPicker from './EmojiPicker.vue'
import ImageUploader from './ImageUploader.vue'

const props = defineProps({
  title: { type: String, default: '发布分享' },
  buttonText: { type: String, default: '发布' },
  initial: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['submit'])
const form = reactive({
  title: props.initial.title || '',
  content: props.initial.content || '',
  imageUrl: props.initial.imageUrl || '',
  tags: props.initial.tags || []
})
const submitting = ref(false)
const error = ref('')
const contentInput = ref(null)
const availableTags = ['校园日常', '学习记录', '图片分享', '生活灵感', '项目展示']

async function insertEmoji(emoji) {
  const input = contentInput.value
  if (!input) {
    form.content += emoji
    return
  }
  const start = input.selectionStart
  const end = input.selectionEnd
  form.content = `${form.content.slice(0, start)}${emoji}${form.content.slice(end)}`
  await nextTick()
  input.focus()
  input.setSelectionRange(start + emoji.length, start + emoji.length)
}

async function submit() {
  error.value = ''
  if (form.title.length < 2) {
    error.value = '标题至少需要 2 个字符'
    return
  }
  if (!form.content) {
    error.value = '正文不能为空'
    return
  }
  submitting.value = true
  try {
    await emit('submit', { ...form, tags: [...form.tags] })
  } finally {
    submitting.value = false
  }
}
</script>
