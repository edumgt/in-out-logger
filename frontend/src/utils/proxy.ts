import { useNProgress } from '@vueuse/integrations/useNProgress'

type TFunction = (() => Promise<any>) | (() => any)

export const useProgress = async ($function: TFunction) => {
  const { start, done, progress } = useNProgress()
  let result
  start()
  try {
    progress.value = 0.5
    result = await $function()
  } catch (e){
    console.error(e)
    throw e
  } finally {
    done()
  }
  return result
}
