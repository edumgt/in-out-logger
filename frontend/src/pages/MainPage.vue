<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'

const route = useRoute()
const router = useRouter()
const store = useStore()
const { token: queryToken, name: queryUsername, email: queryEmail } = route.query ?? {}
if (queryToken && queryUsername && queryEmail) {
  store.commit('setToken', queryToken)
  store.commit('setUsername', queryUsername)
  store.commit('setEmail', queryEmail)
  router.replace('/')
}

const username = computed(() => store.getters.getUsername)
const logout = () => {
  store.commit('logout')
  router.replace('/')
}


</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-800">In Out Logger</h1>
      <div v-if="username" class="mt-6">
        <p class="text-center text-lg font-semibold text-gray-800 mb-4">
          {{ username }}님 안녕하세요! </p>
      </div>
      <div class="space-y-4">
        <router-link to="/calendar" class="block w-full btn blue small">캘린더</router-link>
        <button v-if="username" @click="logout" class="block w-full btn yellow small">
          로그아웃
        </button>
        <router-link v-else to="/sign-in" class="block w-full btn green small">로그인</router-link>
      </div>
    </div>
  </div>
</template>
