import http from './http'

export function createOrder(order) {
  return http.post('/api/orders', order)
}