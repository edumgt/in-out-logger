<script setup lang="ts">
import { ref, watchEffect } from 'vue'

const isOpen = ref(false)
const toggle = () => {
  isOpen.value = !isOpen.value
}

const setScrollBarVisibility = () => {
  if (isOpen.value) {
    document.querySelector('html')!.style.overflow = 'hidden'
  } else {
    document.querySelector('html')!.style.overflow = 'auto'
  }
}
watchEffect(() => {
  setScrollBarVisibility()
})

</script>

<template>
  <div class="modal-wrap z-50" v-show="isOpen" @click="toggle">
    <div class="modal-container" @click.stop="">
      <button @click="toggle">Modal</button>
      <div class="modal-wrap" v-show="isOpen">
        <div class="modal-container">
          <div class="modal-btn">
            <button @click="toggle">닫기</button>
            <button @click="toggle">확인</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
