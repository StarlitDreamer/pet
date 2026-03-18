package com.petboarding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.Pet;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.PetService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/list")
    public Result<List<Pet>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getOwnerId, userId);
        wrapper.orderByDesc(Pet::getCreateTime);
        List<Pet> pets = petService.list(wrapper);
        return Result.success(pets);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Pet pet, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        pet.setOwnerId(userId);
        petService.save(pet);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Pet pet, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Pet dbPet = petService.getById(pet.getId());
        if (dbPet == null || !dbPet.getOwnerId().equals(userId)) {
            throw new BusinessException("Pet not found or no permission");
        }
        petService.updateById(pet);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Pet pet = petService.getById(id);
        if (pet == null || !pet.getOwnerId().equals(userId)) {
            throw new BusinessException("Pet not found or no permission");
        }
        petService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Pet> detail(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        if (pet == null) {
            throw new BusinessException("Pet not found");
        }
        return Result.success(pet);
    }
}
