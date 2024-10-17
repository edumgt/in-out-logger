<script setup lang="ts">
import { isTokenValid } from '@/utils/auth.ts'
import { useStore } from 'vuex'
import Modal from '@/components/Modal.vue'

const isValid = isTokenValid()
const store = useStore()
if (!isValid) {
  store.commit('setUsername', '')
}
</script>

<template>
  <Modal />
  <router-view />
</template>

