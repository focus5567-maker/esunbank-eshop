-- ============================================
-- 商品相關 Stored Procedure / Function
-- ============================================

-- 新增商品
CREATE OR REPLACE PROCEDURE sp_add_product(
    p_product_id   VARCHAR(10),
    p_product_name VARCHAR(100),
    p_price        NUMERIC,
    p_quantity     INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO product (product_id, product_name, price, quantity)
    VALUES (p_product_id, p_product_name, p_price, p_quantity);
END;
$$;

-- 查詢庫存 > 0 的商品清單（FUNCTION 直接回傳 table，MyBatis 端當一般 SELECT 呼叫即可）
CREATE OR REPLACE FUNCTION sp_get_available_products()
RETURNS TABLE (
    product_id   VARCHAR(10),
    product_name VARCHAR(100),
    price        NUMERIC(12, 2),
    quantity     INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.product_id, p.product_name, p.price, p.quantity
    FROM product p
    WHERE p.quantity > 0
    ORDER BY p.product_id;
END;
$$;

-- 依商品編號查詢單一商品（訂單建立時，後端再次驗證庫存用）
CREATE OR REPLACE FUNCTION sp_get_product_by_id(p_product_id VARCHAR)
RETURNS TABLE (
    product_id   VARCHAR(10),
    product_name VARCHAR(100),
    price        NUMERIC(12, 2),
    quantity     INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.product_id, p.product_name, p.price, p.quantity
    FROM product p
    WHERE p.product_id = p_product_id;
END;
$$;