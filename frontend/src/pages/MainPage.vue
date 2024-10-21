<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utils/axios'
import { useStore } from 'vuex'
import useProgress from '@/hooks/useProgress.ts'

const route = useRoute()
const router = useRouter()
const store = useStore()
const { token: queryToken, name: queryUsername } = route.query
if (queryToken && queryUsername) {
  store.commit('setToken', queryToken)
  store.commit('setUsername', queryUsername)
}

const isLoading = ref(false)
const authStatus = ref('')

const isLoggedIn = computed(() => !!store.getters.getUsername)

const checkAuth = async () => {
  isLoading.value = true
  try {
    const response = await axios.get('/api/auth/test')
    authStatus.value = `인증 성공 (상태 코드: ${response.status})`
  } catch (e) {
    authStatus.value = '인증 실패'
    throw e
  } finally {
    isLoading.value = false
  }
}

const username = computed(() => store.getters.getUsername)

const logout = () => {
  store.commit('setUsername', '')
  store.commit('setToken', '')
  router.push('/sign-in')
}
const checkAuthProgress = useProgress(checkAuth)
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-800">In Out Logger</h1>

      <div class="space-y-4">
        <router-link to="/sign-up" class="block w-full btn cyan small">회원가입</router-link>
        <router-link to="/calendar" class="block w-full btn blue small">캘린더</router-link>

        <button @click="checkAuthProgress" class="block w-full btn yellow small" :disabled="isLoading">
          {{ isLoading ? '확인 중...' : '인증' }}
        </button>

        <p v-if="authStatus" class="text-center text-sm" :class="{'text-green-600': authStatus.includes('성공'), 'text-red-600': authStatus.includes('실패')}">
          {{ authStatus }} </p>
        <div v-if="isLoggedIn" class="mt-6">
          <p class="text-center text-lg font-semibold text-gray-800 mb-4">
            {{ username }}님 안녕하세요! </p>
        </div>
        <button v-if="!!username" @click="logout" class="block w-full btn red small">
          로그아웃
        </button>
        <router-link v-else to="/sign-in" class="block w-full btn green small">로그인</router-link>
      </div>



    </div>
  </div>
</template>
