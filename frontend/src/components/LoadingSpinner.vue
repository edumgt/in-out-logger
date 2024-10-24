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
  <div v-if="isLoading">
    <!-- 배경 오버레이 추가 -->
    <div class="loading-overlay"></div>
    <div class="loading-spinner-wrap z-50">
      <img src="@/assets/vue-circle.png" width="100" height="100" class="loading-spinner" alt="Vue Spinner" />
      <span class="text-white text-center">Loading {{ '.'.repeat(counter) }}</span>
    </div>
  </div>
</template>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3); /* 반투명 검정 배경 */
  z-index: 49; /* 스피너보다 낮은 z-index */
}

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
  animation: spin 2s linear infinite;
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
