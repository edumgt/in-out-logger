import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    username: ''
  }),
  actions: {
    setUsername(username: string) {
      this.username = username
    }
  },
  getters: {
    getUsername(state){
      return state.username
    }
  },
  persist: true
})
