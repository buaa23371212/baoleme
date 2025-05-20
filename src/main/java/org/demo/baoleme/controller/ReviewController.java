package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.review.ReviewReadRequest;
import org.demo.baoleme.dto.response.review.ReviewReadResponse;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
        return ResponseBuilder.ok(convertToResponse(reviews));
    }

    @PostMapping("/{storeId}/filter")
    public CommonResponse getFilteredReviews(
            @PathVariable Long storeId,
            @RequestBody ReviewReadRequest request
    ) {
        // Step1: 验证分页参数合法性
        if (request.getPage() < 1 || request.getPageSize() < 1) {
            return ResponseBuilder.fail("分页参数必须大于0");
        }

        // Step2: 根据筛选类型确定评分范围
        Integer min = null;
        Integer max = null;
        if (request.getType() != null) {
            switch (request.getType()) {
                case POSITIVE:
                    min = 4;
                    max = 5;
                    break;
                case NEGATIVE:
                    min = 1;
                    max = 2;
                    break;
                default:
                    return ResponseBuilder.fail("无效的筛选类型");
            }
        }

        // Step3: 查询符合条件的评论（包含分页和图片过滤）
        List<Review> reviews = reviewService.getFilteredReviews(
                storeId,
                min,
                max,
                request.getHasImage(),
                request.getPage(),
                request.getPageSize()
        );

        // Step4: 转换为响应体
        List<ReviewReadResponse> responses = convertToResponse(reviews);

        // Step5: 返回结果
        return ResponseBuilder.ok(responses);
    }

    // 辅助方法：将Review转换为ReviewReadResponse
    private List<ReviewReadResponse> convertToResponse(List<Review> reviews) {
        return reviews.stream().map(review -> {
            ReviewReadResponse response = new ReviewReadResponse();
            // TODO: 使用占位符
            response.setUsername(review.getUser().getName());
            response.setRating(review.getRating());
            response.setComment(review.getComment());
            response.setCreatedAt(review.getCreatedAt());
            response.setUserAvatar(review.getUser().getAvatar());
            response.setImage(review.getImage());
            return response;
        }).collect(Collectors.toList());
    }
}