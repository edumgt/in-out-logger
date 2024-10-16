import {useStore} from "vuex";

export const isTokenValid = () => {
  const store = useStore()
  // const token = localStorage.getItem('token')
  return !!store.getters.getToken
}
