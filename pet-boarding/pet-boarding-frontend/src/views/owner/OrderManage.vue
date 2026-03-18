<template>
  <div class="order-manage">
    <el-card shadow="hover">
      <template #header>
        <span>我的订单</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="fetchOrders">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="pending_pay" />
        <el-tab-pane label="待接单" name="pending_accept" />
        <el-tab-pane label="寄养中" name="checked_in" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="shopName" label="店铺" />
        <el-table-column prop="petName" label="宠物" width="100" />
        <el-table-column prop="serviceName" label="服务" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending_pay'" type="primary" link @click="handlePay(row)">支付</el-button>
            <el-button v-if="row.status === 'pending_pay' || row.status === 'pending_accept'" type="warning" link @click="handleCancel(row)">取消</el-button>
            <el-button v-if="row.status === 'checked_in'" type="success" link @click="$router.push(`/owner/care/${row.id}`)">查看照料</el-button>
            <el-button v-if="row.status === 'completed'" type="primary" link @click="openReviewDialog(row)">评价</el-button>
            <el-button type="info" link @click="openDetail(row)">详情</el-button>
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

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="550px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)">{{ statusText(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="店铺">{{ detail.shopName }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{ detail.petName }}</el-descriptions-item>
        <el-descriptions-item label="服务">{{ detail.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="金额">
          <span style="color:#f56c6c">¥{{ detail.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detail.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detail.endDate }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="拒绝原因" :span="2" v-if="detail.rejectReason">{{ detail.rejectReason }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewVisible" title="评价订单" width="500px" destroy-on-close>
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" show-text
            :texts="['很差', '差', '一般', '好', '很好']" />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入评价内容" />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload action="#" list-type="picture-card" :auto-upload="false" :limit="3"
            :on-exceed="() => ElMessage.warning('最多上传3张图片')">
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="handleReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOwnerOrders, getOrderDetail, payOrder, cancelOrder } from '@/api/order'
import { addReview } from '@/api/review'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const reviewLoading = ref(false)
const detailVisible = ref(false)
const reviewVisible = ref(false)
const activeTab = ref('all')
const orderList = ref([])
const detail = ref({})
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const reviewFormRef = ref(null)

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

const reviewForm = reactive({
  orderId: '',
  rating: 5,
  content: ''
})

const reviewRules = {
  rating: [{ required: true, message: '请评分', trigger: 'change' }],
  content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (activeTab.value !== 'all') params.status = activeTab.value
    const res = await getOwnerOrders(params)
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

const handlePay = (row) => {
  ElMessageBox.confirm('确定要支付该订单吗？', '支付确认', { type: 'info' }).then(async () => {
    await payOrder(row.id)
    ElMessage.success('支付成功')
    fetchOrders()
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '取消确认', { type: 'warning' }).then(async () => {
    await cancelOrder(row.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  }).catch(() => {})
}

const openDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    detail.value = res.data || row
  } catch {
    detail.value = row
  }
  detailVisible.value = true
}

const openReviewDialog = (row) => {
  reviewForm.orderId = row.id
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewVisible.value = true
}

const handleReview = () => {
  reviewFormRef.value.validate(async (valid) => {
    if (!valid) return
    reviewLoading.value = true
    try {
      await addReview(reviewForm)
      ElMessage.success('评价成功')
      reviewVisible.value = false
      fetchOrders()
    } finally {
      reviewLoading.value = false
    }
  })
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
