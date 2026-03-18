package com.petboarding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.entity.CareRecord;
import com.petboarding.mapper.CareRecordMapper;
import com.petboarding.service.CareRecordService;
import org.springframework.stereotype.Service;

@Service
public class CareRecordServiceImpl extends ServiceImpl<CareRecordMapper, CareRecord> implements CareRecordService {
}
