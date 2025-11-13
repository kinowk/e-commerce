package com.loopers.infrastructure.order;

import com.loopers.domain.order.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findOrderDetailById(Long orderId) {
        QOrder o = QOrder.order;
        QOrderProduct op = QOrderProduct.orderProduct;

        List<Tuple> rows = queryFactory
                .select(
                        o,
                        op
                )
                .from(o)
                .join(op).on(op.id.eq(o.id))
                .where(o.id.eq(orderId))
                .fetch();

        if (rows.isEmpty())
            return Optional.empty();

        Order order = rows.get(0).get(o);

        List<OrderProduct> orderProducts = rows.stream()
                .map(row -> row.get(op))
                .toList();

        order.addProduct(orderProducts);

        return Optional.of(order);
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }
}
