<script setup lang="ts">
import { isTokenValid } from '@/utils/auth.ts'
import { useUserStore } from '@/stores/userStore.ts'

const isValid = isTokenValid()
const { setUsername } = useUserStore()
if (!isValid) {
  setUsername('')
}
</script>

<template>
  <router-view />
</template>

