<template>
  <div class="review-manage">
    <el-card shadow="hover">
      <template #header>
        <span>我的评价</span>
      </template>

      <el-empty v-if="!loading && reviews.length === 0" description="暂无评价" />

      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <span class="shop-name">{{ review.shopName }}</span>
          <el-rate v-model="review.rating" disabled size="small" />
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div class="review-reply" v-if="review.reply">
          <el-tag size="small" type="info">商家回复</el-tag>
          <span>{{ review.reply }}</span>
        </div>
        <el-divider />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOwnerOrders } from '@/api/order'
import { getOrderReview } from '@/api/review'

const loading = ref(false)
const reviews = ref([])

const fetchReviews = async () => {
  loading.value = true
  try {
    const orderRes = await getOwnerOrders({ status: 'completed', page: 1, size: 100 })
    const orders = orderRes.data?.records || orderRes.data || []
    const reviewResults = await Promise.allSettled(
      orders.map(order => getOrderReview(order.id))
    )
    reviews.value = reviewResults
      .filter(r => r.status === 'fulfilled' && r.value.data)
      .map(r => r.value.data)
  } finally {
    loading.value = false
  }
}

onMounted(fetchReviews)
</script>

<style scoped>
.review-item {
  padding: 12px 0;
}
.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.shop-name {
  font-weight: bold;
  color: #303133;
}
.review-time {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}
.review-content {
  color: #606266;
  margin: 8px 0;
}
.review-reply {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}
</style>
