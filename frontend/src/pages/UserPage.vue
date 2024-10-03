<template>
    <div class="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-md">
            <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
                {{ isLogin ? 'Login' : 'Sign up' }}
            </h2>
        </div>

        <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                <form @submit.prevent="handleSubmit" class="space-y-6">
                    <div v-if="!isLogin">
                        <label for="username" class="block text-sm font-medium text-gray-700">
                            Username
                        </label>
                        <div class="mt-1">
                            <input id="username" v-model="form.username" name="username" type="text" required
                                   class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div>
                        <label for="email" class="block text-sm font-medium text-gray-700">
                            Email address
                        </label>
                        <div class="mt-1">
                            <input id="email" v-model="form.email" name="email" type="email" autocomplete="email" required
                                   class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div>
                        <label for="password" class="block text-sm font-medium text-gray-700">
                            Password
                        </label>
                        <div class="mt-1">
                            <input id="password" v-model="form.password" name="password" type="password" autocomplete="current-password" required
                                   class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div v-if="!isLogin">
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                            Confirm Password
                        </label>
                        <div class="mt-1">
                            <input id="confirmPassword" v-model="form.confirmPassword" name="confirmPassword" type="password" required
                                   class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div>
                        <button type="submit"
                                class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                            {{ isLogin ? 'Sign in' : 'Sign up' }}
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
                Or {{ isLogin ? 'sign up' : 'login' }}
              </span>
                        </div>
                    </div>

                    <div class="mt-6">
                        <button @click="toggleForm" type="button"
                                class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                            {{ isLogin ? 'Create an account' : 'Login to your account' }}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
interface FormData {
    username: string
    email: string
    password: string
    confirmPassword: string
}

const isLogin = ref<boolean>(true)
const form = reactive<FormData>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
})

const handleSubmit = () => {
    if (isLogin.value) {
        console.log('Login submitted', { email: form.email, password: form.password })
        // Implement login logic here
    } else {
        if (form.password !== form.confirmPassword) {
            alert('Passwords do not match')
            return
        }
        console.log('Signup submitted', form)
        // Implement signup logic here
    }
}

const toggleForm = () => {
    isLogin.value = !isLogin.value
    // Reset form data when switching between login and signup
    Object.keys(form).forEach(key => (form[key as keyof FormData] = ''))
}
</script>
