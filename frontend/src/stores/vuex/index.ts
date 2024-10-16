import { createLogger, createStore } from 'vuex'
import authModule, { AuthModuleState } from './modules/auth.ts'
import createPersistedState from 'vuex-persistedstate';
export interface VuexModules {
  authModule: AuthModuleState;
}

const plugins = [createPersistedState({
  paths: ['authModule'] // persist 적용 module names
})]
if (import.meta.env.MODE !== 'production') {
  plugins.push(createLogger({ collapsed: false }))
}

export default createStore({
  modules: {
    authModule
  },
  plugins
})
