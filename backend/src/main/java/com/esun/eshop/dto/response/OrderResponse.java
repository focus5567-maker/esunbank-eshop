package com.esun.eshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 建立訂單成功後的回應物件。
 * 用來回傳訂單編號、訂單總金額與訂單明細。
 */
@Getter
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}
