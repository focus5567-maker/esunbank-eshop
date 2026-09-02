package com.esun.eshop.controller;

import com.esun.eshop.common.response.ApiResponse;
import com.esun.eshop.dto.request.ProductRequest;
import com.esun.eshop.dto.response.ProductResponse;
import com.esun.eshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理 API（Controller 層）。
 * 提供商品新增，以及查詢庫存量大於零的商品清單功能。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 新增商品。
     * @Valid 觸發 ProductRequest 上的驗證標註，
     * 驗證失敗時由 GlobalExceptionHandler 攔截並回傳 HTTP 400。
     */
    @PostMapping
    public ApiResponse<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.addProduct(request));
    }

    /**
     * 查詢庫存 > 0 的商品清單，給訂單建立頁面使用。
     */
    @GetMapping("/available")
    public ApiResponse<List<ProductResponse>> getAvailableProducts() {
        return ApiResponse.success(productService.getAvailableProducts());
    }
}