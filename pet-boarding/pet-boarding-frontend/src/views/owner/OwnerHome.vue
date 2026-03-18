<template>
  <div class="owner-home">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="40" color="#409eff"><Connection /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.petCount }}</div>
              <div class="stat-label">我的宠物</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="40" color="#67c23a"><List /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeOrders }}</div>
              <div class="stat-label">进行中订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="40" color="#e6a23c"><Bell /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.unreadMessages }}</div>
              <div class="stat-label">未读消息</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-orders" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
          <el-button type="primary" link @click="$router.push('/owner/orders')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="shopName" label="店铺" />
        <el-table-column prop="petName" label="宠物" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPetList } from '@/api/pet'
import { getOwnerOrders } from '@/api/order'
import { getUnreadCount } from '@/api/message'

const loading = ref(false)
const stats = reactive({
  petCount: 0,
  activeOrders: 0,
  unreadMessages: 0
})
const recentOrders = ref([])

const statusMap = {
  pending_pay: { text: '待支付', type: 'warning' },
  pending_accept: { text: '待接单', type: 'info' },
  accepted: { text: '已接单', type: '' },
  checked_in: { text: '寄养中', type: 'success' },
  completed: { text: '已完成', type: 'success' },
  cancelled: { text: '已取消', type: 'danger' }
}

const statusText = (status) => statusMap[status]?.text || status
const statusType = (status) => statusMap[status]?.type || ''

const fetchData = async () => {
  loading.value = true
  try {
    const [petRes, orderRes, msgRes] = await Promise.allSettled([
      getPetList(),
      getOwnerOrders({ page: 1, size: 5 }),
      getUnreadCount()
    ])
    if (petRes.status === 'fulfilled') {
      const pets = petRes.value.data?.records || petRes.value.data || []
      stats.petCount = Array.isArray(pets) ? pets.length : 0
    }
    if (orderRes.status === 'fulfilled') {
      const orders = orderRes.value.data?.records || orderRes.value.data || []
      recentOrders.value = Array.isArray(orders) ? orders.slice(0, 5) : []
      stats.activeOrders = Array.isArray(orders) ? orders.filter(o => ['pending_accept', 'accepted', 'checked_in'].includes(o.status)).length : 0
    }
    if (msgRes.status === 'fulfilled') {
      stats.unreadMessages = msgRes.value.data || 
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.owner-home {
  padding: 0;
}
.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  cursor: pointer;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
