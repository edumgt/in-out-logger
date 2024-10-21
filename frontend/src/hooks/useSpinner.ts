import { useStore } from 'vuex'


const useSpinner = (targetFunction: () => Promise<any>) => {
  const store = useStore()
  return async () => {
    let result
    store.commit('setIsLoading', true)
    try {
      result = await targetFunction()
    } catch (e) {
      console.error(e)
      throw e
    } finally {
      store.commit('setIsLoading', false)
    }
    return result
  }
}

export default useSpinner;
