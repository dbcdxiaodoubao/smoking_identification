import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => {
    const token = localStorage.getItem('token') || ''
    return {
      token: change-me
      userInfo: {},
      roles: []
    }
  },
  
  getters: {
    isLoggedIn: (state) => {
      const hasToken = !!state.token
      return hasToken
    },
    username: (state) => state.userInfo.userName || ''
  },
  
  actions: {
    // 登录
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        
        
        // 检查响应格式并提取token
        if (response.code === 200) {
          // 尝试从不同位置获取token
          let token = null
          
          // 情况1: token直接在data中
          if (typeof response.data === 'string') {
            token = response.data
          }
          // 情况2: token在data.token中
          else if (response.data && response.data.token) {
            token = response.data.token
          }
          // 情况3: token直接在response中
          else if (response.token) {
            token = response.token
          }
          
          if (!token) {
            throw new Error('无法从响应中获取token')
          }
          
          // 存储token
          this.token = token
          localStorage.setItem('token', token)
          
          // 如果响应中包含用户信息，也存储起来
          if (response.data && typeof response.data === 'object' && response.data.userInfo) {
            this.userInfo = response.data.userInfo
          }
          
          return response
        } else {
          throw new Error(response.message || response.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    
    // 获取用户信息
    async getInfo() {
      try {
        const response = await getUserInfo()
        const { data } = response
        
        this.userInfo = data
        
        return data
      } catch (error) {
        throw error
      }
    },
    
    // 登出
    logout() {
      this.token = ''
      this.userInfo = {}
      this.roles = []
      localStorage.removeItem('token')
    }
  }
})