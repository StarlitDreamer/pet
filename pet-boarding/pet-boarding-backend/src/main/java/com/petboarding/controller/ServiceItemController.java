package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.ServiceItem;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.ServiceItemService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/service")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;
    private final ShopService shopService;

    public ServiceItemController(ServiceItemService serviceItemService, ShopService shopService) {
        this.serviceItemService = serviceItemService;
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<List<ServiceItem>> list(@RequestParam Long shopId) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getShopId, shopId);
        wrapper.eq(ServiceItem::getStatus, Constants.SERVICE_ON_SHELF);
        wrapper.orderByAsc(ServiceItem::getSpecies);
        List<ServiceItem> items = serviceItemService.list(wrapper);
        return Result.success(items);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody ServiceItem serviceItem, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getById(serviceItem.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("Shop not found or no permission");
        }
        serviceItem.setId(null);
        serviceItem.setShopId(shop.getId());
        serviceItem.setStatus(Constants.SERVICE_ON_SHELF);
        serviceItemService.save(serviceItem);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ServiceItem serviceItem, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ServiceItem dbItem = serviceItemService.getById(serviceItem.getId());
        if (dbItem == null) {
            throw new BusinessException("Service item not found");
        }
        Shop shop = shopService.getById(dbItem.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        serviceItem.setShopId(dbItem.getShopId());
        serviceItemService.updateById(serviceItem);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<?> toggleStatus(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ServiceItem item = serviceItemService.getById(id);
        if (item == null) {
            throw new BusinessException("Service item not found");
        }
        Shop shop = shopService.getById(item.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        ServiceItem update = new ServiceItem();
        update.setId(id);
        update.setStatus(item.getStatus().equals(Constants.SERVICE_ON_SHELF)
                ? Constants.SERVICE_OFF_SHELF : Constants.SERVICE_ON_SHELF);
        serviceItemService.updateById(update);
        return Result.success();
    }
}
