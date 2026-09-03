package com.esun.eshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 訂單明細的回應物件。
 * 用來回傳訂單中各商品的商品資訊、購買數量、單價與單品項總價。
 */
@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal standPrice;   // 單價（下單當下售價快照）
    private BigDecimal itemPrice;    // 單品項總價 = standPrice × quantity
}
