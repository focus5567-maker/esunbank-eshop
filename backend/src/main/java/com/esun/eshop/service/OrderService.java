package com.esun.eshop.service;

import com.esun.eshop.dto.request.OrderRequest;
import com.esun.eshop.dto.response.OrderResponse;

/**
 * 訂單業務邏輯介面（業務層）。
 * Controller 依賴此介面，不直接依賴具體的 OrderServiceImpl，
 * 降低 Controller 與具體實作的耦合，也方便單元測試時進行 Mock。
 */
public interface OrderService {

    /**
     * 建立訂單。
     * 業務規則：每項商品的購買數量不可超過目前庫存。
     * 此流程涉及訂單主檔、訂單明細與商品庫存的異動，
     * 由 Service 實作層的 @Transactional 管理，
     * 確保所有資料異動要嘛全部成功、要嘛全部回滾。
     */
    OrderResponse createOrder(OrderRequest request);
}
