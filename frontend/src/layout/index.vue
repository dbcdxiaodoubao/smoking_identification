<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="logo">
          <h2>校园无烟慧眼系统</h2>
        </div>
        <div class="menu-wrapper">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            router
            background-color="var(--sidebar-bg)"
            text-color="var(--sidebar-text)"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/home">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/monitor">
              <el-icon><VideoCamera /></el-icon>
              <span>实时监控</span>
            </el-menu-item>
            <el-menu-item index="/camera">
              <el-icon><Camera /></el-icon>
              <span>摄像头管理</span>
            </el-menu-item>
            <el-menu-item index="/incident">
              <el-icon><Warning /></el-icon>
              <span>预警管理</span>
            </el-menu-item>
            <el-menu-item index="/dispose">
              <el-icon><DocumentChecked /></el-icon>
              <span>处理事件</span>
            </el-menu-item>
            <el-menu-item index="/dashboard">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </el-menu-item>
            <el-menu-item v-if="false" index="/detection">
              <el-icon><Picture /></el-icon>
              <span>图像识别</span>
            </el-menu-item>
            <el-menu-item index="/student">
              <el-icon><User /></el-icon>
              <span>学生管理</span>
            </el-menu-item>
          </el-menu>
          <div class="menu-logo">
            <img src="/carousel/image6.png" alt="Logo" />
          </div>
        </div>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <div class="breadcrumb">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                  <el-icon><User /></el-icon>
                  {{ username }}
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentPageTitle = computed(() => route.meta.title || '首页')
const username = computed(() => userStore.username || '用户')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
}

.el-container {
  height: 100%;
}

.aside {
  --sidebar-bg: #263445;
  --sidebar-text: #bfcbd9;
  background-color: var(--sidebar-bg);
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: rgba(0, 0, 0, 0.12);
  color: #fff;
  flex-shrink: 0;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.menu-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

.menu-logo {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
  background-color: var(--sidebar-bg);
}

.menu-logo img {
  max-width: 100%;
  max-height: 70px;
  object-fit: contain;
}

.header {
  background-color: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border-lighter);
  padding: 0 var(--spacing-md);
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-sm);
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb { flex: 1; }

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: var(--color-text-regular);
  font-size: var(--font-size-base);
}

.el-dropdown-link:hover {
  color: var(--color-primary);
}

.main {
  background-color: var(--color-bg-page);
  height: 100vh;
  overflow-y: auto;
  padding: 5px 20px 20px 20px;
  padding-top: 5px;
}
</style>
