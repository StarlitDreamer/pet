package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Coupon;
import com.petboarding.entity.PointsRecord;
import com.petboarding.entity.User;
import com.petboarding.entity.UserCoupon;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.CouponService;
import com.petboarding.service.PointsRecordService;
import com.petboarding.service.UserCouponService;
import com.petboarding.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    private final PointsRecordService pointsRecordService;
    private final UserService userService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public PointsController(PointsRecordService pointsRecordService, UserService userService,
                             CouponService couponService, UserCouponService userCouponService) {
        this.pointsRecordService = pointsRecordService;
        this.userService = userService;
        this.couponService = couponService;
        this.userCouponService = userCouponService;
    }

    @GetMapping("/records")
    public Result<List<PointsRecord>> records(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        List<PointsRecord> records = pointsRecordService.list(wrapper);
        return Result.success(records);
    }

    @PostMapping("/exchange")
    public Result<?> exchange(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long couponId = body.get("couponId");
        if (couponId == null) {
            throw new BusinessException("Coupon ID is required");
        }

        Coupon coupon = couponService.getById(couponId);
        if (coupon == null) {
            throw new BusinessException("Coupon not found");
        }
        // Use coupon value as the points cost for exchange
        int pointsCost = coupon.getValue().intValue();
        if (pointsCost <= 0) {
            throw new BusinessException("This coupon cannot be exchanged with points");
        }

        User user = userService.getById(userId);
        if (user.getPoints() < pointsCost) {
            throw new BusinessException("Insufficient points");
        }

        // Deduct points
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPoints(user.getPoints() - pointsCost);
        userService.updateById(updateUser);

        // Create points record
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-pointsCost);
        record.setType(2);
        record.setDescription("Exchange for coupon: " + coupon.getName());
        record.setOrderId(null);
        pointsRecordService.save(record);

        // Give user the coupon
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCouponService.save(userCoupon);

        return Result.success();
    }
}
