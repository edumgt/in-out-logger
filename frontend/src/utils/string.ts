export const dateToNumber = (date: string) => {
  return Number(date.replace(/-/g,''))
}
