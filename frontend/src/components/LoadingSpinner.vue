<script setup lang="ts">
import { computed, ref, watchEffect } from 'vue'
import { useStore } from 'vuex'

const counter = ref(1)
const store = useStore()
let id: NodeJS.Timeout
const isLoading = computed(() => store.getters.isLoading)
watchEffect(() => {
  if (isLoading.value) {
    id = setInterval(() => {
      counter.value = counter.value % 3 + 1
    }, 100)
  } else {
    if (id) {
      clearInterval(id)
    }
  }
})
</script>

<template>
  <div v-if="isLoading" class="loading-spinner-wrap z-50">
    <img src="@/assets/vue-circle.png" width="100" height="100" class="loading-spinner" alt="Vue Spinner" />
    <span class="text-center">Loading {{ '.'.repeat(counter) }}</span>
  </div>
</template>

<style scoped>
.loading-spinner-wrap {
  left: 47%;
  top: 40%;
  position: absolute;
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
}

.loading-spinner {
  position: relative;
  animation: spin 2s linear infinite; /* 2초 동안 회전하며 무한 반복 */
  transform: translate(-50%, -50%);
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

</style>
