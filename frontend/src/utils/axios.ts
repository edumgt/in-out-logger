import _axios from 'axios'
import router from '../router'
import { useStore } from 'vuex'

const axios = _axios.create({
  headers: {
    'Content-Type': 'application/json'
  }
})
// 요청 인터셉터
axios.interceptors.request.use(config => {
  const store = useStore()
  config.headers.Authorization = `Bearer ${store.getters.getToken}`
  return config
}, error => {
  return Promise.reject(error)
})
axios.interceptors.response.use(async (response) => {
    return response
  },
  async (error) => {
    if (error.response && error.response.status === 401) {
      await router.push('/sign-in')
    }
    return Promise.reject(error)
  }
)

export default axios
