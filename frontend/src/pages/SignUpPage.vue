<template>
  <div class="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
        회원가입 </h2>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <form @submit.prevent="onSubmit" class="space-y-6">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700"> 이름 </label>
            <div class="mt-1">
              <input v-model="name" v-bind="nameProps" name="name" type="text" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700"> 이메일 </label>
            <div class="mt-1">
              <input id="email" v-model="email" v-bind="emailProps" name="email" type="email" autocomplete="email" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-700"> 비밀번호 </label>
            <div class="mt-1">
              <input id="password" v-model="password" v-bind="passwordProps" name="password" type="password" autocomplete="current-password" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>

          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700"> 비밀번호 확인 </label>
            <div class="mt-1">
              <input id="confirmPassword" v-model="confirmPassword" v-bind="confirmPasswordProps" name="confirmPassword" type="password" required class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            </div>
          </div>

          <div>
            <button type="submit" class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              회원가입
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
                또는 로그인
              </span>
            </div>
          </div>

          <div class="mt-6">
            <router-link to="/sign-in" class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
              로그인
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import * as yup from 'yup'
import { useForm } from 'vee-validate'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { h } from 'vue'
import { useStore } from 'vuex'

const signUpSchema = yup.object({
  name: yup.string().required('이름은 필수입니다.').min(2, '이름을 최소 2자 이상 입력하세요.').max(30, '이름은 최대 30자리까지 입력 가능합니다.'),
  email: yup.string().email('유효한 이메일을 입력하세요.').required('이메일은 필수입니다.'),
  password: yup.string().required('비밀번호는 필수입니다.'),
  confirmPassword: yup.string().oneOf([yup.ref('password')], '비밀번호가 일치하지 않습니다.').required('비밀번호 확인은 필수입니다.')
})


// Yup 스키마에서 타입 추론
type TSignUpFormData = yup.InferType<typeof signUpSchema>


const { handleSubmit, defineField } = useForm<TSignUpFormData>({
  validationSchema: signUpSchema
})
const [name, nameProps] = defineField('name')
const [email, emailProps] = defineField('email')
const [password, passwordProps] = defineField('password')
const [confirmPassword, confirmPasswordProps] = defineField('confirmPassword')

const router = useRouter()
const store = useStore()

const onSubmit = handleSubmit(async (values) => {
    try {
      await axios.post('/api/auth/sign-up', values)
      await router.push(`/sign-in?id=${values.email}`)
    } catch (e: any) {
      store.commit('setModal', {
        isOpen: true,
        content: h('p', e.response.data || '회원가입에 실패했습니다.')
      })
      throw e
    }
  },
  ({ errors }) => { // validation 실패 콜백함수
    const message = Object.values(errors)[0]
    store.commit('setModal', {
      isOpen: true,
      content: h('p', message)
    })
  }
)

</script>
