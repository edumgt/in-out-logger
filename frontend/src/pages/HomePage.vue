<script setup lang="ts">
import useUserStore from '@/stores/userStore.ts'
import axios from '@/utils/axios.ts'
import { useRoute, useRouter } from 'vue-router'
import useTokenStore from '@/stores/tokenStore.ts'

const route = useRoute()
const { setUsername } = useUserStore()
const { setToken } = useTokenStore()
const { token, username } = route.query
if (token && username) {
  setToken(token)
  setUsername(username)
}
const { getUsername } = useUserStore()

const checkAuth = async () => {
  const response = await axios.get('/api/auth/test')
  console.log('checkAuth', response.status)
}
const router = useRouter()
const logout = () => {
  setUsername('')
  setToken('')
  router.push('/sign-in')
}

</script>

<template>
  <div class="flex flex-col">
    <router-link to="/">메인</router-link>
    <router-link to="/sign-up">회원가입</router-link>
    <router-link to="/sign-in">로그인</router-link>
    <a class="cursor-pointer" href="http://localhost/oauth2/authorization/kakao">카카오 로그인</a>
    <span @click="checkAuth" class="cursor-pointer">로그인 체크</span>
    <div v-if="getUsername">{{ getUsername }}님 하이</div>
    <div class="cursor-pointer" v-if="getUsername" @click="logout">로그아웃</div>
  </div>
</template>

<style scoped>

</style>
