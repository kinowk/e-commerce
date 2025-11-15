package com.loopers.infrastructure.product;

import com.loopers.domain.brand.QBrand;
import com.loopers.domain.product.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.loopers.domain.product.ProductSortType.*;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final StockJpaRepository stockJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Page<ProductQueryResult.Products> getProductSummary(ProductQueryCommand.GetProductSummary queryCommand) {
        PageRequest pageRequest = PageRequest.of(queryCommand.page(), queryCommand.size());
        QProduct p = QProduct.product;
        QBrand b = QBrand.brand;

        String keyword = queryCommand.keyword();
        Long brandId = queryCommand.brandId();
        ProductSortType sortType = queryCommand.sortType();

        var query = queryFactory
                .select(
                        Projections.constructor(
                                ProductQueryResult.Products.class,
                                p.id,
                                p.name,
                                p.brandId,
                                p.basePrice,
                                p.likeCount
                        )
                )
                .from(p)
                .leftJoin(b).on(b.id.eq(p.brandId))
                .where(
                        containKeywordByProductName(keyword),
                        matchByBrandId(brandId)
                );

        long total = query.fetchCount();

        List<ProductQueryResult.Products> results = query
                .orderBy(getOrderBy(sortType))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageRequest, total);
    }

    @Override
    public Optional<ProductQueryResult.ProductOptions> findProductOptionsByIds(List<Long> productOptionIds) {
        QProduct p = QProduct.product;
        QProductOption po = QProductOption.productOption;
        QStock s = QStock.stock;

        NumberExpression<Integer> sellingPrice = p.basePrice.add(po.extraPrice).as("sellingPrice");

        List<ProductQueryResult.ProductOptions.Item> items = queryFactory
                .select(
                        p.id,
                        po.id,
                        sellingPrice,
                        s.quantity
                )
                .from(po)
                .join(p).on(p.id.eq(po.id))
                .join(s).on(s.productOption.id.eq(po.id))
                .where(po.id.in(productOptionIds))
                .stream()
                .map(row -> new ProductQueryResult.ProductOptions.Item(
                        row.get(p.id),
                        row.get(po.id),
                        row.get(sellingPrice),
                        row.get(s.quantity)
                ))
                .toList();

        if (items.isEmpty())
            return Optional.empty();

        ProductQueryResult.ProductOptions productOptions = new ProductQueryResult.ProductOptions(items);

        return Optional.of(productOptions);
    }

    @Override
    public List<Stock> findStocksByProductOptionIdsForUpdate(List<Long> productOptionIds) {
        return stockJpaRepository.findByProductOptionIdIn(productOptionIds);
    }

    @Override
    public List<Stock> saveStocks(List<Stock> stocks) {
        return stockJpaRepository.saveAll(stocks);
    }

    private OrderSpecifier<?>[] getOrderBy(ProductSortType sortType) {
        QProduct p = QProduct.product;

        if (sortType == LATEST) {
            return new OrderSpecifier[]{p.createdAt.desc()};
        } else if (sortType == PRICE_ASC) {
            return new OrderSpecifier[]{p.basePrice.asc()};
        } else if (sortType == LIKES_DESC) {
            return new OrderSpecifier[]{p.likeCount.desc()};
        }

        return new OrderSpecifier[]{p.createdAt.desc()};
    }

    private static BooleanExpression containKeywordByProductName(String keyword) {
        QProduct p = QProduct.product;
        return StringUtils.hasText(keyword) ? p.name.containsIgnoreCase(keyword) : null;
    }

    private static BooleanExpression matchByBrandId(Long brandId) {
        QProduct p = QProduct.product;
        return brandId == null ? null : p.brandId.eq(brandId);
    }
}
