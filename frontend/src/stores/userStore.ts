import { defineStore } from 'pinia'

const useUserStore = defineStore('user', {
  state: () => ({
    username: ''
  }),
  actions: {
    setUsername(username: string) {
      this.username = username
    }
  },
  getters: {
    getUsername(state) {
      return state.username
    }
  },
  persist: true
})

export default useUserStore
