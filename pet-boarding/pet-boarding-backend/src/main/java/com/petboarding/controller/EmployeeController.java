package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Employee;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.EmployeeService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ShopService shopService;

    public EmployeeController(EmployeeService employeeService, ShopService shopService) {
        this.employeeService = employeeService;
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<List<Employee>> list(@RequestParam(required = false) Long shopId,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = resolveProviderShop(userId, shopId);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getShopId, shop.getId());
        wrapper.orderByDesc(Employee::getCreateTime);
        List<Employee> employees = employeeService.list(wrapper);
        return Result.success(employees);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Employee employee, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = resolveProviderShop(userId, employee.getShopId());
        employee.setShopId(shop.getId());
        employee.setStatus(1);
        employeeService.save(employee);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Employee employee, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Employee dbEmployee = employeeService.getById(employee.getId());
        if (dbEmployee == null) {
            throw new BusinessException("Employee not found");
        }
        Shop shop = shopService.getById(dbEmployee.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        employeeService.updateById(employee);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            throw new BusinessException("Employee not found");
        }
        Shop shop = shopService.getById(employee.getShopId());
        if (shop == null || !shop.getProviderId().equals(userId)) {
            throw new BusinessException("No permission");
        }
        employeeService.removeById(id);
        return Result.success();
    }

    private Shop resolveProviderShop(Long userId, Long shopId) {
        if (shopId != null) {
            Shop shop = shopService.getById(shopId);
            if (shop == null || !shop.getProviderId().equals(userId)) {
                throw new BusinessException("No permission");
            }
            return shop;
        }

        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getProviderId, userId);
        wrapper.orderByDesc(Shop::getCreateTime);
        Shop shop = shopService.getOne(wrapper.last("limit 1"));
        if (shop == null) {
            throw new BusinessException("Shop not found");
        }
        return shop;
    }
}
