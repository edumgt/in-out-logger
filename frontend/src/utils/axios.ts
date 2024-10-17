import _axios from 'axios'
import router from '../router'

const axios = _axios.create({
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터
axios.interceptors.request.use(config => {
  const vuexModules = JSON.parse(localStorage.getItem('vuex') ?? "{}")
  config.headers.Authorization = `Bearer ${vuexModules.authModule?.token ?? ''}`
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
