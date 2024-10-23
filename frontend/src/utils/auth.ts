import { useStore } from 'vuex'

const ADMIN_ALLOWED = new Set(['jenkis@jeit.co.kr', 'gm@jeit.co.kr', 'demd@jeit.co.kr'])
export const isAdmin = () => {
  const store = useStore()
  return ADMIN_ALLOWED.has(store.getters.getEmail)
}

export const jobLevels = [
  '인턴',
  '사원',
  '주임',
  '대리',
  '과장',
  '차장',
  '실장',
  '대표'
]
