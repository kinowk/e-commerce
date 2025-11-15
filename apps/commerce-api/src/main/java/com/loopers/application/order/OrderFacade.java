package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderResult;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentResult;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointResult;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductResult;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final PointService pointService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderOutput.GetOrderDetail getOrderDetail(OrderInput.GetOrderDetail input) {
        userService.getUser(input.userId());

        OrderCommand.GetOrderDetail orderCommand = input.toCommand();
        OrderResult.GetOrderDetail order = orderService.getOrderDetail(orderCommand);

        PaymentCommand.GetPayment paymentCommand = new PaymentCommand.GetPayment(input.orderId(), input.userId());

        PaymentResult.GetPayment payment = paymentService.getPayment(paymentCommand);

        return OrderOutput.GetOrderDetail.from(order, payment);
    }

    public OrderOutput.Create create(OrderInput.Create input) {
        List<OrderInput.Create.Product> products = input.products();

        if (products.isEmpty())
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상품이 없습니다.");

        userService.getUser(input.userId());

        List<Long> productOptionIds = products.stream()
                .map(OrderInput.Create.Product::productOptionId)
                .toList();
        ProductResult.GetProductOptions options = productService.getProductOptions(productOptionIds);

        OrderCart cart = OrderCart.from(input, options);

        if (!cart.isNotEnoughStock())
            throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");

        PointResult.GetPoint point = pointService.getPoint(input.userId());

        if (!cart.isNotEnoughPoint(point.getBalance()))
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트가 부족합니다.");

        OrderResult.Create result = orderService.create(cart.toCommand(input.userId()));

        return OrderOutput.Create.from(result);
    }
}
