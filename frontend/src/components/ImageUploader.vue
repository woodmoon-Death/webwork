<template>
  <div class="uploader">
    <div class="uploader-head">
      <label :for="inputId">{{ label }}</label>
      <span v-if="statusText" class="field-help">{{ statusText }}</span>
    </div>
    <input ref="inputRef" :id="inputId" type="file" accept="image/png,image/jpeg,image/webp" @change="handleFile" />
    <p class="field-help">{{ helpText }}</p>

    <div v-if="displayPreview" class="uploader-preview-card">
      <img :class="['image-preview', previewClass]" :src="assetUrl(displayPreview)" :alt="previewAlt" />
      <div class="uploader-actions">
        <button class="ghost-button" type="button" @click="triggerReplace">重新选择</button>
        <button class="ghost-button danger-text" type="button" @click="clearImage">移除图片</button>
      </div>
    </div>

    <p v-if="error" class="form-error">{{ error }}</p>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { fileApi } from '../api/fileApi'
import { assetUrl } from '../utils/assetUrl'

const props = defineProps({
  label: { type: String, default: '分享图片' },
  helpText: { type: String, default: '支持 jpg、png、webp，最大 5MB。' },
  previewAlt: { type: String, default: '上传图片预览' },
  previewClass: { type: String, default: '' },
  previewUrl: { type: String, default: '' }
})

const emit = defineEmits(['uploaded'])
const preview = ref('')
const error = ref('')
const uploading = ref(false)
const inputRef = ref(null)
const inputId = `image-file-${Math.random().toString(36).slice(2, 8)}`
const displayPreview = computed(() => preview.value || props.previewUrl)
const statusText = computed(() => {
  if (uploading.value) return '图片上传中...'
  if (displayPreview.value) return '预览已更新'
  return ''
})

function triggerReplace() {
  inputRef.value?.click()
}

function clearImage() {
  preview.value = ''
  error.value = ''
  if (inputRef.value) inputRef.value.value = ''
  emit('uploaded', '')
}

async function handleFile(event) {
  const file = event.target.files?.[0]
  error.value = ''
  if (!file) return
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
    error.value = '仅支持 jpg、png、webp 图片'
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    error.value = '图片不能超过 5MB'
    return
  }
  preview.value = URL.createObjectURL(file)
  uploading.value = true
  try {
    const uploaded = await fileApi.upload(file)
    emit('uploaded', uploaded.url)
  } catch (err) {
    error.value = err.message
  } finally {
    uploading.value = false
  }
}
</script>
