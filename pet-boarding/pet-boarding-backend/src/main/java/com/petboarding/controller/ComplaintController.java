package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.Complaint;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.ComplaintService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final ShopService shopService;

    public ComplaintController(ComplaintService complaintService, ShopService shopService) {
        this.complaintService = complaintService;
        this.shopService = shopService;
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Complaint complaint, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        complaint.setComplainantId(userId);
        complaint.setStatus(Constants.COMPLAINT_PENDING);
        complaintService.save(complaint);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Complaint>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");

        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        if (Constants.ROLE_OWNER.equals(role)) {
            wrapper.eq(Complaint::getComplainantId, userId);
        } else if (Constants.ROLE_PROVIDER.equals(role)) {
            LambdaQueryWrapper<Shop> shopWrapper = new LambdaQueryWrapper<>();
            shopWrapper.eq(Shop::getProviderId, userId);
            Shop shop = shopService.getOne(shopWrapper);
            if (shop != null) {
                wrapper.eq(Complaint::getShopId, shop.getId());
            } else {
                return Result.success(java.util.Collections.emptyList());
            }
        }
        wrapper.orderByDesc(Complaint::getCreateTime);
        List<Complaint> complaints = complaintService.list(wrapper);
        return Result.success(complaints);
    }

    @PutMapping("/reply/{id}")
    public Result<?> reply(@PathVariable Long id, @RequestBody Complaint body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Complaint complaint = complaintService.getById(id);
        if (complaint == null) {
            throw new BusinessException("Complaint not found");
        }
        Shop shop = shopService.getById(complaint.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        Complaint update = new Complaint();
        update.setId(id);
        update.setReply(body.getReply());
        update.setReplyTime(LocalDateTime.now());
        update.setStatus(Constants.COMPLAINT_PROCESSING);
        complaintService.updateById(update);
        return Result.success();
    }

    @PutMapping("/resolve/{id}")
    public Result<?> resolve(@PathVariable Long id, @RequestBody(required = false) Complaint body) {
        Complaint complaint = complaintService.getById(id);
        if (complaint == null) {
            throw new BusinessException("Complaint not found");
        }
        Complaint update = new Complaint();
        update.setId(id);
        update.setStatus(Constants.COMPLAINT_RESOLVED);
        if (body != null) {
            update.setResult(body.getResult());
        }
        complaintService.updateById(update);
        return Result.success();
    }
}
