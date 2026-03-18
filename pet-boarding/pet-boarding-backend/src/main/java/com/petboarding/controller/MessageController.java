package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.petboarding.common.Constants;
import com.petboarding.common.Result;
import com.petboarding.entity.Message;
import com.petboarding.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/list")
    public Result<List<Message>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.orderByDesc(Message::getCreateTime);
        List<Message> messages = messageService.list(wrapper);
        return Result.success(messages);
    }

    @GetMapping("/unread-count")
    public Result<?> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.eq(Message::getIsRead, Constants.MESSAGE_UNREAD);
        long count = messageService.count(wrapper);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @PutMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Message message = messageService.getById(id);
        if (message != null && message.getReceiverId().equals(userId)) {
            Message update = new Message();
            update.setId(id);
            update.setIsRead(Constants.MESSAGE_READ);
            messageService.updateById(update);
        }
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<?> readAll(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.eq(Message::getIsRead, Constants.MESSAGE_UNREAD);
        wrapper.set(Message::getIsRead, Constants.MESSAGE_READ);
        messageService.update(wrapper);
        return Result.success();
    }
}
