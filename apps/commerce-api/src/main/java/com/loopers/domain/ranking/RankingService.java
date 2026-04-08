package com.loopers.domain.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public RankingResult.Page getRankingPage(LocalDate date, int page, int size) {
        int offset = (page - 1) * size;
        List<RankingResult.Entry> entries = rankingRepository.getRankingPage(date, offset, size);

        if (entries.isEmpty()) {
            return new RankingResult.Page(date.toString(), page, size, List.of());
        }

        List<Long> productIds = entries.stream().map(RankingResult.Entry::productId).toList();

        Map<Long, Product> productMap = productIds.stream()
                .map(id -> productRepository.findById(id).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Long> brandIds = productMap.values().stream()
                .map(Product::getBrandId)
                .distinct()
                .toList();
        Map<Long, Brand> brandMap = brandRepository.findAllByIdIn(brandIds)
                .stream()
                .collect(Collectors.toMap(Brand::getId, b -> b));

        List<RankingResult.ProductRanking> rankings = entries.stream()
                .map(entry -> {
                    Product product = productMap.get(entry.productId());
                    if (product == null) {
                        return new RankingResult.ProductRanking(
                                entry.rank(), entry.productId(), null, null, null, null, entry.score());
                    }
                    Brand brand = brandMap.get(product.getBrandId());
                    return new RankingResult.ProductRanking(
                            entry.rank(), product.getId(), product.getName(),
                            brand != null ? brand.getName() : null,
                            product.getPrice(), product.getLikeCount(), entry.score());
                })
                .toList();

        return new RankingResult.Page(date.toString(), page, size, rankings);
    }

    public RankingResult.ProductRankInfo getProductRankInfo(LocalDate date, Long productId) {
        Long rank = rankingRepository.getRank(date, productId);
        Double score = rankingRepository.getScore(date, productId);
        if (rank == null) {
            return null;
        }
        return new RankingResult.ProductRankInfo(rank, score);
    }
}
