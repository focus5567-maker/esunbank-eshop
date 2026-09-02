package com.esun.eshop.common.exception;

/**
 * 業務邏輯例外
 * 用於表達「使用者操作在業務規則上不允許」的情況，例如：
 * - 商品編號重複
 * - 購買數量超過庫存
 * - 商品不存在
 *
 * 與系統層級例外（DB連線失敗等）區分，讓 GlobalExceptionHandler 能回傳不同的錯誤語意
 */
public class BusinessException extends RuntimeException {

        /**
     * 建立只有錯誤訊息的業務例外。
     *
     * @param message 錯誤訊息
     */

    public BusinessException(String message) {
        super(message);
    }

    /**
     * 建立包含錯誤訊息與原始例外原因的業務例外。
     *
     * @param message 錯誤訊息
     * @param cause 原始例外
     */

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
