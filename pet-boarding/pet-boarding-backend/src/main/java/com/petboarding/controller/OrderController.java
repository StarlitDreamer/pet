package com.petboarding.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.OrderInfo;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.OrderInfoService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderInfoService orderInfoService;
    private final ShopService shopService;

    public OrderController(OrderInfoService orderInfoService, ShopService shopService) {
        this.orderInfoService = orderInfoService;
        this.shopService = shopService;
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody OrderInfo order, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        order.setOwnerId(userId);
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setStatus(Constants.ORDER_PENDING);
        if (order.getDiscountAmount() == null) {
            order.setDiscountAmount(java.math.BigDecimal.ZERO);
        }
        if (order.getPayAmount() == null) {
            order.setPayAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        }
        orderInfoService.save(order);
        return Result.success(order);
    }

    @GetMapping("/owner/list")
    public Result<?> ownerList(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer status,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOwnerId, userId);
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        orderInfoService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/provider/list")
    public Result<?> providerList(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) Integer status,
                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Shop> shopWrapper = new LambdaQueryWrapper<>();
        shopWrapper.eq(Shop::getProviderId, userId);
        Shop shop = shopService.getOne(shopWrapper);
        if (shop == null) {
            throw new BusinessException("No shop found for this provider");
        }

        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getShopId, shop.getId());
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        orderInfoService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<OrderInfo> detail(@PathVariable Long id) {
        OrderInfo order = orderInfoService.getById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        return Result.success(order);
    }

    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null || !order.getOwnerId().equals(userId)) {
            throw new BusinessException("Order not found or no permission");
        }
        if (!Constants.ORDER_PENDING.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow payment");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_PAID);
        update.setPayTime(LocalDateTime.now());
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null || !order.getOwnerId().equals(userId)) {
            throw new BusinessException("Order not found or no permission");
        }
        if (order.getStatus() > Constants.ORDER_PAID) {
            throw new BusinessException("Order cannot be cancelled at current status");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_CANCELLED);
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/accept/{id}")
    public Result<?> accept(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        verifyProviderPermission(userId, order.getShopId());
        if (!Constants.ORDER_PAID.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow acceptance");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_ACCEPTED);
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/reject/{id}")
    public Result<?> reject(@PathVariable Long id, @RequestBody(required = false) OrderInfo body,
                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        verifyProviderPermission(userId, order.getShopId());
        if (!Constants.ORDER_PAID.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow rejection");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_REJECTED);
        if (body != null) {
            update.setRejectReason(body.getRejectReason());
        }
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/checkin/{id}")
    public Result<?> checkin(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        verifyProviderPermission(userId, order.getShopId());
        if (!Constants.ORDER_ACCEPTED.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow check-in");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_CHECKED_IN);
        update.setCheckinTime(LocalDateTime.now());
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/checkout/{id}")
    public Result<?> checkout(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        verifyProviderPermission(userId, order.getShopId());
        if (!Constants.ORDER_CHECKED_IN.equals(order.getStatus())
                && !Constants.ORDER_BOARDING.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow check-out");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_CHECKED_OUT);
        update.setCheckoutTime(LocalDateTime.now());
        orderInfoService.updateById(update);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(id);
        if (order == null || !order.getOwnerId().equals(userId)) {
            throw new BusinessException("Order not found or no permission");
        }
        if (!Constants.ORDER_CHECKED_OUT.equals(order.getStatus())) {
            throw new BusinessException("Order status does not allow completion");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(Constants.ORDER_COMPLETED);
        orderInfoService.updateById(update);
        return Result.success();
    }

    private void verifyProviderPermission(Long userId, Long shopId) {
        Shop shop = shopService.getById(shopId);
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
    }
}
