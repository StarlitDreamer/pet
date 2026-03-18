package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Attendance;
import com.petboarding.entity.Employee;
import com.petboarding.entity.Shop;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.AttendanceService;
import com.petboarding.service.EmployeeService;
import com.petboarding.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final ShopService shopService;

    public AttendanceController(AttendanceService attendanceService,
                                EmployeeService employeeService,
                                ShopService shopService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.shopService = shopService;
    }

    @GetMapping("/list")
    public Result<List<Attendance>> list(@RequestParam(required = false) Long employeeId,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = resolveProviderShop(userId);

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getShopId, shop.getId());

        if (employeeId != null) {
            Employee employee = requireProviderEmployee(userId, employeeId);
            wrapper.eq(Attendance::getEmployeeId, employee.getId());
        }

        String rangeStart = startDate;
        String rangeEnd = endDate;
        if (date != null) {
            rangeStart = date;
            rangeEnd = date;
        }
        if (rangeStart != null) {
            wrapper.ge(Attendance::getDate, LocalDate.parse(rangeStart));
        }
        if (rangeEnd != null) {
            wrapper.le(Attendance::getDate, LocalDate.parse(rangeEnd));
        }

        wrapper.orderByDesc(Attendance::getDate).orderByDesc(Attendance::getClockIn);
        List<Attendance> records = attendanceService.list(wrapper);
        return Result.success(records);
    }

    @PostMapping("/clockin")
    public Result<?> clockIn(@RequestBody Attendance attendance, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Employee employee = requireProviderEmployee(userId, attendance.getEmployeeId());

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employee.getId());
        wrapper.eq(Attendance::getDate, LocalDate.now());
        if (attendanceService.count(wrapper) > 0) {
            throw new BusinessException("Already clocked in today");
        }

        Attendance record = new Attendance();
        record.setEmployeeId(employee.getId());
        record.setShopId(employee.getShopId());
        record.setDate(LocalDate.now());
        record.setClockIn(LocalDateTime.now());
        record.setStatus(1);
        attendanceService.save(record);
        return Result.success();
    }

    @PutMapping("/clockout")
    public Result<?> clockOut(@RequestBody Attendance attendance, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Employee employee = requireProviderEmployee(userId, attendance.getEmployeeId());

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employee.getId());
        wrapper.eq(Attendance::getDate, LocalDate.now());
        Attendance record = attendanceService.getOne(wrapper);
        if (record == null) {
            throw new BusinessException("No clock-in record found for today");
        }
        if (record.getClockOut() != null) {
            throw new BusinessException("Already clocked out today");
        }

        Attendance update = new Attendance();
        update.setId(record.getId());
        update.setClockOut(LocalDateTime.now());
        attendanceService.updateById(update);
        return Result.success();
    }

    private Shop resolveProviderShop(Long userId) {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getProviderId, userId);
        wrapper.orderByDesc(Shop::getCreateTime);
        Shop shop = shopService.getOne(wrapper.last("limit 1"));
        if (shop == null) {
            throw new BusinessException("Shop not found");
        }
        return shop;
    }

    private Employee requireProviderEmployee(Long userId, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException("Employee is required");
        }

        Employee employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new BusinessException("Employee not found");
        }

        Shop shop = resolveProviderShop(userId);
        if (!shop.getId().equals(employee.getShopId())) {
            throw new BusinessException("No permission");
        }
        return employee;
    }
}
