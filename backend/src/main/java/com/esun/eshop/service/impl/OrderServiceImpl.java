package com.esun.eshop.service.impl;

import com.esun.eshop.common.exception.BusinessException;
import com.esun.eshop.dto.request.OrderItemRequest;
import com.esun.eshop.dto.request.OrderRequest;
import com.esun.eshop.dto.response.OrderItemResponse;
import com.esun.eshop.dto.response.OrderResponse;
import com.esun.eshop.mapper.OrderMapper;
import com.esun.eshop.mapper.ProductMapper;
import com.esun.eshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 訂單業務邏輯實作（業務層）。
 * 負責處理訂單建立的業務規則（庫存驗證、金額計算），
 * 協調 OrderMapper 與 ProductMapper 完成訂單主檔、訂單明細、庫存扣減等資料異動，
 * 並透過 @Transactional 管理整個訂單交易，
 * 發生 RuntimeException 等未處理例外時，相關資料異動會一起回滾。
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        // 第一階段：驗證所有品項（商品存在、庫存足夠）。
        // 先查出所有商品資料並完成驗證，確認所有品項都合法後，
        // 再進入後續的金額計算與資料寫入流程。
        List<ProductMapper.ProductRow> products = new ArrayList<>();
        for (OrderItemRequest item : request.getItems()) {
            ProductMapper.ProductRow product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + item.getProductId());
            }
            if (item.getQuantity() > product.quantity) {
                throw new BusinessException(
                        "商品「" + product.productName + "」庫存不足，目前庫存 "
                                + product.quantity + "，欲購買 " + item.getQuantity());
            }
            products.add(product);
        }

        // 第二階段：計算每項小計與訂單總金額。
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<BigDecimal> itemPrices = new ArrayList<>();
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (int i = 0; i < request.getItems().size(); i++) {
            OrderItemRequest itemRequest = request.getItems().get(i);
            ProductMapper.ProductRow product = products.get(i);

            BigDecimal itemPrice = product.price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            itemPrices.add(itemPrice);
            totalPrice = totalPrice.add(itemPrice);

            itemResponses.add(new OrderItemResponse(
                    product.productId, product.productName, itemRequest.getQuantity(),
                    product.price, itemPrice));
        }

        // 第三階段：寫入資料庫。
        // payStatus 固定傳 0（未付款）：尚未實作金流，訂單建立當下視為未付款。
        // 型別使用 (short) 0 而非 int，對應 PostgreSQL sp_insert_order 定義的 SMALLINT 參數型別。
        String orderId = generateOrderId();
        orderMapper.insertOrder(orderId, request.getMemberId(), totalPrice, (short) 0);

        for (int i = 0; i < request.getItems().size(); i++) {
            OrderItemRequest itemRequest = request.getItems().get(i);
            ProductMapper.ProductRow product = products.get(i);

            orderMapper.insertOrderDetail(
                    orderId, product.productId, itemRequest.getQuantity(),
                    product.price, itemPrices.get(i));

            // 扣庫存呼叫 sp_update_product_stock。
            // SP 內部使用 FOR UPDATE 鎖定商品資料列，並再次檢查庫存是否足夠。
            // 第一階段的庫存檢查屬於預先驗證，實際扣庫存時仍由 SP 進行即時檢查，避免並發交易造成超賣。
            orderMapper.updateProductStock(product.productId, itemRequest.getQuantity());
        }

        return new OrderResponse(orderId, totalPrice, itemResponses);
    }

    /**
     * 產生訂單編號。
     * 格式為 Ms + 8 位日期 + 6 位隨機數字，例如 Ms20260903123456。
     * 在本次實作題的範例下，採用日期加隨機數的方式產生訂單編號；
     * 若未來需要更高的唯一性或分散式環境下的 ID 生成能力，
     * 可改用 UUID 或雪花演算法等 ID 生成方案。
     */
    private String generateOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "Ms" + datePart + randomPart;
    }
}
