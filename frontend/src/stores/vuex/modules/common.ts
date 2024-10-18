import { Module } from 'vuex'
import { VuexModules } from '../index.ts'

export interface CommonModuleState {
  isLoading: boolean;
}

const commonModule: Module<CommonModuleState, VuexModules> = {
  state() {
    return {
      isLoading: false
    }
  },
  actions: {},
  mutations: {
    setIsLoading(state, payload) {
      state.isLoading = payload
    },
  },
  getters: {
    isLoading(state){
      return state.isLoading;
    }
  }
}

export default commonModule
