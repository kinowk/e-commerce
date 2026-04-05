package com.loopers.domain.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponResult;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final CouponService couponService;

    @Transactional
    public OrderResult.Create createOrder(OrderCommand.Create command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 비어있습니다.");
        }

        // 상품 재고 확인 및 차감 (비관적 락), 총 금액 계산
        List<Product> orderedProducts = new ArrayList<>();
        long totalAmount = 0L;
        for (OrderCommand.Create.Item item : command.items()) {
            Product product = productRepository.findByIdForUpdate(item.productId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
            product.decreaseStock(item.quantity());
            totalAmount += product.getPrice() * item.quantity();
            orderedProducts.add(product);
        }
        orderedProducts.forEach(productRepository::save);

        // 쿠폰 적용 (비관적 락은 CouponService 내부에서 처리)
        long discountAmount = 0L;
        if (command.couponId() != null) {
            CouponResult.Use couponResult = couponService.useForOrder(
                    new CouponCommand.Use(command.couponId(), command.userId()),
                    totalAmount
            );
            discountAmount = couponResult.discountAmount();
        }

        long finalAmount = totalAmount - discountAmount;

        // 포인트 확인 및 차감 (비관적 락)
        if (finalAmount > 0) {
            Point point = pointRepository.findByUserIdForUpdate(command.userId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다."));
            point.deduct(finalAmount);
            pointRepository.save(point);
        }

        // 주문 저장
        Order order = new Order(command.userId(), totalAmount, discountAmount, command.couponId());
        Order savedOrder = orderRepository.save(order);

        // 주문 항목 저장
        List<OrderItem> savedItems = new ArrayList<>();
        for (int i = 0; i < command.items().size(); i++) {
            OrderItem item = new OrderItem(
                    savedOrder.getId(),
                    command.items().get(i).productId(),
                    command.items().get(i).quantity(),
                    orderedProducts.get(i).getPrice()
            );
            savedItems.add(orderRepository.saveItem(item));
        }

        return OrderResult.Create.of(savedOrder, savedItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResult.Summary> getOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderResult.Summary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResult.Detail getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        if (!order.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.NOT_FOUND);
        }
        List<OrderItem> items = orderRepository.findItemsByOrderId(orderId);
        return OrderResult.Detail.of(order, items);
    }
}
