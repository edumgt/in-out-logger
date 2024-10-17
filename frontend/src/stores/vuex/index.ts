import { createLogger, createStore } from 'vuex'
import authModule, { AuthModuleState } from './modules/auth.ts'
import createPersistedState from 'vuex-persistedstate';
import modalModule ,{ ModalModuleState } from './modules/modal.ts'
export interface VuexModules {
  authModule: AuthModuleState;
  modalModule: ModalModuleState;
}

const plugins = [createPersistedState({
  paths: ['authModule'] // persist 적용 module names
})]
if (import.meta.env.MODE !== 'production') {
  plugins.push(createLogger({ collapsed: false }))
}

export default createStore({
  modules: {
    authModule,
    modalModule
  },
  plugins
})
