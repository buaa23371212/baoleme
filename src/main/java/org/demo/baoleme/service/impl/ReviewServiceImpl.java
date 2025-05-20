package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.ReviewMapper;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        review.setId(null); // 确保ID由数据库生成
        review.setCreatedAt(null); // 由自动填充处理
        int result = reviewMapper.insert(review);
        return result > 0 ? review : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(Long reviewId) {
        return reviewMapper.selectById(reviewId);
    }

    @Override
    @Transactional
    public boolean updateReview(Review review) {
        // 禁止更新createdAt字段
        review.setCreatedAt(null);
        return reviewMapper.updateById(review) > 0;
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId) {
        return reviewMapper.deleteById(reviewId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByStore(Long storeId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getStoreId, storeId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .between(Review::getRating, minRating, maxRating)
        );
    }
}