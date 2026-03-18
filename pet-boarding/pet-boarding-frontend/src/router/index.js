import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/owner',
    component: () => import('@/components/OwnerLayout.vue'),
    children: [
      { path: 'home', name: 'OwnerHome', component: () => import('@/views/owner/OwnerHome.vue') },
      { path: 'pets', name: 'PetManage', component: () => import('@/views/owner/PetManage.vue') },
      { path: 'search', name: 'ServiceSearch', component: () => import('@/views/owner/ServiceSearch.vue') },
      { path: 'orders', name: 'OrderManage', component: () => import('@/views/owner/OrderManage.vue') },
      { path: 'care/:orderId', name: 'CareView', component: () => import('@/views/owner/CareView.vue') },
      { path: 'reviews', name: 'ReviewManage', component: () => import('@/views/owner/ReviewManage.vue') },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/owner/UserProfile.vue') }
    ]
  },
  {
    path: '/provider',
    component: () => import('@/components/ProviderLayout.vue'),
    children: [
      { path: 'home', name: 'ProviderHome', component: () => import('@/views/provider/ProviderHome.vue') },
      { path: 'shop', name: 'ShopManage', component: () => import('@/views/provider/ShopManage.vue') },
      { path: 'services', name: 'ServiceManage', component: () => import('@/views/provider/ServiceManage.vue') },
      { path: 'orders', name: 'ProviderOrders', component: () => import('@/views/provider/ProviderOrders.vue') },
      { path: 'care', name: 'CareManage', component: () => import('@/views/provider/CareManage.vue') },
      { path: 'reviews', name: 'ProviderReviews', component: () => import('@/views/provider/ProviderReviews.vue') },
      { path: 'complaints', name: 'ComplaintManage', component: () => import('@/views/provider/ComplaintManage.vue') },
      { path: 'employees', name: 'EmployeeManage', component: () => import('@/views/provider/EmployeeManage.vue') },
      { path: 'attendance', name: 'AttendanceManage', component: () => import('@/views/provider/AttendanceManage.vue') },
      { path: 'marketing', name: 'MarketingManage', component: () => import('@/views/provider/MarketingManage.vue') }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
