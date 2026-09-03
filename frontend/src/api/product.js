import http from './http'

export function getAvailableProducts() {
  return http.get('/api/products/available')
}

export function addProduct(product) {
  return http.post('/api/products', product)
}