import { defineStore } from 'pinia'

const useTokenStore = defineStore('token', {
  state: () => ({
    token: ''
  }),
  actions: {
    setToken(token: string) {
      this.token = token
    }
  },
  getters: {
    getToken(state){
      return state.token
    }
  },
  persist: true
})
export default useTokenStore
