-- ============================================
-- 訂單相關 Stored Procedure
-- ============================================

-- 新增訂單主檔
CREATE OR REPLACE PROCEDURE sp_insert_order(
    p_order_id    VARCHAR(20),
    p_member_id   VARCHAR(20),
    p_total_price NUMERIC,
    p_pay_status  SMALLINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO orders (order_id, member_id, total_price, pay_status)
    VALUES (p_order_id, p_member_id, p_total_price, p_pay_status);
END;
$$;

-- 新增訂單明細
CREATE OR REPLACE PROCEDURE sp_insert_order_detail(
    p_order_id    VARCHAR(20),
    p_product_id  VARCHAR(10),
    p_quantity    INT,
    p_stand_price NUMERIC,
    p_item_price  NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO order_detail (order_id, product_id, quantity, stand_price, item_price)
    VALUES (p_order_id, p_product_id, p_quantity, p_stand_price, p_item_price);
END;
$$;

-- 扣減商品庫存（庫存不足時 RAISE EXCEPTION，讓外層 Spring Transaction 回滾）
CREATE OR REPLACE PROCEDURE sp_update_product_stock(
    p_product_id VARCHAR(10),
    p_quantity   INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_current_qty INT; --v_current_qty 用來存「目前庫存」
BEGIN
    SELECT quantity INTO v_current_qty
    FROM product
    WHERE product_id = p_product_id
    FOR UPDATE;  -- 鎖定該列，避免同時下單造成超賣

    IF v_current_qty IS NULL THEN
        RAISE EXCEPTION '商品不存在: %', p_product_id;
    END IF;

    IF v_current_qty < p_quantity THEN --p_quantity 要買的數量 傳入的parameter
        RAISE EXCEPTION '商品 % 庫存不足，目前庫存 %，欲扣減 %', p_product_id, v_current_qty, p_quantity;
    END IF;

    UPDATE product
    SET quantity = quantity - p_quantity,
        updated_at = now()
    WHERE product_id = p_product_id;
END;
$$;