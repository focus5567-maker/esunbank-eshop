package com.esun.eshop.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 新增商品的請求物件（Controller層接收前端傳入的資料）。
 * 欄位上的驗證標註會在 Controller 的 @Valid 生效時自動檢查，
 * 檢查失敗會被 GlobalExceptionHandler 的 MethodArgumentNotValidException handler 攔截，回400。
 */
@Getter
@Setter
public class ProductRequest {

    /** 商品名稱，不可為 null、空字串或只包含空白 */
    @NotBlank(message = "商品編號不可為空")
    private String productId;

    /** 商品名稱，不可為 null、空字串或只包含空白 */
    @NotBlank(message = "商品名稱不可為空")
    private String productName;

    /** 售價，不可為 null 且不可小於 0 */
    @NotNull(message = "售價不可為空")
    @DecimalMin(value = "0", inclusive = true, message = "售價不可小於0")
    private BigDecimal price;

    /** 庫存，不可為 null 且不可小於 0 */
    @NotNull(message = "庫存不可為空")
    @Min(value = 0, message = "庫存不可小於0")
    private Integer quantity;
}