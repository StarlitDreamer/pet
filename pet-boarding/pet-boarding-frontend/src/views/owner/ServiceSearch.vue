<template>
  <div class="service-search">
    <el-card shadow="hover" class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
        </el-form-item>
        <el-form-item label="宠物种类">
          <el-select v-model="searchForm.species" placeholder="请选择" clearable>
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="店铺名称/服务" clearable />
        </el-form-item>
        <el-form-item label="价格范围">
          <el-input-number v-model="searchForm.minPrice" :min="0" placeholder="最低" style="width:100px" controls-position="right" />
          <span style="margin: 0 8px">-</span>
          <el-input-number v-model="searchForm.maxPrice" :min="0" placeholder="最高" style="width:100px" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" v-loading="loading" class="shop-list">
      <el-col :span="8" v-for="shop in shopList" :key="shop.id" class="shop-col">
        <el-card shadow="hover" class="shop-card" @click="openShopDetail(shop)">
          <div class="shop-name">{{ shop.name }}</div>
          <div class="shop-address">
            <el-icon><Location /></el-icon> {{ shop.address }}
          </div>
          <div class="shop-rating">
            <el-rate v-model="shop.rating" disabled show-score text-color="#ff9900" />
          </div>
          <div class="shop-desc">{{ shop.description }}</div>
          <div class="shop-tags">
            <el-tag v-for="svc in (shop.services || []).slice(0, 3)" :key="svc.id" size="small" class="svc-tag">
              {{ svc.name }} ¥{{ svc.price }}/{{ svc.unit || '天' }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && shopList.length === 0" description="暂无搜索结果" />

    <!-- 店铺详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentShop.name" width="650px" destroy-on-close>
      <div class="detail-section">
        <p><strong>地址：</strong>{{ currentShop.address }}</p>
        <p><strong>电话：</strong>{{ currentShop.phone }}</p>
        <p><strong>简介：</strong>{{ currentShop.description }}</p>
        <p><strong>容量：</strong>{{ currentShop.capacity }} 只</p>
        <el-divider />
        <h4>服务项目</h4>
        <el-table :data="currentShop.services || []" stripe>
          <el-table-column prop="name" label="服务名称" />
          <el-table-column prop="species" label="适用种类" width="100">
            <template #default="{ row }">
              {{ { cat: '猫', dog: '狗', all: '全部' }[row.species] || row.species }}
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              <span style="color:#f56c6c">¥{{ row.price }}</span>/{{ row.unit || '天' }}
            </template>
          </el-table-column>
          <el-table-column prop="includes" label="包含内容" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openOrderDialog(row)">立即预约</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 预约弹窗 -->
    <el-dialog v-model="orderVisible" title="创建预约" width="500px" destroy-on-close>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
        <el-form-item label="选择宠物" prop="petId">
          <el-select v-model="orderForm.petId" placeholder="请选择宠物" style="width:100%">
            <el-option v-for="pet in petList" :key="pet.id" :label="pet.name" :value="pet.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="寄养日期" prop="dateRange">
          <el-date-picker v-model="orderForm.dateRange" type="daterange" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="服务项目">
          <el-tag>{{ orderForm.serviceName }}</el-tag>
          <span style="margin-left:10px;color:#f56c6c">¥{{ orderForm.price }}/{{ orderForm.unit || '天' }}</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" type="textarea" placeholder="特殊需求说明" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderVisible = false">取消</el-button>
        <el-button type="primary" :loading="orderLoading" @click="handleCreateOrder">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getShopList, getShopDetail } from '@/api/shop'
import { getPetList } from '@/api/pet'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const orderLoading = ref(false)
const detailVisible = ref(false)
const orderVisible = ref(false)
const shopList = ref([])
const petList = ref([])
const currentShop = ref({})
const orderFormRef = ref(null)

const searchForm = reactive({
  city: '',
  species: '',
  keyword: '',
  minPrice: undefined,
  maxPrice: undefined
})

const orderForm = reactive({
  petId: '',
  dateRange: [],
  serviceId: '',
  serviceName: '',
  shopId: '',
  price: 0,
  unit: '天',
  remark: ''
})

const orderRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择寄养日期', trigger: 'change' }]
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await getShopList(searchForm)
    shopList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openShopDetail = async (shop) => {
  try {
    const res = await getShopDetail(shop.id)
    currentShop.value = res.data || shop
  } catch {
    currentShop.value = shop
  }
  detailVisible.value = true
}

const openOrderDialog = (service) => {
  orderForm.serviceId = service.id
  orderForm.serviceName = service.name
  orderForm.shopId = currentShop.value.id
  orderForm.price = service.price
  orderForm.unit = service.unit || '天'
  orderForm.petId = ''
  orderForm.dateRange = []
  orderForm.remark = ''
  orderVisible.value = true
}

const handleCreateOrder = () => {
  orderFormRef.value.validate(async (valid) => {
    if (!valid) return
    orderLoading.value = true
    try {
      await createOrder({
        petId: orderForm.petId,
        shopId: orderForm.shopId,
        serviceId: orderForm.serviceId,
        startDate: orderForm.dateRange[0],
        endDate: orderForm.dateRange[1],
        remark: orderForm.remark
      })
      ElMessage.success('预约提交成功')
      orderVisible.value = false
      detailVisible.value = false
    } finally {
      orderLoading.value = false
    }
  })
}

const fetchPets = async () => {
  try {
    const res = await getPetList()
    petList.value = res.data?.records || res.data || []
  } catch {
    // ignore
  }
}

onMounted(() => {
  handleSearch()
  fetchPets()
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 20px;
}
.shop-list {
  min-height: 200px;
}
.shop-col {
  margin-bottom: 20px;
}
.shop-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.shop-card:hover {
  transform: translateY(-4px);
}
.shop-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}
.shop-address {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}
.shop-rating {
  margin-bottom: 8px;
}
.shop-desc {
  color: #606266;
  font-size: 13px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.svc-tag {
  margin-right: 6px;
  margin-bottom: 4px;
}
.detail-section p {
  margin: 8px 0;
  color: #606266;
}
</style>
