import axios from 'axios'

/**
 * 統一的 axios 實例。
 * baseURL 指向後端 API，所有請求共用這個設定，
 * 之後如果要改後端網址（例如部署到別的主機），只需要改這裡一個地方。
 */
const http = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 回應攔截器：統一處理後端的 ApiResponse 格式（{success, message, data}）。
 * HTTP 200 時，檢查 success 欄位；成功則回傳 data，失敗則丟出帶有 message 的 Error。
 * HTTP 400/500 等非 2xx 回應則由錯誤處理函式統一取得後端 message，
 * 讓呼叫端可以使用一般的 try-catch 處理錯誤。
 */
http.interceptors.response.use(
  (response) => {
    const { success, message, data } = response.data
    if (success) {
      return data
    }
    return Promise.reject(new Error(message || '請求失敗'))
  },
  (error) => {
    // HTTP 400/500 等非 2xx 回應，錯誤內容仍在 error.response.data 裡（ApiResponse 格式）
    const message = error.response?.data?.message || '網路錯誤，請稍後再試'
    return Promise.reject(new Error(message))
  }
)

export default http