<template>
  <div class="debug-container">
    <h1>调试页面</h1>
    <div class="debug-info">
      <h2>登录状态信息</h2>
      <p>isLoggedIn: {{ userStore.isLoggedIn }}</p>
      <p>Token: {{ userStore.token }}</p>
      <p>Username: {{ userStore.username }}</p>
      <p>UserInfo: {{ userStore.userInfo }}</p>
    </div>
    
    <div class="debug-info">
      <h2>LocalStorage</h2>
      <p>Token in localStorage: {{ localStorageToken }}</p>
    </div>
    
    <div class="debug-actions">
      <el-button @click="testLogin">测试登录</el-button>
      <el-button @click="testLogout">测试登出</el-button>
      <el-button @click="checkToken">检查Token</el-button>
      <el-button @click="goToDashboard">前往仪表盘</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const localStorageToken = ref('')

onMounted(() => {
  localStorageToken.value = localStorage.getItem('token')
  console.log('调试页面加载，当前token:', localStorageToken.value)
})

const testLogin = async () => {
  try {
    await userStore.login({
      userName: 'admin',
      password: change-me
    })
    localStorageToken.value = localStorage.getItem('token')
    ElMessage.success('登录测试成功')
  } catch (error) {
    ElMessage.error('登录测试失败: ' + error.message)
  }
}

const testLogout = () => {
  userStore.logout()
  localStorageToken.value = localStorage.getItem('token')
  ElMessage.success('登出成功')
}

const checkToken = () => {
  localStorageToken.value = localStorage.getItem('token')
  console.log('当前token:', localStorageToken.value)
  ElMessage.info(`Token: ${localStorageToken.value || '无'}`)
}

const goToDashboard = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.debug-container {
  padding: 20px;
}

.debug-info {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 5px;
}

.debug-actions {
  margin-top: 20px;
}

.debug-actions .el-button {
  margin-right: 10px;
}
</style>