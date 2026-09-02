package com.esun.eshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品資料存取介面（資料層）。
 * 對應 DB/SP/03_sp_product.sql 裡定義的Stored Procedure / Function，
 * 具體 SQL 呼叫方式寫在同名的 ProductMapper.xml。
 */
@Mapper
public interface ProductMapper {

    /**
     * 新增商品，呼叫 sp_add_product。
     */
    void insertProduct(
            @Param("productId") String productId,
            @Param("productName") String productName,
            @Param("price") BigDecimal price,
            @Param("quantity") Integer quantity
    );

    /**
     * 查詢庫存 > 0 的商品清單，呼叫 sp_get_available_products()。
     */
    List<ProductRow> findAvailableProducts();

    /**
     * 依商品編號查詢單一商品，呼叫 sp_get_product_by_id(p_product_id)。
     * Service 層新增商品前用這個檢查編號是否重複。
     */
    ProductRow findById(@Param("productId") String productId);

    /**
     * 對應 sp_get_available_products / sp_get_product_by_id 回傳的資料列。
     * 放在同一個檔案裡是因為它只是這個 Mapper 內部查詢結果的載體，
     * 不是對外的 API 回應格式（那個是 dto/response/ProductResponse）。
     */
    class ProductRow {
        public String productId;
        public String productName;
        public BigDecimal price;
        public Integer quantity;
    }
}
