<template>
  <div class="marketing-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 创建优惠券
          </el-button>
        </div>
      </template>

      <el-table :data="couponList" v-loading="loading" stripe>
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type === 'amount' ? '满减' : '折扣' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="面值" width="80">
          <template #default="{ row }">
            {{ row.type === 'amount' ? '¥' + row.value : row.value + '折' }}
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="门槛" width="80">
          <template #default="{ row }">
            ¥{{ row.minAmount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总量" width="60" />
        <el-table-column prop="receivedCount" label="已领" width="60" />
        <el-table-column prop="startTime" label="开始时间" width="110" />
        <el-table-column prop="endTime" label="结束时间" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="couponStatusType(row)" size="small">{{ couponStatusText(row) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建优惠券" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="amount">满减</el-radio>
            <el-radio label="discount">折扣</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面值" prop="value">
              <el-input-number v-model="form.value" :min="0" :precision="form.type === 'discount' ? 1 : 2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门槛金额" prop="minAmount">
              <el-input-number v-model="form.minAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发放数量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" :max="99999" style="width:100%" />
        </el-form-item>
        <el-form-item label="有效期" prop="timeRange">
          <el-date-picker v-model="form.timeRange" type="daterange" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCoupons, addCoupon } from '@/api/coupon'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const couponList = ref([])
const formRef = ref(null)

const defaultForm = {
  name: '', type: 'amount', value: 10,
  minAmount: 0, totalCount: 100, timeRange: []
}
const form = reactive({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入面值', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const couponStatusText = (row) => {
  if (row.endTime && new Date(row.endTime) < new Date()) return '已过期'
  if (row.receivedCount >= row.totalCount) return '已领完'
  return '进行中'
}
const couponStatusType = (row) => {
  if (row.endTime && new Date(row.endTime) < new Date()) return 'info'
  if (row.receivedCount >= row.totalCount) return 'warning'
  return 'success'
}

const fetchCoupons = async () => {
  loading.value = true
  try {
    const res = await getCoupons()
    couponList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  Object.assign(form, defaultForm)
  form.timeRange = []
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await addCoupon({
        name: form.name,
        type: form.type,
        value: form.value,
        minAmount: form.minAmount,
        totalCount: form.totalCount,
        startTime: form.timeRange[0],
        endTime: form.timeRange[1]
      })
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchCoupons()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(fetchCoupons)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
