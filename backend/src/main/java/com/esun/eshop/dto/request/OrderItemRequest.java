package com.esun.eshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 訂單明細的請求物件。
 * 作為 OrderRequest 內部 List 的元素型別，
 * 用來表示訂單中的單一商品與購買數量。
 */
@Getter
@Setter
public class OrderItemRequest {

    /** 商品編號，不可為 null、空字串或只包含空白 */
    @NotBlank(message = "商品編號不可為空")
    private String productId;

    /** 購買數量，不可為 null，且必須大於 0（不可以買0件或負數件） */
    @NotNull(message = "購買數量不可為空")
    @Min(value = 1, message = "購買數量必須大於0")
    private Integer quantity;
}