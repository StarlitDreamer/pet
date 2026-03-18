package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.Coupon;
import com.petboarding.entity.Shop;
import com.petboarding.entity.UserCoupon;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.CouponService;
import com.petboarding.service.ShopService;
import com.petboarding.service.UserCouponService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final ShopService shopService;

    public CouponController(CouponService couponService, UserCouponService userCouponService,
                             ShopService shopService) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<List<Coupon>> list() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1);
        wrapper.gt(Coupon::getRemainCount, 0);
        wrapper.gt(Coupon::getEndTime, LocalDateTime.now());
        wrapper.orderByDesc(Coupon::getCreateTime);
        List<Coupon> coupons = couponService.list(wrapper);
        return Result.success(coupons);
    }

    @GetMapping("/shop/{shopId}")
    public Result<List<Coupon>> listByShop(@PathVariable Long shopId) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getShopId, shopId);
        wrapper.eq(Coupon::getStatus, 1);
        wrapper.gt(Coupon::getEndTime, LocalDateTime.now());
        wrapper.orderByDesc(Coupon::getCreateTime);
        List<Coupon> coupons = couponService.list(wrapper);
        return Result.success(coupons);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Coupon coupon, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        if (!Constants.ROLE_PROVIDER.equals(role)) {
            throw new BusinessException("Only providers can create coupons");
        }
        Shop shop = shopService.getById(coupon.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        coupon.setRemainCount(coupon.getTotalCount());
        coupon.setStatus(1);
        couponService.save(coupon);
        return Result.success();
    }

    @PostMapping("/receive/{id}")
    public Result<?> receive(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Coupon coupon = couponService.getById(id);
        if (coupon == null) {
            throw new BusinessException("Coupon not found");
        }
        if (coupon.getRemainCount() <= 0) {
            throw new BusinessException("Coupon has been fully claimed");
        }
        if (coupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Coupon has expired");
        }

        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getCouponId, id);
        if (userCouponService.count(wrapper) > 0) {
            throw new BusinessException("You have already received this coupon");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(id);
        userCoupon.setStatus(Constants.COUPON_UNUSED);
        userCouponService.save(userCoupon);

        coupon.setRemainCount(coupon.getRemainCount() - 1);
        couponService.updateById(coupon);

        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<UserCoupon>> my(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.orderByDesc(UserCoupon::getReceiveTime);
        List<UserCoupon> coupons = userCouponService.list(wrapper);
        return Result.success(coupons);
    }
}
