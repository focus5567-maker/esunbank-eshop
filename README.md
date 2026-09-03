# 玉山銀行後端工程師實作題 - 電商購物中心系統

## 專案簡介

本專案為電商購物中心系統，依照實作題需求完成商品管理與訂單建立等核心功能。

主要功能：

* 新增商品
* 查詢有庫存的商品
* 建立訂單
* 新增訂單明細
* 扣減商品庫存

後端採用 Spring Boot 建立 RESTful API，前端使用 Vue 3，資料庫使用 PostgreSQL，並透過 MyBatis 呼叫 PostgreSQL Function / Procedure。

---

## 技術架構

### 技術棧

**Frontend**

* Vue 3
* Vue Router 4
* Axios
* Vite

**Backend**

* Java 17
* Spring Boot 3.3.4
* Spring MVC
* MyBatis
* Maven

**Database**

* PostgreSQL 16
* Stored Procedure / Function

### 系統架構

```text
Vue 3
  │
  │ Axios / HTTP
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Mapper（MyBatis）
  │
  ▼
PostgreSQL
  ├── Function：查詢
  └── Procedure：資料異動
```

---

## 專案結構

### Backend

```text
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/esun/eshop/
│       │       ├── controller/
│       │       ├── service/
│       │       │   └── impl/
│       │       ├── mapper/
│       │       ├── dto/
│       │       │   ├── request/
│       │       │   └── response/
│       │       ├── common/
│       │       │   ├── exception/
│       │       │   └── response/
│       │       └── config/
│       │
│       └── resources/
│           ├── mapper/
│           │   ├── ProductMapper.xml
│           │   └── OrderMapper.xml
│           └── application.yml
└── pom.xml
```

### Database

```text
DB/
├── DDL/
│   └── 01_create_tables.sql
│
├── DML/
│   └── 02_seed_data.sql
│
└── SP/
    ├── 03_sp_product.sql
    └── 04_sp_order.sql
```

---

## 資料庫設計

主要資料表：

### product

商品資料表，儲存：商品編號、商品名稱、商品價格、商品庫存、建立時間、更新時間

### orders

訂單主檔，儲存：訂單編號、會員編號、訂單總金額、付款狀態

使用 `orders` 作為訂單主檔資料表名稱，避免與 SQL `ORDER` 關鍵字產生混淆。

### order_detail

訂單明細，儲存：訂單編號、商品編號、購買數量、商品單價、小計

---

## Function / Procedure

本專案依照資料庫操作的性質，將查詢與資料異動分開處理。

### 查詢類：Function

* `sp_get_available_products`：查詢庫存 > 0 的商品清單
* `sp_get_product_by_id`：依商品編號查詢單一商品

例如：

```sql
SELECT product_id, product_name, price, quantity
FROM sp_get_available_products();
```

MyBatis 中使用一般 `SELECT` 語法呼叫 Function：

```xml
<select id="findAvailableProducts" resultMap="productResultMap">
    SELECT product_id, product_name, price, quantity
    FROM sp_get_available_products()
</select>
```

### 異動類：Procedure

* `sp_add_product`：新增商品
* `sp_insert_order`：新增訂單主檔
* `sp_insert_order_detail`：新增訂單明細
* `sp_update_product_stock`：扣減商品庫存（內含 `FOR UPDATE` 鎖定與即時庫存檢查）

MyBatis 使用 `CALLABLE` 搭配 `CALL` 語法呼叫：

```xml
<insert id="insertOrder" statementType="CALLABLE">
    CALL sp_insert_order(
        #{orderId,    mode=IN, jdbcType=VARCHAR},
        #{memberId,   mode=IN, jdbcType=VARCHAR},
        #{totalPrice, mode=IN, jdbcType=NUMERIC},
        #{payStatus,  mode=IN, jdbcType=SMALLINT}
    )
</insert>
```

因此本專案的資料庫呼叫方式為：

* **查詢 → Function → `SELECT ... FROM function()`**
* **異動 → Procedure → `CALL procedure(...)`**

後端透過 MyBatis 統一管理資料庫呼叫，將資料庫操作與 Java 業務邏輯分離。

---

## Transaction 管理

訂單建立流程使用 Spring `@Transactional` 管理整體交易。

訂單建立主要包含：

1. 新增 `orders` 訂單主檔
2. 新增 `order_detail` 訂單明細
3. 呼叫 Stored Procedure 扣減商品庫存

```java
@Transactional
public OrderResponse createOrder(OrderRequest request) {
    // 訂單建立流程
}
```

如果其中任一步驟發生 RuntimeException，例如庫存不足，Spring Transaction 會將此次交易中的資料異動回滾，避免只完成部分資料異動。

### 並發庫存控制

`sp_update_product_stock` 會使用：

```sql
SELECT quantity INTO v_current_qty
FROM product
WHERE product_id = p_product_id
FOR UPDATE;
```

透過 `FOR UPDATE` 鎖定商品資料列，並在 Stored Procedure 中再次確認庫存是否足夠：

