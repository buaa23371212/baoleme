package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{storeId}")
    public CommonResponse getStoreReviews(@PathVariable Long storeId) {
        // Step1: 查询指定店铺的全部评论
        List<Review> reviews = reviewService.getReviewsByStore(storeId);

        // Step2: 构建响应体（空列表也视为正常结果）
        return ResponseBuilder.ok(reviews);
    }

    @GetMapping("/{storeId}/range")
    public CommonResponse getReviewsByRating(
            @PathVariable Long storeId,
            @RequestParam Integer min,
            @RequestParam Integer max
    ) {
        // Step1: 验证评分范围合法性
        if (min < 1 || max > 5 || min > max) {
            return ResponseBuilder.fail("评分范围不合法，有效值为1-5且min≤max");
        }

        // Step2: 查询指定评分范围的评论
        List<Review> reviews = reviewService.getReviewsByRatingRange(min, max);

        // Step3: 过滤非本店铺评论（安全增强）
        List<Review> filtered = reviews.stream()
                .filter(r -> r.getStoreId().equals(storeId))
                .toList();

        // Step4: 返回结果
        return ResponseBuilder.ok(filtered);
    }
}