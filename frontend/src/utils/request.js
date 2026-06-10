import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 基础URL，在vite.config.js中配置了代理
  timeout: 120000, // 请求超时时间（120秒，适配AI报告生成）
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 直接使用后端返回的token，不添加Bearer前缀
      config.headers.Authorization = token
      console.log('添加Authorization请求头:', config.headers.Authorization)
      console.log('请求URL:', config.url)
      console.log('请求方法:', config.method)
    } else {
      console.log('未找到token，未添加Authorization请求头')
    }
    
    // 如果是FormData，删除Content-Type头，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
      console.log('检测到FormData，删除Content-Type头')
      
      // 打印FormData内容用于调试
      console.log('FormData内容:')
      for (let [key, value] of config.data.entries()) {
        if (value instanceof File) {
          console.log(`- ${key}:`, value.name, value.size, value.type)
        } else {
          console.log(`- ${key}:`, value)
        }
      }
    }
    
    return config
  },
  error => {
    // 请求错误处理
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 统一处理响应数据
    const res = response.data
    
    console.log('API响应:', res)
    console.log('请求URL:', response.config.url)
    console.log('响应状态码:', response.status)
    
    // 如果返回的code不为200，则判断为错误
    if (res.code && res.code !== 200) {
      ElMessage({
        message: res.message || res.msg || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未授权，token过期或错误
      if (res.code === 401) {
        console.log('收到401错误，当前token:', localStorage.getItem('token'))
        console.log('收到401错误，响应详情:', res)
        // 重新登录
        ElMessage({
          message: '登录已过期，请重新登录',
          type: 'warning',
          duration: 5 * 1000
        })
        // 清除token
        localStorage.removeItem('token')
        // 跳转到登录页
        router.push('/login')
      }
      
      // 403: 权限不足
      if (res.code === 403) {
        ElMessage({
          message: '权限不足，无法访问',
          type: 'warning',
          duration: 5 * 1000
        })
      }
      
      return Promise.reject(new Error(res.message || res.msg || 'Error'))
    } else {
      // 返回完整的响应数据，保持结构一致
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 如果是网络错误或CORS错误，可能是后端API不可用
    if (error.message.includes('Network Error') || error.message.includes('CORS')) {
      console.log('检测到网络错误，可能是后端API不可用')
    }
    
    // 处理HTTP错误状态码
    let message = ''
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接出错${error.response.status}`
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else {
      message = '连接服务器失败'
    }
    
    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

export default request