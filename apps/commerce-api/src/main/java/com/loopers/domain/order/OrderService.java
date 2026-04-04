package com.loopers.domain.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponResult;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.UserRepository;
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
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final CouponService couponService;
    private final ExternalOrderClient externalOrderClient;

    @Transactional
    public OrderResult.Create createOrder(OrderCommand.Create command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 비어있습니다.");
        }

        // 상품 재고 확인 및 차감 (비관적 락)
        List<Product> orderedProducts = new ArrayList<>();
        for (OrderCommand.Create.Item item : command.items()) {
            Product product = productRepository.findByIdForUpdate(item.productId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
            product.decreaseStock(item.quantity());
            orderedProducts.add(product);
        }
        orderedProducts.forEach(productRepository::save);

        // 총 금액 계산
        long totalAmount = 0L;
        for (int i = 0; i < command.items().size(); i++) {
            totalAmount += orderedProducts.get(i).getPrice() * command.items().get(i).quantity();
        }

        // 쿠폰 적용 (비관적 락은 CouponService 내부에서 처리)
        long discountAmount = 0L;
        if (command.couponId() != null) {
            CouponResult.Use couponResult = couponService.useForOrder(
                    new CouponCommand.Use(command.couponId(), command.userLoginId()),
                    totalAmount
            );
            discountAmount = couponResult.discountAmount();
        }

        long finalAmount = totalAmount - discountAmount;

        // 포인트 확인 및 차감 (비관적 락)
        Long userId = userRepository.findByLoginId(command.userLoginId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."))
                .getId();
        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다."));
        point.deduct(finalAmount);
        pointRepository.save(point);

        // 주문 저장
        Order order = new Order(command.userLoginId(), totalAmount, discountAmount, command.couponId());
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

        // 외부 시스템 전송
        externalOrderClient.send(savedOrder.getId());

        return OrderResult.Create.of(savedOrder, savedItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResult.Summary> getOrders(String userLoginId) {
        return orderRepository.findByUserLoginId(userLoginId)
                .stream()
                .map(OrderResult.Summary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResult.Detail getOrder(String userLoginId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        if (!order.getUserLoginId().equals(userLoginId)) {
            throw new CoreException(ErrorType.NOT_FOUND);
        }
        List<OrderItem> items = orderRepository.findItemsByOrderId(orderId);
        return OrderResult.Detail.of(order, items);
    }
}
