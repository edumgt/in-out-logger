<script setup lang="ts">
  import { useUserStore } from '@/stores/userStore.ts'
  import axios from 'axios'

  const { getUsername } = useUserStore()
  const checkAuth = async () => {
    const response = await axios.get('/api/auth/test')
    console.log(response)
  }

</script>

<template>
    <div class="flex flex-col">
      <router-link to="/">메인</router-link>
      <router-link to="/sign-up">회원가입</router-link>
      <router-link to="/sign-in">로그인</router-link>
      <span @click="checkAuth" class="cursor-pointer">로그인 체크</span>
      <div>{{ getUsername }}님 하이</div>
    </div>
</template>

<style scoped>

</style>
