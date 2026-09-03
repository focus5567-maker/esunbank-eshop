import { createRouter, createWebHistory } from 'vue-router'
import ProductManage from '../views/ProductManage.vue'
import OrderCreate from '../views/OrderCreate.vue'

const routes = [
  { path: '/', redirect: '/products' },
  { path: '/products', name: 'ProductManage', component: ProductManage },
  { path: '/orders', name: 'OrderCreate', component: OrderCreate }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router