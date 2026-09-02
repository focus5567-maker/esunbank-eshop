package com.esun.eshop.common.exception;

import com.esun.eshop.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全域例外處理器。
 * 共用層元件：攔截 Controller 層拋出的例外，統一轉換成 ApiResponse 格式回傳給前端，
 * 並設定適當的 HTTP 狀態碼，避免系統內部錯誤細節（Stack trace、類別名）外洩給前端。
 *
 * 各 handler 對應不同類型的例外：
 * - BusinessException：我自己在 Service 層主動 throw 的
 * - MethodArgumentNotValidException / ConstraintViolationException：Spring 驗證失敗時自動拋出
 * - Exception：兜底，涵蓋以上兩者以外的所有情況（包含 MyBatis/JDBC 層的資料庫例外）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 業務邏輯例外（例如庫存不足、商品不存在）→ 400 Bad Request */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    /** @Valid 標註的 @RequestBody 物件，欄位驗證失敗時 Spring 自動拋出 → 400 Bad Request */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ApiResponse.fail(message);
    }

    /** @RequestParam / @PathVariable 等單一參數驗證失敗 → 400 Bad Request */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    /**
     * 兜底：以上三種以外的所有例外都會落到這裡，包含 MyBatis 呼叫 Stored Procedure
     * 時（例如庫存不足 RAISE EXCEPTION）被 Spring 轉換過的資料庫例外 → 500 Internal Server Error。
     * 刻意不回傳 ex.getMessage()，避免系統內部細節外洩給前端。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnexpectedException(Exception ex) {
        return ApiResponse.fail("系統發生未預期錯誤，請聯繫管理員");
    }
}