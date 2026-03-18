<template>
  <div class="provider-home">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="36" color="#409eff"><List /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="36" color="#67c23a"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalRevenue }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="36" color="#e6a23c"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.occupancyRate }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon size="36" color="#909399"><Avatar /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employeeCount }}</div>
              <div class="stat-label">员工数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
              <el-button type="primary" link @click="$router.push('/provider/orders')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" v-loading="loading" stripe>
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="ownerName" label="客户" width="80" />
            <el-table-column prop="petName" label="宠物" width="80" />
            <el-table-column prop="startDate" label="入住日期" width="100" />
            <el-table-column prop="endDate" label="离店日期" width="100" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" style="height:100%">
          <template #header>
            <span>数据概览</span>
          </template>
          <div class="chart-placeholder">
            <el-icon size="60" color="#dcdfe6"><PieChart /></el-icon>
            <p style="color:#909399">图表区域</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProviderOrders } from '@/api/order'
import { getEmployees } from '@/api/employee'

const loading = ref(false)
const stats = reactive({
  todayOrders: 0,
  totalRevenue: 0,
  occupancyRate: 0,
  employeeCount: 0
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
const statusText = (s) => statusMap[s]?.text || s
const statusType = (s) => statusMap[s]?.type || ''

const fetchData = async () => {
  loading.value = true
  try {
    const [orderRes, empRes] = await Promise.allSettled([
      getProviderOrders({ page: 1, size: 5 }),
      getEmployees()
    ])
    if (orderRes.status === 'fulfilled') {
      const raw = orderRes.value.data
      const orders = raw?.records || raw?.list || (Array.isArray(raw) ? raw : [])
      recentOrders.value = orders.slice(0, 5)
      stats.todayOrders = orders.length
      stats.totalRevenue = orders.reduce((sum, o) => sum + (parseFloat(o.payAmount || o.totalAmount) || 0), 0).toFixed(2)
      const activeOrders = orders.filter(o => o.status === 3 || o.status === 4).length
      stats.occupancyRate = orders.length > 0 ? Math.round(activeOrders / Math.max(orders.length, 1) * 100) : 0
    }
    if (empRes.status === 'fulfilled') {
      const raw = empRes.value.data
      const emps = raw?.records || raw?.list || (Array.isArray(raw) ? raw : [])
      stats.employeeCount = emps.length
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  cursor: pointer;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
}
</style>
