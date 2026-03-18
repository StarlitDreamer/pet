<template>
  <div class="complaint-manage">
    <el-card shadow="hover">
      <template #header>
        <span>投诉处理</span>
      </template>

      <el-tabs v-model="statusTab" @tab-change="fetchComplaints">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="处理中" name="processing" />
        <el-tab-pane label="已解决" name="resolved" />
      </el-tabs>

      <el-table :data="complaints" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="ownerName" label="投诉人" width="90" />
        <el-table-column prop="content" label="投诉内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="投诉时间" width="160" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="complaintStatusType(row.status)" size="small">{{ complaintStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending' || row.status === 'processing'" type="primary" link @click="openReplyDialog(row)">回复/申诉</el-button>
            <el-button v-if="row.status === 'processing'" type="success" link @click="handleResolve(row)">标记解决</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="replyVisible" title="回复/申诉" width="500px" destroy-on-close>
      <div class="complaint-detail">
        <p><strong>投诉内容：</strong>{{ currentComplaint.content }}</p>
        <el-divider />
        <div v-if="currentComplaint.replies && currentComplaint.replies.length" class="reply-list">
          <div v-for="(reply, idx) in currentComplaint.replies" :key="idx" class="reply-item">
            <el-tag size="small" :type="reply.role === 'provider' ? 'primary' : 'warning'">
              {{ reply.role === 'provider' ? '商家' : '客户' }}
            </el-tag>
            <span>{{ reply.content }}</span>
            <span class="reply-time">{{ reply.createTime }}</span>
          </div>
        </div>
      </div>
      <el-form>
        <el-form-item label="回复内容">
          <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复或申诉内容" />
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
import { getComplaints, replyComplaint, resolveComplaint } from '@/api/complaint'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const replyLoading = ref(false)
const replyVisible = ref(false)
const statusTab = ref('all')
const complaints = ref([])
const currentComplaint = ref({})
const replyContent = ref('')

const complaintStatusMap = {
  pending: { text: '待处理', type: 'warning' },
  processing: { text: '处理中', type: '' },
  resolved: { text: '已解决', type: 'success' }
}
const complaintStatusText = (s) => complaintStatusMap[s]?.text || s
const complaintStatusType = (s) => complaintStatusMap[s]?.type || ''

const fetchComplaints = async () => {
  loading.value = true
  try {
    const params = {}
    if (statusTab.value !== 'all') params.status = statusTab.value
    const res = await getComplaints(params)
    complaints.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openReplyDialog = (row) => {
  currentComplaint.value = row
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
    await replyComplaint(currentComplaint.value.id, { content: replyContent.value })
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchComplaints()
  } finally {
    replyLoading.value = false
  }
}

const handleResolve = (row) => {
  ElMessageBox.confirm('确定标记该投诉为已解决？', '确认', { type: 'info' }).then(async () => {
    await resolveComplaint(row.id)
    ElMessage.success('已标记为解决')
    fetchComplaints()
  }).catch(() => {})
}

onMounted(fetchComplaints)
</script>

<style scoped>
.complaint-detail p {
  color: #606266;
}
.reply-list {
  margin-bottom: 16px;
}
.reply-item {
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
}
.reply-time {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}
</style>
