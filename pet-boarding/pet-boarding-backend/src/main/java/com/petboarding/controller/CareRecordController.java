package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.CareRecord;
import com.petboarding.entity.OrderInfo;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.CareRecordService;
import com.petboarding.service.OrderInfoService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/care")
public class CareRecordController {

    private final CareRecordService careRecordService;
    private final OrderInfoService orderInfoService;
    private final ShopService shopService;

    public CareRecordController(CareRecordService careRecordService,
                                 OrderInfoService orderInfoService,
                                 ShopService shopService) {
        this.careRecordService = careRecordService;
        this.orderInfoService = orderInfoService;
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<List<CareRecord>> list(@RequestParam Long orderId) {
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareRecord::getOrderId, orderId);
        wrapper.orderByDesc(CareRecord::getCreateTime);
        List<CareRecord> records = careRecordService.list(wrapper);
        return Result.success(records);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody CareRecord careRecord, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderInfo order = orderInfoService.getById(careRecord.getOrderId());
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        Shop shop = shopService.getById(order.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        careRecord.setPetId(order.getPetId());
        careRecordService.save(careRecord);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CareRecord> detail(@PathVariable Long id) {
        CareRecord record = careRecordService.getById(id);
        if (record == null) {
            throw new BusinessException("Care record not found");
        }
        return Result.success(record);
    }
}
