package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public ProductResult.GetProductDetail getProductDetail(Long productId) {
        String key = "product:detail:" + productId;

        Object cached = null;
        try {
            cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof ProductResult.GetProductDetail result)
                return result;
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 조회 실패: {}", key, e);
        }

        ProductResult.GetProductDetail result = productRepository.findById(productId)
                .map(ProductResult.GetProductDetail::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 상세 정보가 존재하지 않습니다."));

        try {
            redisTemplate.opsForValue().set(key, result, Duration.ofMinutes(10));
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 저장 실패: {}", key, e);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ProductResult.GetProductSummary getProductSummary(ProductCommand.GetProductSummary command) {
        String key = "product:" +
                ":" + command.page() +
                ":" + command.size() +
                ":" + command.sortType();

        Object cached = null;
        try {
            cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof ProductResult.GetProductSummary result)
                return result;
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 조회 실패: {}", key, e);
        }
        ProductQueryCommand.GetProductSummary queryCommand = ProductQueryCommand.GetProductSummary.from(command);
        Page<ProductQueryResult.Products> productsPage = productRepository.getProductSummary(queryCommand);
        ProductResult.GetProductSummary result = ProductResult.GetProductSummary.from(productsPage);

        try {
            redisTemplate.opsForValue().set(key, result, Duration.ofMinutes(5));
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 저장 실패: {}", key, e);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ProductResult.GetProductOptions getProductOptions(List<Long> productOptionIds) {
        return productRepository.findProductOptionsByIds(productOptionIds)
                .map(ProductResult.GetProductOptions::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 옵션이 존재하지 않습니다."));
    }

    @Transactional
    public void addStocks(ProductCommand.AddStocks command) {
        List<ProductCommand.AddStocks.Item> items = command.items();
        if (items.isEmpty())
            return;

        long distinctItemCount = items.stream()
                .map(ProductCommand.AddStocks.Item::productOptionId)
                .distinct()
                .count();

        if (distinctItemCount != items.size())
            throw new CoreException(ErrorType.BAD_REQUEST, "중복된 상품 옵션이 존재합니다.");

        List<Long> productOptionIds = items.stream()
                .map(ProductCommand.AddStocks.Item::productOptionId)
                .toList();

        Map<Long, Stock> stockMap = productRepository.findStocksForUpdate(productOptionIds)
                .stream()
                .collect(toMap(stock -> stock.getProductOption().getId(), Function.identity()));

        if (stockMap.size() != productOptionIds.size())
            throw new CoreException(ErrorType.NOT_FOUND);

        for (ProductCommand.AddStocks.Item item : items) {
            Stock stock = stockMap.get(item.productOptionId());
            stock.add(item.amount());
        }

        productRepository.saveStocks(List.copyOf(stockMap.values()));
    }

    @Transactional
    public void deductStocks(ProductCommand.DeductStocks command) {
        List<ProductCommand.DeductStocks.Item> items = command.items();
        if (items.isEmpty())
            return;

        long distinctItemCount = items.stream()
                .map(ProductCommand.DeductStocks.Item::productOptionId)
                .distinct()
                .count();

        if (distinctItemCount != items.size())
            throw new CoreException(ErrorType.BAD_REQUEST, "중복된 상품 옵션이 존재합니다.");

        List<Long> productOptionIds = items.stream()
                .map(ProductCommand.DeductStocks.Item::productOptionId)
                .toList();

        Map<Long, Stock> stockMap = productRepository.findStocksForUpdate(productOptionIds)
                .stream()
                .collect(toMap(stock -> stock.getProductOption().getId(), Function.identity()));

        if (stockMap.size() != productOptionIds.size())
            throw new CoreException(ErrorType.NOT_FOUND);

        for (ProductCommand.DeductStocks.Item item : items) {
            Stock stock = stockMap.get(item.productOptionId());
            stock.deduct(item.amount());
        }

        productRepository.saveStocks(List.copyOf(stockMap.values()));
    }

    @Transactional
    public void likeProduct(Long productId) {
        productRepository.increaseLikeCount(productId);
    }

    @Transactional
    public void dislikeProduct(Long productId) {
        productRepository.decreaseLikeCount(productId);
    }
}
