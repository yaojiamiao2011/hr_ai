import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'ai-config',
        name: 'AIConfig',
        component: () => import('@/views/ai-config/index.vue'),
        meta: { title: '大模型配置', icon: 'Setting' }
      },
      {
        path: 'ai-config/test/:id',
        name: 'AIConfigTest',
        component: () => import('@/views/ai-config/test.vue'),
        meta: { title: '模型测试', icon: 'ChatDotRound' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router