package com.esun.eshop.controller;

import com.esun.eshop.common.response.ApiResponse;
import com.esun.eshop.dto.request.OrderRequest;
import com.esun.eshop.dto.response.OrderResponse;
import com.esun.eshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 訂單管理 API（Controller 層）。
 * 提供建立訂單功能。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 建立訂單。
     * @Valid 觸發 OrderRequest 上的驗證標註（包含巢狀的 items 清單），
     * 驗證失敗時由 GlobalExceptionHandler 攔截並回傳 HTTP 400。
     */
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }
}
