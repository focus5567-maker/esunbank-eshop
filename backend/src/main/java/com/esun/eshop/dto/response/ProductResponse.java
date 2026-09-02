package com.esun.eshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 商品資料的回應物件（API 回傳給前端的格式）。
 * 與 ProductMapper.ProductRow 分開，
 * 避免資料層的資料結構直接暴露給 API，
 * 也讓資料層變動時不會直接影響對外的 API 格式。
 */
@Getter
@AllArgsConstructor
public class ProductResponse {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
