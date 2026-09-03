package com.esun.eshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 建立訂單的請求物件（Controller 層接收前端傳入的資料）。
 * items 是巢狀結構：一個訂單包含多個商品項目，對應前端「勾選多個商品並設定數量」的操作。
 */
@Getter
@Setter
public class OrderRequest {

    /** 會員編號，題目資料表有此欄位，但本次範圍未實作會員系統，先接受前端傳入的字串 */
    private String memberId;

    /**
     * 訂單品項清單，不可為空清單（至少要選一項商品才能建立訂單）。
     * @Valid 標註在這裡才會讓 Spring 深入驗證 List 裡每一個 OrderItemRequest 元素，
     * 沒有這個標註，List 內部元素的 @NotBlank/@Min 不會被觸發。
     */
    @NotEmpty(message = "訂單至少需要包含一項商品")
    @Valid
    private List<OrderItemRequest> items;
}
