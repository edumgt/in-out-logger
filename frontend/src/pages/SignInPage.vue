<script setup lang="ts">
import { h, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios.ts'
import { useStore } from 'vuex'
import useProgress from '@/hooks/useProgress.ts'
import { isAxiosError } from 'axios'
import { messageHandler } from '@/utils/error.ts'

const store = useStore()
const form = reactive({
  email: '',
  password: ''
})

const router = useRouter()

const handleSubmit = async () => {
  try {
    const response = await axios.post<Authentication>('/api/auth/sign-in', form)
    const { data } = response
    let token = response.headers.authorization
    if (!token.startsWith('Bearer')) {
      store.commit('setModal', {
        isOpen: true,
        content: () => h('p', '로그인에 실패했습니다.')
      })
      return
    }
    token = token.slice(7)
    store.commit('setToken', token)
    store.commit('setUsername', data.username)
    store.commit('setEmail', data.email)
    await router.push('/')
  } catch (e: any) {
    if (isAxiosError(e)) {
      store.commit('setModal', {
        isOpen: true,
        content: () => h('p', messageHandler(e, '로그인에 실패했습니다.'))
      })
    }
    throw e
  }
}
const handleSubmitProgress = useProgress(handleSubmit)
const handleGoogleAuth = () => {
  const url = import.meta.env.VITE_BACKEND_URL + '/oauth2/authorization/google'
  location.href = url
}
</script>
<template>
  <div class="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
<!--    <div class="sm:mx-auto sm:w-full sm:max-w-md">-->
<!--      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">-->
<!--        로그인 </h2>-->
<!--    </div>-->

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <form @submit.prevent="handleSubmitProgress" class="space-y-6">

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700"> 이메일 </label>
            <div class="mt-1">
              <input id="email" v-model="form.email" name="email" type="email" autocomplete="email" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-700"> 비밀번호 </label>
            <div class="mt-1">
              <input id="password" v-model="form.password" name="password" type="password" autocomplete="current-password" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>


          <div>
            <button type="submit" class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              로그인
            </button>
          </div>
        </form>

        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-300"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-gray-500">
                또는
              </span>
            </div>
          </div>

          <div class="mt-6 justify-center flex gap-3">
            <button @click="handleGoogleAuth">
              <img src="@/assets/web_light_rd_ctn@1x.png" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
