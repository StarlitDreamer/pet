package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Review;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.ReviewService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ShopService shopService;

    public ReviewController(ReviewService reviewService, ShopService shopService) {
        this.reviewService = reviewService;
        this.shopService = shopService;
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Review review, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        review.setOwnerId(userId);

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, review.getOrderId());
        wrapper.eq(Review::getOwnerId, userId);
        if (reviewService.count(wrapper) > 0) {
            throw new BusinessException("You have already reviewed this order");
        }

        reviewService.save(review);
        return Result.success();
    }

    @GetMapping("/shop/{shopId}")
    public Result<List<Review>> listByShop(@PathVariable Long shopId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getShopId, shopId);
        wrapper.orderByDesc(Review::getCreateTime);
        List<Review> reviews = reviewService.list(wrapper);
        return Result.success(reviews);
    }

    @GetMapping("/order/{orderId}")
    public Result<Review> getByOrder(@PathVariable Long orderId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId);
        Review review = reviewService.getOne(wrapper);
        return Result.success(review);
    }

    @PutMapping("/reply/{id}")
    public Result<?> reply(@PathVariable Long id, @RequestBody Review body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Review review = reviewService.getById(id);
        if (review == null) {
            throw new BusinessException("Review not found");
        }
        Shop shop = shopService.getById(review.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        Review update = new Review();
        update.setId(id);
        update.setReply(body.getReply());
        update.setReplyTime(LocalDateTime.now());
        reviewService.updateById(update);
        return Result.success();
    }
}
