package com.petboarding.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String city,
                          @RequestParam(required = false) String keyword) {
        Page<Shop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getStatus, Constants.SHOP_ACTIVE);
        if (StrUtil.isNotBlank(city)) {
            wrapper.eq(Shop::getCity, city);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Shop::getName, keyword).or().like(Shop::getDescription, keyword));
        }
        wrapper.orderByDesc(Shop::getRating);
        shopService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Shop> detail(@PathVariable Long id) {
        Shop shop = shopService.getById(id);
        if (shop == null) {
            throw new BusinessException("Shop not found");
        }
        return Result.success(shop);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Shop shop, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        if (!Constants.ROLE_PROVIDER.equals(role)) {
            throw new BusinessException("Only providers can create shops");
        }
        shop.setProviderId(userId);
        shop.setStatus(Constants.SHOP_ACTIVE);
        shop.setRating(new java.math.BigDecimal("5.0"));
        shop.setCurrentCount(0);
        shopService.save(shop);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Shop shop, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop dbShop = shopService.getById(shop.getId());
        if (dbShop == null || !dbShop.getProviderId().equals(userId)) {
            throw new BusinessException("Shop not found or no permission");
        }
        shopService.updateById(shop);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<?> my(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getProviderId, userId);
        Shop shop = shopService.getOne(wrapper);
        return Result.success(shop);
    }
}
