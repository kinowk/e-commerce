package com.loopers.application.payment;

import com.loopers.application.payment.processor.PaymentProcessContext;
import com.loopers.application.payment.processor.PaymentProcessor;
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

    private final OrderService orderService;
    private final UserService userService;
    private final List<PaymentProcessor> paymentProcessors;

    @Transactional
    public PaymentOutput.Pay pay(PaymentInput.Pay input) {
        Long userId = input.userId();
        Long orderId = input.orderId();

        userService.getUser(userId);

        OrderCommand.GetOrderDetail orderCommand = new OrderCommand.GetOrderDetail(userId, orderId);
        OrderResult.GetOrderDetail orderResult = orderService.getOrderDetail(orderCommand);

        if (orderResult.status() != OrderStatus.CREATED)
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 완료/취소된 주문입니다.");

        PaymentProcessor paymentProcessor = paymentProcessors.stream()
                .filter(processor -> processor.supports(input.paymentMethod()))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "결제 수단이 잘못 되었습니다."));

        PaymentProcessContext paymentProcessContext = PaymentProcessContext.of(userId, orderResult);
        return paymentProcessor.process(paymentProcessContext);
    }

}
