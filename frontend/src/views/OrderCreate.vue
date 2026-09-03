<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAvailableProducts } from '../api/product'
import { createOrder } from '../api/order'

const products = ref([])
const loading = ref(false)
const errorMsg = ref('')
const orderResult = ref(null)

// 每個商品的勾選狀態與數量，用 productId 當 key
const selected = ref({})   // { P001: true/false }
const quantities = ref({}) // { P001: 1 }

async function loadProducts() {
  loading.value = true
  errorMsg.value = ''
  try {
    products.value = await getAvailableProducts()
    products.value.forEach(p => {
      selected.value[p.productId] = false
      quantities.value[p.productId] = 1
    })
  } catch (err) {
    errorMsg.value = err.message
  } finally {
    loading.value = false
  }
}

// 已勾選的品項，附上即時計算的小計
const selectedItems = computed(() => {
  return products.value
    .filter(p => selected.value[p.productId])
    .map(p => {
      const qty = quantities.value[p.productId] || 0
      return {
        productId: p.productId,
        productName: p.productName,
        price: p.price,
        quantity: qty,
        subtotal: (p.price * qty).toFixed(2)
      }
    })
})

const totalPrice = computed(() => {
  return selectedItems.value
    .reduce((sum, item) => sum + Number(item.subtotal), 0)
    .toFixed(2)
})

async function handleCreateOrder() {
  errorMsg.value = ''
  orderResult.value = null

  if (selectedItems.value.length === 0) {
    errorMsg.value = '請至少選擇一項商品'
    return
  }

  const payload = {
    memberId: '1713', // 本次範圍未實作會員系統，先用固定測試值
    items: selectedItems.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  }

  try {
    orderResult.value = await createOrder(payload)
    // 建立成功後重新載入商品清單，反映最新庫存
    selected.value = {}
    quantities.value = {}
    await loadProducts()
  } catch (err) {
    errorMsg.value = err.message
  }
}

onMounted(loadProducts)
</script>

<template>
  <div>
    <h2>建立訂單</h2>
    <p v-if="loading">載入中...</p>

    <table v-else class="table">
      <thead>
        <tr>
          <th></th>
          <th>商品編號</th>
          <th>商品名稱</th>
          <th>售價</th>
          <th>庫存</th>
          <th>購買數量</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in products" :key="p.productId">
          <td><input type="checkbox" v-model="selected[p.productId]" /></td>
          <td>{{ p.productId }}</td>
          <td>{{ p.productName }}</td>
          <td>{{ p.price }}</td>
          <td>{{ p.quantity }}</td>
          <td>
            <input
              type="number"
              min="1"
              :max="p.quantity"
              v-model.number="quantities[p.productId]"
              :disabled="!selected[p.productId]"
              style="width: 70px"
            />
          </td>
        </tr>
      </tbody>
    </table>

    <h3>訂單內容</h3>
    <table class="table" v-if="selectedItems.length > 0">
      <thead>
        <tr>
          <th>商品名稱</th>
          <th>單價</th>
          <th>數量</th>
          <th>小計</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in selectedItems" :key="item.productId">
          <td>{{ item.productName }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.quantity }}</td>
          <td>{{ item.subtotal }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>尚未選擇商品</p>

    <p class="total">訂單總金額：{{ totalPrice }}</p>

    <button @click="handleCreateOrder">建立訂單</button>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

    <div v-if="orderResult" class="success-box">
      <p>訂單建立成功！訂單編號：{{ orderResult.orderId }}</p>
      <p>總金額：{{ orderResult.totalPrice }}</p>
    </div>
  </div>
</template>

<style scoped>
.table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }
.table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
.table th { background: #f5f5f5; }
.total { font-size: 18px; font-weight: bold; margin: 12px 0; }
button { padding: 8px 16px; background: #42b983; color: white; border: none; border-radius: 4px; cursor: pointer; }
.error { color: #e74c3c; }
.success-box { margin-top: 16px; padding: 12px; background: #eafaf1; border: 1px solid #42b983; border-radius: 4px; }
</style>