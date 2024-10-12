import useTokenStore from '../stores/tokenStore.ts'

export const isTokenValid = () => {
  const { getToken } = useTokenStore()
  // const token = localStorage.getItem('token')
  return !!getToken
}
