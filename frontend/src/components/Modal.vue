<script setup lang="ts">
import { computed, watchEffect } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const isModalOpen = computed(() => store.getters.isAlertModalOpen)

watchEffect(() => {
  const value = isModalOpen.value ? 'hidden' : 'auto'
  document.querySelector('html')!.style.overflow = value
})

const handleClose = computed(() => store.getters.handleModalClose)
const handleConfirm = computed(() => store.getters.handleModalConfirm)
const content = computed(() => store.getters.getContent)
const closeText = computed(() => store.getters.getCloseText)
const confirmText = computed(() => store.getters.getConfirmText)
const closeModalWithoutSideEffect = () => {
  store.commit('setModal', {
    isOpen: false
  })
}

</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center overflow-auto bg-black bg-opacity-50" v-show="isModalOpen" @click="closeModalWithoutSideEffect">
    <div class="relative bg-white rounded-lg shadow-xl p-6 m-4 w-auto min-w-[500px]" @click.stop="">
      <div v-show="isModalOpen" class="mt-4">
        <div class="w-auto flex  ">
          <component :is="content" :key="content" class="text-lg text-left mb-4"></component>
        </div>
        <div class="flex justify-end space-x-4">
          <button class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition duration-300" @click="handleClose">
            {{ closeText }}
          </button>
          <button class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition duration-300" @click="handleConfirm">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
