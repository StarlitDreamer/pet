<template>
  <div class="service-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>服务项目管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 添加服务
          </el-button>
        </div>
      </template>

      <el-table :data="serviceList" v-loading="loading" stripe>
        <el-table-column prop="name" label="服务名称" width="140" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="species" label="适用种类" width="90">
          <template #default="{ row }">
            {{ { cat: '猫', dog: '狗', all: '全部' }[row.species] || row.species }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">
            <span style="color:#f56c6c">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="80">
          <template #default="{ row }">
            <span v-if="row.originalPrice" style="text-decoration:line-through;color:#c0c4cc">¥{{ row.originalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link @click="handleToggle(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务' : '添加服务'" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入服务描述" />
        </el-form-item>
        <el-form-item label="适用种类" prop="species">
          <el-select v-model="form.species" placeholder="请选择" style="width:100%">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="全部" value="all" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="计费单位" prop="unit">
          <el-select v-model="form.unit" style="width:100%">
            <el-option label="天" value="天" />
            <el-option label="次" value="次" />
            <el-option label="小时" value="小时" />
          </el-select>
        </el-form-item>
        <el-form-item label="包含内容" prop="includes">
          <el-input v-model="form.includes" type="textarea" :rows="2" placeholder="如：喂食、遛弯、洗澡等" />
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
import { getServiceList, addService, updateService, toggleServiceStatus } from '@/api/service'
import { getMyShop } from '@/api/shop'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const serviceList = ref([])
const formRef = ref(null)
const shopId = ref('')

const defaultForm = {
  name: '', description: '', species: 'all',
  price: 0, originalPrice: 0, unit: '天', includes: ''
}
const form = reactive({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  species: [{ required: true, message: '请选择适用种类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchServices = async () => {
  if (!shopId.value) return
  loading.value = true
  try {
    const res = await getServiceList(shopId.value)
    serviceList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, defaultForm)
  }
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateService(form)
        ElMessage.success('更新成功')
      } else {
        await addService({ ...form, shopId: shopId.value })
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchServices()
    } finally {
      submitLoading.value = false
    }
  })
}

const handleToggle = async (row) => {
  await toggleServiceStatus(row.id)
  ElMessage.success(row.status === 1 ? '已下架' : '已上架')
  fetchServices()
}

onMounted(async () => {
  try {
    const res = await getMyShop()
    shopId.value = res.data?.id || ''
    if (shopId.value) fetchServices()
    else ElMessage.warning('请先创建店铺')
  } catch {
    ElMessage.warning('请先创建店铺')
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
