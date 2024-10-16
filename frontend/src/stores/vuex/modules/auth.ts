import { Module } from 'vuex'
import { VuexModules } from '../index.ts'

export interface AuthModuleState {
  token: string;
  username: string;
  email: string;
}

const authModule: Module<AuthModuleState, VuexModules> = {
  state() {
    return {
      email: '',
      username: '',
      token: ''
    }
  },
  actions: {},
  mutations: {
    setUsername(state, payload) {
      state.username = payload
    },
    setToken(state, payload) {
      state.token = payload
    },
    setEmail(state, payload) {
      state.email = payload
    }
  },
  getters: {
    getUsername(state) {
      return state.username
    },
    getToken(state) {
      return state.token
    },
    getEmail(state) {
      return state.email
    }
  }
}

export default authModule
