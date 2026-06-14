const USER_ID_KEY = 'X-User-Id'
const USER_INFO_KEY = 'user-info'

export function saveUserId(userId) {
  localStorage.setItem(USER_ID_KEY, userId)
}

export function getUserId() {
  return localStorage.getItem(USER_ID_KEY)
}

export function removeUserId() {
  localStorage.removeItem(USER_ID_KEY)
}

export function saveUser(user) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(user))
}

export function getUser() {
  const str = localStorage.getItem(USER_INFO_KEY)
  return str ? JSON.parse(str) : null
}
