<script setup lang="ts">
import { useStore } from 'vuex'
import Modal from '@/components/Modal.vue'
import InputModal from '@/components/InputModal.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { onMounted, onUnmounted } from 'vue'

const store = useStore()
const handleKeyDown = (e: KeyboardEvent) => {
  if(e.key === 'Escape'){
    store.getters.handleModalClose()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})


</script>

<template>
  <loading-spinner/>

  <modal />
  <input-modal />
  <router-view />

</template>

