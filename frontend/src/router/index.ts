import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import SignInPage from '../pages/SignInPage.vue'
import MainPage from '../pages/MainPage.vue'
import CalendarPage from '../pages/CalendarPage.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: MainPage
  },
  {
    path: '/sign-in',
    name: 'signIn',
    component: SignInPage
  },
  {
    path: '/calendar',
    name: 'calendar',
    meta: {
      requiresAuth: true
    },
    component: CalendarPage
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, _from, next) => {
  const requiresAuth = to.meta?.requiresAuth
  if(requiresAuth){
    const vuexModules = JSON.parse(localStorage.getItem('vuex') ?? "{}")
    const token = vuexModules.authModule?.token
    if(!token){
      next('/sign-in')
    }
  }

  next()
})

export default router
