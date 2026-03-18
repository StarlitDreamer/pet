<template>
  <div class="provider-reviews">
    <el-card shadow="hover">
      <template #header>
        <span>店铺评价</span>
      </template>

      <el-empty v-if="!loading && reviews.length === 0" description="暂无评价" />

      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <span class="owner-name">{{ review.ownerName || '匿名用户' }}</span>
          <el-rate v-model="review.rating" disabled size="small" />
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div class="review-reply" v-if="review.reply">
          <el-tag size="small" type="info">我的回复</el-tag>
          <span>{{ review.reply }}</span>
        </div>
        <div class="review-actions" v-else>
          <el-button type="primary" size="small" @click="openReplyDialog(review)">回复</el-button>
        </div>
        <el-divider />
      </div>
    </el-card>

    <el-dialog v-model="replyVisible" title="回复评价" width="450px" destroy-on-close>
      <el-form>
        <el-form-item label="回复内容">
          <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="handleReply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyShop } from '@/api/shop'
import { getShopReviews, replyReview } from '@/api/review'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const replyLoading = ref(false)
const replyVisible = ref(false)
const reviews = ref([])
const replyContent = ref('')
const replyReviewId = ref('')

const fetchReviews = async () => {
  loading.value = true
  try {
    const shopRes = await getMyShop()
    const shopId = shopRes.data?.id
    if (shopId) {
      const res = await getShopReviews(shopId)
      reviews.value = res.data?.records || res.data || []
    }
  } finally {
    loading.value = false
  }
}

const openReplyDialog = (review) => {
  replyReviewId.value = review.id
  replyContent.value = ''
  replyVisible.value = true
}

const handleReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await replyReview(replyReviewId.value, { reply: replyContent.value })
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchReviews()
  } finally {
    replyLoading.value = false
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
.owner-name {
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
.review-actions {
  margin-top: 8px;
}
</style>
