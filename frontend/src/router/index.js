import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    redirect: '/dashboard/spot',
    children: [
      {
        path: 'spot',
        name: 'SpotList',
        component: () => import('@/views/SpotList.vue'),
        meta: { title: '钓点管理' }
      },
      {
        path: 'record',
        name: 'RecordList',
        component: () => import('@/views/RecordList.vue'),
        meta: { title: '渔获记录' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '季节统计' }
      },
      {
        path: 'share',
        name: 'ShareList',
        component: () => import('@/views/ShareList.vue'),
        meta: { title: '位点分享广场' }
      },
      {
        path: 'favorite',
        name: 'FavoriteList',
        component: () => import('@/views/FavoriteList.vue'),
        meta: { title: '我的收藏' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
