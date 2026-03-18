<template>
  <div class="care-view">
    <el-page-header @back="$router.back()" title="返回订单" content="照料记录" />

    <el-card shadow="hover" style="margin-top:20px" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>照料记录时间线</span>
          <el-button type="primary" link @click="fetchRecords">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-empty v-if="records.length === 0" description="暂无照料记录" />

      <el-timeline v-else>
        <el-timeline-item
          v-for="record in records"
          :key="record.id"
          :timestamp="record.createTime"
          placement="top"
          :type="healthColor(record.healthStatus)"
        >
          <el-card shadow="hover">
            <div class="record-header">
              <el-tag size="small" :type="typeTagType(record.type)">{{ typeText(record.type) }}</el-tag>
              <span class="health-tag">
                健康状况：
                <el-tag :type="healthColor(record.healthStatus)" size="small">{{ record.healthStatus || '正常' }}</el-tag>
              </span>
            </div>
            <p class="record-content">{{ record.content }}</p>
            <div class="record-meta" v-if="record.temperature || record.weight">
              <span v-if="record.temperature">体温：{{ record.temperature }}℃</span>
              <span v-if="record.weight">体重：{{ record.weight }}kg</span>
            </div>
            <p class="record-note" v-if="record.healthNote">备注：{{ record.healthNote }}</p>
            <div class="record-photos" v-if="record.photos && record.photos.length">
              <el-image v-for="(photo, idx) in record.photos" :key="idx" :src="photo" style="width:80px;height:80px;margin-right:8px" fit="cover" :preview-src-list="record.photos" />
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCareRecords } from '@/api/care'

const route = useRoute()
const loading = ref(false)
const records = ref([])
let timer = null

const typeMap = {
  feeding: { text: '喂食', type: 'success' },
  walking: { text: '遛弯', type: '' },
  cleaning: { text: '清洁', type: 'info' },
  health_check: { text: '健康检查', type: 'warning' },
  medication: { text: '用药', type: 'danger' },
  other: { text: '其他', type: 'info' }
}

const typeText = (t) => typeMap[t]?.text || t
const typeTagType = (t) => typeMap[t]?.type || ''

const healthColor = (status) => {
  if (!status || status === '正常' || status === 'good') return 'success'
  if (status === '异常' || status === 'bad') return 'danger'
  return 'warning'
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getCareRecords(route.params.orderId)
    records.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecords()
  timer = setInterval(fetchRecords, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.record-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.record-content {
  color: #303133;
  margin: 8px 0;
}
.record-meta {
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 20px;
  margin-bottom: 6px;
}
.record-note {
  color: #e6a23c;
  font-size: 13px;
}
</style>
