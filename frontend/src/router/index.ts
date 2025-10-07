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
      },
      {
        path: 'video-tool',
        name: 'VideoTool',
        component: () => import('@/views/video-tool/index.vue'),
        meta: { title: '视频工具', icon: 'VideoPlay' }
      },
      {
        path: 'video-tool/test-video',
        name: 'TestVideo',
        component: () => import('@/views/video-tool/test-video.vue'),
        meta: { title: '测试视频转文本', icon: 'VideoPlay' }
      },
      {
        path: 'video-tool/test-specific-video',
        name: 'TestSpecificVideo',
        component: () => import('@/views/video-tool/test-specific-video.vue'),
        meta: { title: '测试特定视频转文本', icon: 'VideoPlay' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router