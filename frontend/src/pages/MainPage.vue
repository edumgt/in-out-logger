<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import useUserStore from '@/stores/userStore'
import useTokenStore from '@/stores/tokenStore'
import axios from '@/utils/axios'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const tokenStore = useTokenStore()

const { username } = storeToRefs(userStore)

const { token: queryToken, username: queryUsername } = route.query
if (queryToken && queryUsername) {
  tokenStore.setToken(queryToken as string)
  userStore.setUsername(queryUsername as string)
}

const isLoading = ref(false)
const authStatus = ref('')

const isLoggedIn = computed(() => !!username.value)

const checkAuth = async () => {
  isLoading.value = true
  try {
    const response = await axios.get('/api/auth/test')
    authStatus.value = `인증 성공 (상태 코드: ${response.status})`
  } catch (error) {
    authStatus.value = '인증 실패'
  } finally {
    isLoading.value = false
  }
}

const logout = () => {
  userStore.setUsername('')
  tokenStore.setToken('')
  router.push('/sign-in')
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-800">인증 관리</h1>

      <div class="space-y-4">
        <router-link
          v-for="(link, index) in [
            { to: '/sign-up', text: '회원가입' },
            { to: '/sign-in', text: '로그인' }
          ]"
          :key="index"
          :to="link.to"
          class="block w-full text-center py-2 px-4 bg-blue-500 text-white rounded hover:bg-blue-600 transition duration-200"
        >
          {{ link.text }}
        </router-link>

        <a
          href="http://localhost/oauth2/authorization/kakao"
          class="block w-full text-center py-2 px-4 bg-yellow-500 text-black rounded hover:bg-yellow-600 transition duration-200"
        >
          카카오 로그인
        </a>

        <button
          @click="checkAuth"
          class="w-full py-2 px-4 bg-green-500 text-white rounded hover:bg-green-600 transition duration-200"
          :disabled="isLoading"
        >
          {{ isLoading ? '확인 중...' : '로그인 체크' }}
        </button>

        <p v-if="authStatus" class="text-center text-sm" :class="{'text-green-600': authStatus.includes('성공'), 'text-red-600': authStatus.includes('실패')}">
          {{ authStatus }}
        </p>
      </div>

      <div v-if="isLoggedIn" class="mt-6">
        <p class="text-center text-lg font-semibold text-gray-800 mb-4">
          {{ username }}님 안녕하세요!
        </p>
        <button
          @click="logout"
          class="w-full py-2 px-4 bg-red-500 text-white rounded hover:bg-red-600 transition duration-200"
        >
          로그아웃
        </button>
      </div>
    </div>
  </div>
</template>
