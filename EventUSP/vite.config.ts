import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/proxy/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/proxy\/api/, '/api'),
        },
      },
    },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
});
