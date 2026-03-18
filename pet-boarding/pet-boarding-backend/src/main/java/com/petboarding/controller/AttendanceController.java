package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Attendance;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/list")
    public Result<List<Attendance>> list(@RequestParam(required = false) Long employeeId,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            wrapper.eq(Attendance::getEmployeeId, employeeId);
        }
        if (startDate != null) {
            wrapper.ge(Attendance::getDate, LocalDate.parse(startDate));
        }
        if (endDate != null) {
            wrapper.le(Attendance::getDate, LocalDate.parse(endDate));
        }
        wrapper.orderByDesc(Attendance::getDate);
        List<Attendance> records = attendanceService.list(wrapper);
        return Result.success(records);
    }

    @PostMapping("/clockin")
    public Result<?> clockIn(@RequestBody Attendance attendance) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, attendance.getEmployeeId());
        wrapper.eq(Attendance::getDate, LocalDate.now());
        if (attendanceService.count(wrapper) > 0) {
            throw new BusinessException("Already clocked in today");
        }
        attendance.setDate(LocalDate.now());
        attendance.setClockIn(LocalDateTime.now());
        attendance.setStatus(1);
        attendanceService.save(attendance);
        return Result.success();
    }

    @PutMapping("/clockout")
    public Result<?> clockOut(@RequestBody Attendance attendance) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, attendance.getEmployeeId());
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
}
