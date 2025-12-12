package com.loopers.application.payment;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderResult;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentResult;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final PointService pointService;

    @Transactional
    public PaymentOutput.Ready ready(PaymentInput.Ready input) {
        Long userId = input.userId();
        Long orderId = input.orderId();

        userService.getUser(userId);

        OrderCommand.GetOrderDetail orderCommand = new OrderCommand.GetOrderDetail(userId, orderId);
        OrderResult.GetOrderDetail orderResult = orderService.getOrderDetail(orderCommand);

        if (orderResult.status() != OrderStatus.CREATED)
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 완료/취소된 주문입니다.");

        List<ProductCommand.DeductStocks.Item> items = orderResult.products().stream()
                .map(product -> new ProductCommand.DeductStocks.Item(
                        product.productOptionId(),
                        product.quantity()
                ))
                .toList();

        ProductCommand.DeductStocks productCommand = new ProductCommand.DeductStocks(items);
        productService.deductStocks(productCommand);

        Long totalPrice = orderResult.totalPrice();

        if (totalPrice > 0) {
            PointCommand.Use pointCommand = new PointCommand.Use(
                    userId,
                    totalPrice
            );

            pointService.use(pointCommand);
        }

        PaymentCommand.Pay paymentCommand = new PaymentCommand.Pay(
                orderId,
                userId,
                totalPrice,
                input.paymentMethod()
        );

        PaymentResult.Pay paymentResult = paymentService.pay(paymentCommand);
        orderService.complete(orderId);

        return PaymentOutput.Ready.from(paymentResult);
    }

    public void pay(Long paymentId) {
        PaymentResult.GetPayment paymentResult = paymentService.getPayment(paymentId);

        OrderCommand.GetOrderDetail orderCommand = new OrderCommand.GetOrderDetail(paymentResult.userId(), paymentResult.orderId());
        OrderResult.GetOrderDetail orderResult = orderService.getOrderDetail(orderCommand);
    }
}
