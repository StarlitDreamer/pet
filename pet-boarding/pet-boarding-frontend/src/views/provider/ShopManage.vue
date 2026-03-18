<template>
  <div class="shop-manage">
    <el-card shadow="hover">
      <template #header>
        <span>店铺信息</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:600px" v-loading="loading">
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="店铺简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入店铺简介" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input v-model="form.district" placeholder="区县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="容纳数量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="1000" />
          <span style="margin-left:8px;color:#909399">只</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSave">
            {{ hasShop ? '保存修改' : '创建店铺' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyShop, addShop, updateShop } from '@/api/shop'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const hasShop = ref(false)

const form = reactive({
  id: '',
  name: '',
  description: '',
  province: '',
  city: '',
  district: '',
  address: '',
  phone: '',
  capacity: 10
})

const rules = {
  name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳数量', trigger: 'blur' }]
}

const fetchShop = async () => {
  loading.value = true
  try {
    const res = await getMyShop()
    const shop = res.data
    if (shop && shop.id) {
      hasShop.value = true
      Object.assign(form, shop)
    }
  } catch {
    hasShop.value = false
  } finally {
    loading.value = false
  }
}

const handleSave = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (hasShop.value) {
        await updateShop(form)
        ElMessage.success('保存成功')
      } else {
        const res = await addShop(form)
        form.id = res.data?.id || ''
        hasShop.value = true
        ElMessage.success('店铺创建成功')
      }
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(fetchShop)
</script>
