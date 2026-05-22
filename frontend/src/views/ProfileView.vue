<template>
  <section class="profile-page">
    <div v-if="profile" class="panel profile-card profile-card-refined">
      <div class="profile-avatar profile-avatar-large">
        <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="`${profile.nickname} 的头像`" />
        <span v-else>{{ profile.nickname.slice(0, 1) }}</span>
      </div>
      <div class="profile-card-main">
        <div class="profile-card-topline">
          <div>
            <p class="eyebrow">个人主页</p>
            <h1>{{ profile.nickname }}</h1>
            <p class="muted">@{{ profile.username }} · {{ profile.role }}</p>
          </div>
          <button v-if="isMe" class="ghost-button" type="button" @click="editing = !editing">
            {{ editing ? '收起编辑' : '编辑资料' }}
          </button>
        </div>
        <p class="profile-bio">{{ profile.bio || '这个用户还没有填写简介。' }}</p>
        <div class="profile-stats" aria-label="个人主页统计">
          <div>
            <strong>{{ posts.length }}</strong>
            <span>发布</span>
          </div>
          <div>
            <strong>{{ profile.receivedLikeCount || 0 }}</strong>
            <span>获赞</span>
          </div>
          <div>
            <strong>{{ imagePostCount }}</strong>
            <span>图片分享</span>
          </div>
        </div>
        <RouterLink v-if="userStore.isLoggedIn && !isMe" class="primary-button compact" :to="`/messages?user=${profile.id}`">发私信</RouterLink>
      </div>
    </div>

    <p v-if="success" class="notice success-notice">{{ success }}</p>

    <form v-if="editing" class="panel form-grid" @submit.prevent="saveProfile">
      <h2>编辑个人信息</h2>
      <label>
        昵称
        <input v-model.trim="profileForm.nickname" maxlength="30" />
      </label>
      <label>
        简介
        <textarea v-model.trim="profileForm.bio" maxlength="255" rows="4"></textarea>
      </label>
      <ImageUploader
        label="上传头像"
        help-text="支持 jpg、png、webp，最大 5MB。建议使用正方形图片。"
        preview-alt="头像预览"
        preview-class="avatar-preview"
        :preview-url="profileForm.avatarUrl"
        @uploaded="profileForm.avatarUrl = $event"
      />
      <p v-if="profileForm.avatarUrl" class="field-help">头像已上传，保存资料后生效。</p>
      <p v-if="error" class="form-error">{{ error }}</p>
      <div class="form-actions">
        <button class="primary-button" type="submit">保存资料</button>
        <button class="ghost-button" type="button" @click="resetProfileForm">重置</button>
      </div>
    </form>

    <section class="feed-panel">
      <div class="profile-content-head">
        <h2>发布内容</h2>
        <div class="segmented" aria-label="筛选个人内容">
          <button v-for="item in profileFilters" :key="item.value" type="button" :class="{ active: profileFilter === item.value }" @click="profileFilter = item.value">
            {{ item.label }}
          </button>
        </div>
      </div>
      <LoadingState v-if="loading" />
      <div v-else-if="filteredPosts.length" class="post-list">
        <PostCard v-for="post in filteredPosts" :key="post.id" :post="post" @toggle-like="toggleLike" />
      </div>
      <EmptyState v-else title="暂无发布" description="这里还没有符合当前筛选条件的分享。" />
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { postApi } from '../api/postApi'
import { userApi } from '../api/userApi'
import { useUserStore } from '../stores/userStore'
import EmptyState from '../components/EmptyState.vue'
import ImageUploader from '../components/ImageUploader.vue'
import LoadingState from '../components/LoadingState.vue'
import PostCard from '../components/PostCard.vue'

const route = useRoute()
const userStore = useUserStore()
const profile = ref(null)
const posts = ref([])
const loading = ref(false)
const editing = ref(false)
const isMe = ref(false)
const error = ref('')
const success = ref('')
const profileFilter = ref('all')
const profileForm = ref({ nickname: '', bio: '', avatarUrl: '' })
const profileFilters = [
  { label: '全部', value: 'all' },
  { label: '图片', value: 'image' },
  { label: '热门', value: 'hot' }
]

const filteredPosts = computed(() => {
  if (profileFilter.value === 'image') {
    return posts.value.filter((post) => Boolean(post.imageUrl))
  }
  if (profileFilter.value === 'hot') {
    return [...posts.value].sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  }
  return posts.value
})
const imagePostCount = computed(() => posts.value.filter((post) => Boolean(post.imageUrl)).length)

onMounted(load)
watch(() => route.params.id, load)

async function load() {
  loading.value = true
  error.value = ''
  success.value = ''
  try {
    profile.value = await userApi.detail(route.params.id)
    isMe.value = userStore.user?.id === Number(route.params.id)
    resetProfileForm()
    posts.value = await userApi.posts(route.params.id)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function toggleLike(post) {
  if (post.likedByMe) await postApi.unlike(post.id)
  else await postApi.like(post.id)
  await load()
}

async function saveProfile() {
  error.value = ''
  success.value = ''
  if (!profileForm.value.nickname) {
    error.value = '昵称不能为空'
    return
  }
  try {
    profile.value = await userApi.updateMe(profileForm.value)
    editing.value = false
    await userStore.fetchMe(true)
    isMe.value = userStore.user?.id === Number(route.params.id)
    resetProfileForm()
    success.value = '资料已更新，头像和信息已经同步。'
  } catch (err) {
    error.value = err.message
  }
}

function resetProfileForm() {
  if (!profile.value) return
  profileForm.value = {
    nickname: profile.value.nickname || '',
    bio: profile.value.bio || '',
    avatarUrl: profile.value.avatarUrl || ''
  }
}
</script>
