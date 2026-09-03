package com.esun.eshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 訂單資料存取介面（資料層）。
 * 負責透過 MyBatis 呼叫訂單相關 Stored Procedure。
 * 對應 DB/SP/04_sp_order.sql 中定義的三支 Stored Procedure，
 * 實際的 SQL 呼叫語句則定義於 OrderMapper.xml。
 */
@Mapper
public interface OrderMapper {

    /**
     * 新增訂單主檔，呼叫 sp_insert_order。
     */
    void insertOrder(
            @Param("orderId") String orderId,
            @Param("memberId") String memberId,
            @Param("totalPrice") BigDecimal totalPrice,
            @Param("payStatus") Short payStatus
    );

    /**
     * 新增訂單明細，呼叫 sp_insert_order_detail。
     */
    void insertOrderDetail(
            @Param("orderId") String orderId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity,
            @Param("standPrice") BigDecimal standPrice,
            @Param("itemPrice") BigDecimal itemPrice
    );

    /**
     * 扣減商品庫存，呼叫 sp_update_product_stock。
     * Stored Procedure 透過 FOR UPDATE 鎖定商品資料列，
     * 並在庫存不足時拋出例外。
     * 由 Service 層的 @Transactional 管理整個訂單交易，
     * 發生例外時會回滾相關資料異動。
     */
    void updateProductStock(
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );
}
