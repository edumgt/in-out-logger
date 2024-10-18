<script setup lang="ts">
import { isTokenValid } from '@/utils/auth.ts'
import { useStore } from 'vuex'
import Modal from '@/components/Modal.vue'
import InputModal from '@/components/InputModal.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const isValid = isTokenValid()
const store = useStore()
if (!isValid) {
  store.commit('setUsername', '')
}



</script>

<template>
  <loading-spinner/>

  <modal />
  <input-modal />
  <router-view />

</template>

