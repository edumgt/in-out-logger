<script setup lang="ts">
import { isTokenValid } from '@/utils/auth.ts'
import { useStore } from 'vuex'

const isValid = isTokenValid()
const store = useStore()
if (!isValid) {
  store.commit('setUsername', '')
}
</script>

<template>
  <router-view />
</template>

