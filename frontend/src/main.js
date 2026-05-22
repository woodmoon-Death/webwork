import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/styles/base.css'
import './assets/styles/layout.css'
import './assets/styles/components.css'

createApp(App).use(createPinia()).use(router).mount('#app')
