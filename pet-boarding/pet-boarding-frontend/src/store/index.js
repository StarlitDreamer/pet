import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    token: localStorage.getItem('token') || ''
  }),
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data?.token || res.token
      this.userInfo = res.data?.user || res.user || res.data
      localStorage.setItem('token', this.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return this.userInfo
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },
    async getUserInfo() {
      const res = await getUserInfoApi()
      this.userInfo = res.data || res
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return this.userInfo
    }
  }
})
