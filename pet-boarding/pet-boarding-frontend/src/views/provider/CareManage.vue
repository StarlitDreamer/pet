<template>
  <div class="care-manage">
    <el-card shadow="hover">
      <template #header>
        <span>照料记录管理</span>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="选择订单">
          <el-select v-model="selectedOrderId" placeholder="请选择订单" style="width:300px" @change="fetchCareRecords" filterable>
            <el-option v-for="order in activeOrders" :key="order.id" :label="`${order.orderNo} - ${order.petName}`" :value="order.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="!selectedOrderId" @click="openCareDialog">
            <el-icon><Plus /></el-icon> 添加记录
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <el-empty v-if="!selectedOrderId" description="请先选择一个订单" />

      <div v-else v-loading="loading">
        <el-timeline v-if="careRecords.length > 0">
          <el-timeline-item v-for="record in careRecords" :key="record.id" :timestamp="record.createTime" placement="top">
            <el-card shadow="hover">
              <div class="record-header">
                <el-tag :type="typeTagType(record.type)" size="small">{{ typeText(record.type) }}</el-tag>
                <el-tag :type="healthColor(record.healthStatus)" size="small">{{ record.healthStatus || '正常' }}</el-tag>
              </div>
              <p>{{ record.content }}</p>
              <div class="record-meta" v-if="record.temperature || record.weight">
                <span v-if="record.temperature">体温：{{ record.temperature }}℃</span>
                <span v-if="record.weight">体重：{{ record.weight }}kg</span>
              </div>
              <p v-if="record.healthNote" style="color:#e6a23c;font-size:13px">备注：{{ record.healthNote }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无照料记录" />
      </div>
    </el-card>

    <el-dialog v-model="careVisible" title="添加照料记录" width="550px" destroy-on-close>
      <el-form ref="careFormRef" :model="careForm" :rules="careRules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="careForm.type" placeholder="请选择" style="width:100%">
            <el-option label="喂食" value="feeding" />
            <el-option label="遛弯" value="walking" />
            <el-option label="清洁" value="cleaning" />
            <el-option label="健康检查" value="health_check" />
            <el-option label="用药" value="medication" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="careForm.content" type="textarea" :rows="3" placeholder="请输入照料内容" />
        </el-form-item>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-radio-group v-model="careForm.healthStatus">
            <el-radio label="正常">正常</el-radio>
            <el-radio label="异常">异常</el-radio>
            <el-radio label="观察中">观察中</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="健康备注" v-if="careForm.healthStatus !== '正常'">
          <el-input v-model="careForm.healthNote" type="textarea" :rows="2" placeholder="请描述异常情况" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体温(℃)">
              <el-input-number v-model="careForm.temperature" :min="35" :max="42" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="careForm.weight" :min="0" :max="200" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="careVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitCare">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProviderOrders } from '@/api/order'
import { getCareRecords, addCareRecord } from '@/api/care'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const careVisible = ref(false)
const selectedOrderId = ref('')
const activeOrders = ref([])
const careRecords = ref([])
const careFormRef = ref(null)

const typeMap = {
  feeding: { text: '喂食', type: 'success' },
  walking: { text: '遛弯', type: '' },
  cleaning: { text: '清洁', type: 'info' },
  health_check: { text: '健康检查', type: 'warning' },
  medication: { text: '用药', type: 'danger' },
  other: { text: '其他', type: 'info' }
}
const typeText = (t) => typeMap[t]?.text || t
const typeTagType = (t) => typeMap[t]?.type || ''
const healthColor = (s) => {
  if (!s || s === '正常') return 'success'
  if (s === '异常') return 'danger'
  return 'warning'
}

const defaultCareForm = {
  type: 'feeding', content: '', healthStatus: '正常',
  healthNote: '', temperature: 38.5, weight: null
}
const careForm = reactive({ ...defaultCareForm })

const careRules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  healthStatus: [{ required: true, message: '请选择健康状况', trigger: 'change' }]
}

const fetchActiveOrders = async () => {
  try {
    const res = await getProviderOrders({ status: 'checked_in', page: 1, size: 100 })
    activeOrders.value = res.data?.records || res.data || []
  } catch {
    // ignore
  }
}

const fetchCareRecords = async () => {
  if (!selectedOrderId.value) return
  loading.value = true
  try {
    const res = await getCareRecords(selectedOrderId.value)
    careRecords.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openCareDialog = () => {
  Object.assign(careForm, defaultCareForm)
  careVisible.value = true
}

const handleSubmitCare = () => {
  careFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await addCareRecord({ ...careForm, orderId: selectedOrderId.value })
      ElMessage.success('记录添加成功')
      careVisible.value = false
      fetchCareRecords()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(fetchActiveOrders)
</script>

<style scoped>
.filter-form {
  margin-bottom: 0;
}
.record-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
.record-meta {
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 20px;
}
</style>
