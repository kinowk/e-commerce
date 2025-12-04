package com.loopers.application.payment.processor;

import com.loopers.application.payment.PaymentOutput;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentResult;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor {

    private final PointService pointService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CARD;
    }

    @Override
    public PaymentOutput.Pay process(PaymentProcessContext context) {
        Long orderId = context.orderId();
        Long userId = context.userId();

        List<ProductCommand.DeductStocks.Item> items = context.products().stream()
                .map(product -> new ProductCommand.DeductStocks.Item(
                        product.productOptionId(),
                        product.quantity()
                ))
                .toList();

        ProductCommand.DeductStocks deductStocksCommand = new ProductCommand.DeductStocks(items);
        productService.deductStocks(deductStocksCommand);

        Long totalPrice = context.totalPrice();

        if (totalPrice != null && totalPrice > 0) {
            PointCommand.Use useCommand = new PointCommand.Use(
                    userId,
                    totalPrice
            );

            pointService.use(useCommand);
        }

        PaymentCommand.Pay payCommand = new PaymentCommand.Pay(
                orderId,
                userId,
                totalPrice,
                PaymentMethod.CARD
        );
        PaymentResult.Pay payResult = paymentService.pay(payCommand);

        orderService.complete(orderId);

        return PaymentOutput.Pay.from(payResult);
    }
}
