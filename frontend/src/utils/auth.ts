export const isTokenValid = () => {
  const token = localStorage.getItem('$at')
  if(!token) {
    return false
  }
  const tokenExpiresIn = localStorage.getItem('$exp')
  if(!tokenExpiresIn){
    return false
  }
  const now = new Date()
  return now.getTime() < Number(tokenExpiresIn)
}
