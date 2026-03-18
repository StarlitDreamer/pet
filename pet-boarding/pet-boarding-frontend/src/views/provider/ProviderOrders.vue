<template>
  <div class="provider-orders">
    <el-card shadow="hover">
      <template #header>
        <span>订单管理</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待接单" name="pending_accept" />
        <el-tab-pane label="已接单" name="accepted" />
        <el-tab-pane label="寄养中" name="checked_in" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="ownerName" label="客户" width="80" />
        <el-table-column prop="petName" label="宠物" width="80" />
        <el-table-column prop="serviceName" label="服务" width="110" />
        <el-table-column prop="startDate" label="入住日期" width="100" />
        <el-table-column prop="endDate" label="离店日期" width="100" />
        <el-table-column prop="totalAmount" label="金额" width="90">
          <template #default="{ row }">
            <span style="color:#f56c6c">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" link @click="handleAccept(row)">接单</el-button>
            <el-button v-if="row.status === 1" type="danger" link @click="openRejectDialog(row)">拒绝</el-button>
            <el-button v-if="row.status === 2" type="primary" link @click="handleCheckin(row)">办理入住</el-button>
            <el-button v-if="[3, 4].includes(row.status)" type="warning" link @click="handleCheckout(row)">办理离店</el-button>
            <el-button v-if="[3, 4].includes(row.status)" type="success" link @click="$router.push('/provider/care')">添加照料</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        class="pagination"
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="rejectVisible" title="拒绝原因" width="400px" destroy-on-close>
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="handleReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProviderOrders, acceptOrder, rejectOrder, checkinOrder, checkoutOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const rejectLoading = ref(false)
const rejectVisible = ref(false)
const activeTab = ref('all')
const orderList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rejectReason = ref('')
const rejectOrderId = ref('')

const statusMap = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '待接单', type: 'info' },
  2: { text: '已接单', type: '' },
  3: { text: '已入住', type: 'success' },
  4: { text: '寄养中', type: 'success' },
  5: { text: '已离店', type: '' },
  6: { text: '已完成', type: 'success' },
  7: { text: '已取消', type: 'danger' },
  8: { text: '已拒绝', type: 'danger' }
}

const statusText = (status) => statusMap[status]?.text || status
const statusType = (status) => statusMap[status]?.type || ''

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    const res = await getProviderOrders(params)
    orderList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  fetchOrders()
}

const handlePageChange = (nextPage) => {
  page.value = nextPage
  fetchOrders()
}

const handleAccept = async (row) => {
  await acceptOrder(row.id)
  ElMessage.success('已接单')
  fetchOrders()
}

const openRejectDialog = (row) => {
  rejectOrderId.value = row.id
  rejectReason.value = ''
  rejectVisible.value = true
}

const handleReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  rejectLoading.value = true
  try {
    await rejectOrder(rejectOrderId.value, { rejectReason: rejectReason.value })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchOrders()
  } finally {
    rejectLoading.value = false
  }
}

const handleCheckin = (row) => {
  ElMessageBox.confirm('确定办理入住吗？', '确认', { type: 'info' }).then(async () => {
    await checkinOrder(row.id)
    ElMessage.success('入住成功')
    fetchOrders()
  }).catch(() => {})
}

const handleCheckout = (row) => {
  ElMessageBox.confirm('确定办理离店吗？', '确认', { type: 'info' }).then(async () => {
    await checkoutOrder(row.id)
    ElMessage.success('离店成功')
    fetchOrders()
  }).catch(() => {})
}

onMounted(fetchOrders)
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
