-- ============================================
-- 資料庫 DDL
-- 資料庫引擎: PostgreSQL
-- ============================================

-- 商品主檔
CREATE TABLE product (
    product_id   VARCHAR(10)     PRIMARY KEY,
    product_name VARCHAR(100)    NOT NULL,
    price        NUMERIC(12, 2)  NOT NULL CHECK (price >= 0),
    quantity     INT             NOT NULL CHECK (quantity >= 0),
    created_at   TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP       NOT NULL DEFAULT now()
);

COMMENT ON TABLE product IS '商品主檔';
COMMENT ON COLUMN product.product_id IS '商品編號';
COMMENT ON COLUMN product.product_name IS '商品名稱';
COMMENT ON COLUMN product.price IS '售價';
COMMENT ON COLUMN product.quantity IS '庫存';

-- 訂單主檔（"order" 為 SQL 保留字，改用 orders）
CREATE TABLE orders (
    order_id     VARCHAR(20)     PRIMARY KEY,
    member_id    VARCHAR(20),
    total_price  NUMERIC(12, 2)  NOT NULL CHECK (total_price >= 0),
    pay_status   SMALLINT        NOT NULL DEFAULT 0 CHECK (pay_status IN (0, 1)),
    created_at   TIMESTAMP       NOT NULL DEFAULT now()
);

COMMENT ON TABLE orders IS '訂單主檔';
COMMENT ON COLUMN orders.order_id IS '訂單編號';
COMMENT ON COLUMN orders.member_id IS '會員編號（本次範圍未實作會員系統，先接受前端傳入值）';
COMMENT ON COLUMN orders.total_price IS '訂單總金額';
COMMENT ON COLUMN orders.pay_status IS '付款狀態 0=未付款 1=已付款';

-- 訂單明細
CREATE TABLE order_detail (
    order_item_sn  BIGSERIAL       PRIMARY KEY,
    order_id       VARCHAR(20)     NOT NULL REFERENCES orders(order_id),
    product_id     VARCHAR(10)     NOT NULL REFERENCES product(product_id),
    quantity       INT             NOT NULL CHECK (quantity > 0),
    stand_price    NUMERIC(12, 2)  NOT NULL CHECK (stand_price >= 0),
    item_price     NUMERIC(12, 2)  NOT NULL CHECK (item_price >= 0)
);

COMMENT ON TABLE order_detail IS '訂單明細';
COMMENT ON COLUMN order_detail.order_item_sn IS '訂單明細流水號';
COMMENT ON COLUMN order_detail.stand_price IS '單價（下單當下售價快照）';
COMMENT ON COLUMN order_detail.item_price IS '單品項總價 = stand_price * quantity';