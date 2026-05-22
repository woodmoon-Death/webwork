import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import PublishView from '../views/PublishView.vue'
import PostDetailView from '../views/PostDetailView.vue'
import ProfileView from '../views/ProfileView.vue'
import AdminView from '../views/AdminView.vue'
import NotificationsView from '../views/NotificationsView.vue'
import MessagesView from '../views/MessagesView.vue'
import { useUserStore } from '../stores/userStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/publish', name: 'publish', component: PublishView, meta: { requiresAuth: true } },
    { path: '/posts/:id', name: 'post-detail', component: PostDetailView },
    { path: '/users/:id', name: 'profile', component: ProfileView },
    { path: '/notifications', name: 'notifications', component: NotificationsView, meta: { requiresAuth: true } },
    { path: '/messages', name: 'messages', component: MessagesView, meta: { requiresAuth: true } },
    { path: '/admin', name: 'admin', component: AdminView, meta: { requiresAuth: true, admin: true } }
  ]
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  if (!userStore.initialized) {
    await userStore.fetchMe()
  }
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.admin && !userStore.isAdmin) {
    return { name: 'home' }
  }
  return true
})

export default router
