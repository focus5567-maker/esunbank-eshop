package com.esun.eshop.service;

import com.esun.eshop.dto.request.ProductRequest;
import com.esun.eshop.dto.response.ProductResponse;

import java.util.List;

/**
 * 商品業務邏輯介面（業務層）。
 * Controller 依賴此介面，不直接依賴具體的 ProductServiceImpl，
 * 降低 Controller 與具體實作的耦合，也方便單元測試時進行 Mock。
 */
public interface ProductService {

    /**
     * 新增商品。
     * 業務規則：商品編號不可重複。
     * 由 Service 先檢查並拋出明確的業務例外，
     * 讓全域例外處理器可以回傳適當的錯誤訊息給前端。
     */
    ProductResponse addProduct(ProductRequest request);

    /**
     * 查詢庫存 > 0 的商品清單，給訂單建立頁面使用。
     */
    List<ProductResponse> getAvailableProducts();
}
