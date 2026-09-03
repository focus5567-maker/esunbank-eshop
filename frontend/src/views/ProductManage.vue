<script setup>
import { ref, onMounted } from 'vue'
import { getAvailableProducts, addProduct } from '../api/product'

const products = ref([])
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = ref({
  productId: '',
  productName: '',
  price: null,
  quantity: null
})

async function loadProducts() {
  loading.value = true
  errorMsg.value = ''
  try {
    products.value = await getAvailableProducts()
  } catch (err) {
    errorMsg.value = err.message
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  errorMsg.value = ''
  successMsg.value = ''
  try {
    await addProduct(form.value)
    successMsg.value = `商品「${form.value.productName}」新增成功`
    form.value = { productId: '', productName: '', price: null, quantity: null }
    await loadProducts()
  } catch (err) {
    errorMsg.value = err.message
  }
}

onMounted(loadProducts)
</script>

<template>
  <div>
    <h2>新增商品</h2>
    <form @submit.prevent="handleSubmit" class="form">
      <div class="field">
        <label>商品編號</label>
        <input v-model="form.productId" required />
      </div>
      <div class="field">
        <label>商品名稱</label>
        <input v-model="form.productName" required />
      </div>
      <div class="field">
        <label>售價</label>
        <input v-model.number="form.price" type="number" min="0" required />
      </div>
      <div class="field">
        <label>庫存</label>
        <input v-model.number="form.quantity" type="number" min="0" required />
      </div>
      <button type="submit">新增商品</button>
    </form>

    <p v-if="successMsg" class="success">{{ successMsg }}</p>
    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

    <h2>商品清單（庫存 &gt; 0）</h2>
    <p v-if="loading">載入中...</p>
    <table v-else class="table">
      <thead>
        <tr>
          <th>商品編號</th>
          <th>商品名稱</th>
          <th>售價</th>
          <th>庫存</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in products" :key="p.productId">
          <td>{{ p.productId }}</td>
          <td>{{ p.productName }}</td>
          <td>{{ p.price }}</td>
          <td>{{ p.quantity }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.form { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; margin-bottom: 16px; }
.field { display: flex; flex-direction: column; }
.field label { font-size: 12px; color: #666; margin-bottom: 4px; }
.field input { padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; }
button { padding: 8px 16px; background: #42b983; color: white; border: none; border-radius: 4px; cursor: pointer; }
.success { color: #42b983; }
.error { color: #e74c3c; }
.table { width: 100%; border-collapse: collapse; margin-top: 8px; }
.table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
.table th { background: #f5f5f5; }
</style>