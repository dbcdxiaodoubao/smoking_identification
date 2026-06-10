import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由组件懒加载
const Login = () => import('@/views/Login.vue')
const Layout = () => import('@/layout/index.vue')
const Home = () => import('@/views/Home.vue')
const Dashboard = () => import('@/views/Dashboard.vue')
const Camera = () => import('@/views/camera/index.vue')
const Incident = () => import('@/views/incident/index.vue')
const Dispose = () => import('@/views/dispose/index.vue')
const Detection = () => import('@/views/detect/index.vue')
const Monitor = () => import('@/views/monitor/index.vue')
const Debug = () => import('@/views/Debug.vue')
const Student = () => import('@/views/student/index.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/debug',
    name: 'Debug',
    component: Debug,
    meta: { title: '调试', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { title: '首页', requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '数据分析', requiresAuth: true }
      },
      {
        path: 'camera',
        name: 'Camera',
        component: Camera,
        meta: { title: '摄像头管理', requiresAuth: true }
      },
      {
        path: 'incident',
        name: 'Incident',
        component: Incident,
        meta: { title: '预警管理', requiresAuth: true }
      },
      {
        path: 'dispose',
        name: 'Dispose',
        component: Dispose,
        meta: { title: '处理事件', requiresAuth: true }
      },
      {
        path: 'detection',
        name: 'Detection',
        component: Detection,
        meta: { title: '图像识别', requiresAuth: true }
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: Monitor,
        meta: { title: '实时监控', requiresAuth: true }
      },
      {
        path: 'student',
        name: 'Student',
        component: Student,
        meta: { title: '学生管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 校园无烟慧眼系统` : '校园无烟慧眼系统'
  
  console.log('路由守卫检查:', {
    path: to.path,
    from: from.path,
    requiresAuth,
    isLoggedIn: userStore.isLoggedIn,
    token: change-me
    localStorageToken: localStorage.getItem('token')
  })
  
  if (to.path === '/') {
    // 特殊处理根路径
    if (userStore.isLoggedIn) {
      next('/home')
    } else {
      next('/login')
    }
  } else if (to.path === '/login') {
    // 访问登录页
    if (userStore.isLoggedIn) {
      next('/home')
    } else {
      // 未登录则显示登录页
      next()
    }
  } else if (requiresAuth && !userStore.isLoggedIn) {
    // 需要登录但未登录，跳转到登录页
    console.log('需要登录但未登录，跳转到登录页')
    next('/login')
  } else {
    console.log('路由守卫通过，继续导航')
    next()
  }
})

export default router