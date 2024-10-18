<script setup lang="ts">
import { useStore } from 'vuex'
import { computed, nextTick, ref, watch } from 'vue'

const store = useStore()
const isModalOpen = computed(() => store.getters.isInputModalOpen)
const handleClose = computed(() => store.getters.handleModalClose)
const handleConfirm = computed(() => store.getters.handleModalConfirm)
const inputValue = computed(() => store.getters.getInputValue)

const updateInputState = (e: any) => {
  // 단방향 바인딩하고 react처럼 state 변경하는 식으로 처리
  store.commit('setInputValue', e.target.value);
}

const inputRef = ref()

watch([isModalOpen],async () => {
  if(isModalOpen.value && inputRef.value) {
    await nextTick()
    inputRef.value.focus()
  }
})

</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center overflow-auto bg-black bg-opacity-50" v-show="isModalOpen" @click="handleClose">
    <div class="relative bg-white rounded-lg shadow-xl p-6 m-4 max-w-xl w-full" @click.stop="">
      <div v-show="isModalOpen" class="mt-4">
        <component :is="store.getters.getContent" class="text-lg mb-4"></component>
        <input
          ref="inputRef"
          :value="inputValue"
          @input="updateInputState"
          @keyup.enter="handleConfirm"
          type="text"
          class="w-full px-3 py-2 text-gray-700 border rounded-lg focus:outline-none focus:border-blue-500 mb-4"
          :placeholder="store.getters.getPlaceholder"
        />
        <div class="flex justify-end space-x-4">
          <button class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition duration-300" @click="handleClose">닫기</button>
          <button class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition duration-300" @click="handleConfirm">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
