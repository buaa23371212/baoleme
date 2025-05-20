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

    @Override
    @Transactional(readOnly = true)
    public List<Review> getFilteredReviews(
            Long storeId,
            Integer minRating,
            Integer maxRating,
            Boolean hasImage,
            int page,
            int pageSize
    ) {
        // Step1: 构建分页对象（MyBatis-Plus页码从1开始）
        Page<Review> pageObj = new Page<>(page, pageSize);

        // Step2: 构建查询条件
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStoreId, storeId);

        // 评分范围条件
        if (minRating != null && maxRating != null) {
            wrapper.between(Review::getRating, minRating, maxRating);
        }

        // 图片过滤条件
        if (hasImage != null && hasImage) {
            wrapper.isNotNull(Review::getImage);
        }

        // Step3: 执行分页查询
        Page<Review> resultPage = reviewMapper.selectPage(pageObj, wrapper);

        // Step4: 返回当前页数据
        return resultPage.getRecords();
    }
}