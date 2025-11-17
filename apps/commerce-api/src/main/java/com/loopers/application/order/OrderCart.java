package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.product.ProductResult;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderCart {

    private final Map<Long, OrderInput.Create.Product> productMap;
    private final Map<Long, ProductResult.GetProductOptions.Item> optionMap;

    public static OrderCart from(OrderInput.Create input, ProductResult.GetProductOptions productOptions) {
        Map<Long, OrderInput.Create.Product> productMap = Map.copyOf(
                input.products().stream()
                        .collect(Collectors.toMap(OrderInput.Create.Product::productOptionId, Function.identity()))
        );

        Map<Long, ProductResult.GetProductOptions.Item> optionMap = Map.copyOf(
                productOptions.items().stream()
                        .collect(Collectors.toMap(ProductResult.GetProductOptions.Item::productOptionId, Function.identity()))
        );

        if (!productMap.keySet().equals(optionMap.keySet()))
            throw new CoreException(ErrorType.NOT_FOUND);

        return new OrderCart(productMap, optionMap);
    }

    public boolean isNotEnoughStock() {
        for (Long key : this.productMap.keySet()) {
            int quantity = this.productMap.get(key).quantity();
            int stockQuantity = this.optionMap.get(key).stockQuantity();

            if (quantity <= stockQuantity)
                return true;
        }

        return false;
    }

    public boolean isNotEnoughPoint(long balance) {
        return balance < calculateTotalPrice();
    }

    public long calculateTotalPrice() {
        long totalPrice = 0L;

        for (Long key : this.productMap.keySet()) {
            int sellingPrice = this.optionMap.get(key).sellingPrice();
            int quantity = this.productMap.get(key).quantity();

            totalPrice += (long) sellingPrice * quantity;
        }

        return totalPrice;
    }

    public OrderCommand.Create toCommand(Long userId) {
        List<OrderCommand.Create.Product> products = new ArrayList<>();

        for (Long key : this.productMap.keySet()) {
            Integer quantity = this.productMap.get(key).quantity();
            Integer sellingPrice = this.optionMap.get(key).sellingPrice();

            OrderCommand.Create.Product product = new OrderCommand.Create.Product(
                    key,
                    sellingPrice,
                    quantity
            );

            products.add(product);
        }

        return new OrderCommand.Create(
                userId,
                calculateTotalPrice(),
                List.copyOf(products)
        );
    }
}
