// vite.config.js
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    base: "/bgm-investments-esg-frontend/",
    plugins: [react()],
    server: {
        port: 5173,
        open: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',            // backend
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '/bgm-investments-esg-backend/v1')
            }
        }
    }
})