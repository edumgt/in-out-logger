import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'), // '@'를 'src'로 설정
    },
  },
  server: {
    proxy: { // https://github.com/vitejs/vite/discussions/9285
      // 경로가 "/api" 로 시작하는 요청을 대상으로 proxy 설정
      '/api': {
        // 요청 전달 대상 서버 주소 설정
        target: 'http://localhost',
        // 요청 헤더 host 필드 값을 대상 서버의 호스트 이름으로  변경
        changeOrigin: true,
        // SSL 인증서 검증 무시
        secure: false,
        ws: true
      },
    },
  }
})
