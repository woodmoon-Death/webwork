import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080'
    }
  },
  preview: {
    host: '0.0.0.0',
    port: 4173,
    allowedHosts: ['exquisite-playfulness-production-b983.up.railway.app']
  }
})
