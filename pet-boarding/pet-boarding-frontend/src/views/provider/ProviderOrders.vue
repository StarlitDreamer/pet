<template>
  <div class="provider-orders">
    <el-card shadow="hover">
      <template #header>
        <span>订单管理</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="fetchOrders">
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
        <el-table-column prop="totalAmount" label="金额" width="80">
          <template #default="{ row }">
            <span style="color:#f56c6c">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending_accept'" type="success" link @click="handleAccept(row)">接单</el-button>
            <el-button v-if="row.status === 'pending_accept'" type="danger" link @click="openRejectDialog(row)">拒绝</el-button>
            <el-button v-if="row.status === 'accepted'" type="primary" link @click="handleCheckin(row)">办理入住</el-button>
            <el-button v-if="row.status === 'checked_in'" type="warning" link @click="handleCheckout(row)">办理离店</el-button>
            <el-button v-if="row.status === 'checked_in'" type="success" link @click="$router.push('/provider/care')">添加照料</el-button>
            <el-button v-if="row.status === 'checked_out'" type="primary" link @click="handleComplete(row)">完成订单</el-button>
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
import { getProviderOrders, acceptOrder, rejectOrder, checkinOrder, checkoutOrder, completeOrder } from '@/api/order'
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
  pending_pay: { text: '待支付', type: 'warning' },
  pending_accept: { text: '待接单', type: 'info' },
  accepted: { text: '已接单', type: '' },
  checked_in: { text: '寄养中', type: 'success' },
  checked_out: { text: '已离店', type: '' },
  completed: { text: '已完成', type: 'success' },
  cancelled: { text: '已取消', type: 'danger' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusType = (s) => statusMap[s]?.type || ''

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (activeTab.value !== 'all') params.status = activeTab.value
    const res = await getProviderOrders(params)
    orderList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handlePageChange = (p) => {
  page.value = p
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
    await rejectOrder(rejectOrderId.value, { reason: rejectReason.value })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchOrders()
  } finally {
    rejectLoading.value = false
  }
}

const handleCheckin = (row) => {
  ElMessageBox.confirm('确定办理入住？', '确认', { type: 'info' }).then(async () => {
    await checkinOrder(row.id)
    ElMessage.success('入住成功')
    fetchOrders()
  }).catch(() => {})
}

const handleCheckout = (row) => {
  ElMessageBox.confirm('确定办理离店？', '确认', { type: 'info' }).then(async () => {
    await checkoutOrder(row.id)
    ElMessage.success('离店成功')
    fetchOrders()
  }).catch(() => {})
}

const handleComplete = async (row) => {
  await completeOrder(row.id)
  ElMessage.success('订单已完成')
  fetchOrders()
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
