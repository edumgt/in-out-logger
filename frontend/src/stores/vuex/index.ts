import {createLogger, createStore} from 'vuex'
import authModule, {AuthModuleState} from "./modules/auth.ts";

export interface VuexModules {
    authModule: AuthModuleState;
}

export default createStore({
    modules: {
        authModule,
    },
    plugins: import.meta.env.MODE === 'production' ? [] : [createLogger({
        collapsed: false
    })]
})
