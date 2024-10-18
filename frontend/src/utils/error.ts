import { AxiosError } from 'axios'

export const messageHandler = (e: AxiosError, defaultMessage: string) => {
  return typeof e.response?.data === 'string' && e.response.data || defaultMessage || '서버에서 에러가 발생했습니다.'
}

