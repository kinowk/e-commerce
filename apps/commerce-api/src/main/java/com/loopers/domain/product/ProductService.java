package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductResult.GetProductDetail getProductDetail(Long productId) {
        return productRepository.findById(productId)
                .map(ProductResult.GetProductDetail::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 상세 정보가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public ProductResult.GetProductSummary getProductSummary(ProductCommand.GetProductSummary command) {
        ProductQueryCommand.GetProductSummary queryCommand = ProductQueryCommand.GetProductSummary.from(command);
        Page<ProductQueryResult.Products> productsPage = productRepository.getProductSummary(queryCommand);

        return ProductResult.GetProductSummary.from(productsPage);
    }

    @Transactional(readOnly = true)
    public ProductResult.GetProductOptions getProductOptions(List<Long> productOptionIds) {
        return productRepository.findProductOptionsByIds(productOptionIds)
                .map(ProductResult.GetProductOptions::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 옵션이 존재하지 않습니다."));
    }

    @Transactional
    public void deductStcoks(ProductCommand.DeductStocks command) {
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

        Map<Long, Stock> stockMap = productRepository.findStocksByProductOptionIdsForUpdate(productOptionIds)
                .stream()
                .collect(toMap(s -> s.getProductOption().getId(), Function.identity()));

        if (stockMap.size() != productOptionIds.size())
            throw new CoreException(ErrorType.NOT_FOUND);

        for (ProductCommand.DeductStocks.Item item : items) {
            Stock stock = stockMap.get(item.productOptionId());
            stock.deduct(item.amount());
        }
    }
}