```sql
IF v_current_qty < p_quantity THEN
    RAISE EXCEPTION '商品 % 庫存不足，目前庫存 %，欲扣減 %',
        p_product_id, v_current_qty, p_quantity;
END IF;
```

因此 Java Service 的庫存檢查屬於事前驗證，而真正扣庫存時仍由 Stored Procedure 進行即時確認。

### 實際測試

此交易機制已透過實際測試驗證。

測試方式為送出超過目前庫存數量的訂單請求，使 `sp_update_product_stock` 拋出庫存不足例外。

測試結果：

* `orders` 未殘留該筆失敗訂單
* `order_detail` 未殘留該筆失敗訂單明細
* 商品庫存未被扣減

確認 `@Transactional` 的 rollback 機制正常運作。

---

## SQL Injection 防護

MyBatis 使用 `#{}` 進行參數綁定，避免將使用者輸入直接串接至 SQL。

本專案未使用 `${}` 拼接使用者輸入。

```xml
#{productId}
#{quantity}
```

---

## XSS 防護

Vue 使用 `{{ }}` 進行一般資料綁定，會對 HTML 內容進行跳脫。

本專案未使用 `v-html` 直接渲染使用者輸入。

---

## API

### 商品

| Method | API                       | 說明       |
| ------ | ------------------------- | -------- |
| GET    | `/api/products/available` | 查詢有庫存的商品 |
| POST   | `/api/products`           | 新增商品     |

### 訂單

| Method | API           | 說明   |
| ------ | ------------- | ---- |
| POST   | `/api/orders` | 建立訂單 |

### 建立訂單範例

```json
{
  "memberId": "1713",
  "items": [
    {
      "productId": "P001",
      "quantity": 2
    }
  ]
}
```

---

## API 回應格式

後端使用統一的 `ApiResponse` 格式：

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

發生錯誤時：

```json
{
  "success": false,
  "message": "商品庫存不足",
  "data": null
}
```

前端透過 Axios Response Interceptor 統一處理 API 回應：

* 成功時直接取得 `data`
* 失敗時將後端 `message` 轉成 Error
* 各頁面透過 `try-catch` 處理錯誤

---

## 訂單編號

格式：`Ms` + 8 位日期 + 6 位隨機數字，例如 `Ms20260903123456`。

使用 Java `LocalDate` 搭配 `ThreadLocalRandom` 產生，在本次範例實作下使用；
未來若需支援分散式環境下的 ID 生成，可改用 UUID 或雪花演算法等 ID 生成方案。

---

## 資料庫密碼管理

資料庫帳號密碼不直接寫入 Repository，而是透過環境變數注入：

```yaml
username: ${DB_USERNAME:postgres}
password: ${DB_PASSWORD}
```

如此可以讓不同環境使用不同的資料庫設定，也避免將敏感資訊直接寫入程式碼或設定檔。

---

## 啟動方式

### 1. 建立 PostgreSQL Database

```bash
psql -U postgres
```

於 psql 內執行：

```sql
CREATE DATABASE esunbank_eshop
WITH
    ENCODING 'UTF8'
    LC_COLLATE 'C'
    LC_CTYPE 'C'
    TEMPLATE template0;
```

```sql
\q
```

> 建議使用 UTF-8 與 `template0` 建立資料庫，避免執行包含中文資料的 DDL / DML 腳本時因作業系統語系不同而發生編碼問題。

### 2. 執行資料庫腳本

於專案根目錄依序執行：

```bash
psql -U postgres -d esunbank_eshop -f DB/DDL/01_create_tables.sql
psql -U postgres -d esunbank_eshop -f DB/DML/02_seed_data.sql
psql -U postgres -d esunbank_eshop -f DB/SP/03_sp_product.sql
psql -U postgres -d esunbank_eshop -f DB/SP/04_sp_order.sql
```

建立資料表、測試資料及 PostgreSQL Function / Procedure。

### 3. 設定環境變數並啟動 Backend

**Windows PowerShell**

```powershell
$env:DB_PASSWORD="your_postgres_password"
cd backend
mvn spring-boot:run
```

**macOS / Linux**

```bash
export DB_PASSWORD=your_postgres_password
cd backend
mvn spring-boot:run
```

> 若 PostgreSQL 使用者名稱不是 `postgres`，可另外設定 `DB_USERNAME` 環境變數。

Backend 預設啟動於：

```text
http://localhost:8080
```

### 4. 啟動 Frontend

另開一個終端機：

```bash
cd frontend
npm install
npm run dev
```

Frontend 預設啟動於：

```text
http://localhost:5173
```

開啟瀏覽器進入上述網址即可操作。

---

## 專案範圍

本次實作主要完成：

* 新增商品與商品查詢
* 建立訂單與訂單明細
* 庫存扣減
* Stored Procedure / Function
* Transaction 管理
* SQL Injection 與 XSS 防護

本次實作以商品、訂單及庫存等核心流程為主，
會員、權限、金流及物流等延伸功能則未納入本次實作範圍。
