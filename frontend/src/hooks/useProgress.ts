import { useNProgress } from '@vueuse/integrations/useNProgress'

const useProgress = (targetFunction: () => Promise<any>) => {
  const { start, done, progress } = useNProgress()
  return async () => {
    let result
    start()
    try {
      progress.value = 0.5
      result = await targetFunction()
    } catch (e) {
      console.error(e)
      throw e
    } finally {
      done()
    }
    return result
  }
}
export default useProgress;
