package com.esun.eshop.common.response;

import lombok.Getter;

/**
 * 統一 API 回應格式。
 *
 * 共用層元件：統一 Controller 回傳的 JSON 結構，
 * 讓前端可以使用一致的格式處理 API 回應。
 *
 * @param <T> 回應資料的型別，例如 Product、Order 或 List<Product>
 */
@Getter
public class ApiResponse<T> {

    /** API 是否成功 */
    private final boolean success;

    /** API 回應訊息 */
    private final String message;

    /** API 回傳的實際資料 */
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /** 成功回應，使用預設訊息 OK */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    /** 成功回應，可自訂訊息 */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 失敗回應，data 為 null */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}