import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userId = ref(null)
  const username = ref('')
  const nickname = ref('')
  const token = ref('')

  function setUserInfo(user) {
    userId.value = user.userId
    username.value = user.username
    nickname.value = user.nickname
    token.value = user.token
  }

  function clearUserInfo() {
    userId.value = null
    username.value = ''
    nickname.value = ''
    token.value = ''
  }

  return {
    userId,
    username,
    nickname,
    token,
    setUserInfo,
    clearUserInfo
  }
})
