import { useNProgress } from '@vueuse/integrations/useNProgress'

type TFunction = (() => Promise<any>) | (() => Promise<any>)[]

export const useProgress = (targetFunction: TFunction) => {
  const { start, done, progress } = useNProgress()
  if (Array.isArray(targetFunction)) {
    return async () => {
      start()
      const percentage = Math.round(100 / targetFunction.length) / 100
      targetFunction.forEach((promise) => {
        promise().then(() => {
          progress.value = (progress.value as number) + percentage
        })
      })

      return await Promise.all(targetFunction).then((result) => {
        done()
        return result
      })
    }
  }
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
export const sleep = (second: number) => {
  return new Promise(resolve => setTimeout(resolve, second * 1000))
}
