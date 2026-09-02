package com.esun.eshop.service.impl;

import com.esun.eshop.common.exception.BusinessException;
import com.esun.eshop.dto.request.ProductRequest;
import com.esun.eshop.dto.response.ProductResponse;
import com.esun.eshop.mapper.ProductMapper;
import com.esun.eshop.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品業務邏輯實作（業務層）。
 * 負責處理商品相關業務規則、呼叫資料層（ProductMapper），
 * 並將資料層的 ProductRow 轉換成對外的 ProductResponse。
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        // 業務規則：商品編號不可重複。
        // 主動先查一次，能在正常情況下提供清楚的錯誤訊息（"商品編號已存在"），
        // 而不是等資料庫 PRIMARY KEY 約束報錯。
        ProductMapper.ProductRow existing = productMapper.findById(request.getProductId());
        if (existing != null) {
            throw new BusinessException("商品編號已存在: " + request.getProductId());
        }

        productMapper.insertProduct(
                request.getProductId(),
                request.getProductName(),
                request.getPrice(),
                request.getQuantity()
        );

        return new ProductResponse(
                request.getProductId(),
                request.getProductName(),
                request.getPrice(),
                request.getQuantity()
        );
    }

    @Override
    public List<ProductResponse> getAvailableProducts() {
        List<ProductMapper.ProductRow> rows = productMapper.findAvailableProducts();
        return rows.stream()
                .map(row -> new ProductResponse(
                        row.productId,
                        row.productName,
                        row.price,
                        row.quantity
                ))
                .toList();
    }
}
