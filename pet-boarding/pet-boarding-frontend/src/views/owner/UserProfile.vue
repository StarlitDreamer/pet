<template>
  <div class="user-profile">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 个人信息 -->
      <el-tab-pane label="个人信息" name="info">
        <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px" style="max-width:500px">
          <el-form-item label="用户名">
            <el-input v-model="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="实名认证">
            <el-tag v-if="infoForm.verified" type="success">已认证</el-tag>
            <template v-else>
              <el-tag type="warning">未认证</el-tag>
              <el-button type="primary" link style="margin-left:10px" @click="verifyVisible = true">去认证</el-button>
            </template>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">保存修改</el-button>
          </el-form-item>
        </el-form>

        <el-dialog v-model="verifyVisible" title="实名认证" width="400px" destroy-on-close>
          <el-form ref="verifyFormRef" :model="verifyForm" :rules="verifyRules" label-width="80px">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="verifyForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="verifyForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="verifyVisible = false">取消</el-button>
            <el-button type="primary" :loading="verifyLoading" @click="handleVerify">提交认证</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- 优惠券 -->
      <el-tab-pane label="优惠券" name="coupon">
        <el-tabs v-model="couponTab" @tab-change="fetchCoupons">
          <el-tab-pane label="未使用" name="unused" />
          <el-tab-pane label="已使用" name="used" />
          <el-tab-pane label="已过期" name="expired" />
        </el-tabs>
        <el-row :gutter="16" v-loading="couponLoading">
          <el-col :span="8" v-for="coupon in couponList" :key="coupon.id" style="margin-bottom:16px">
            <el-card shadow="hover" :class="['coupon-card', couponTab !== 'unused' ? 'coupon-disabled' : '']">
              <div class="coupon-value">
                <span v-if="coupon.type === 'amount'">¥{{ coupon.value }}</span>
                <span v-else>{{ coupon.value }}折</span>
              </div>
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-condition">满{{ coupon.minAmount ||  }}元可用</div>
              <div class="coupon-time">{{ coupon.startTime }} - {{ coupon.endTime }}</div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-if="!couponLoading && couponList.length === 0" description="暂无优惠券" />
      </el-tab-pane>

      <!-- 积分 -->
      <el-tab-pane label="积分" name="points">
        <div class="points-header">
          <div class="points-balance">
            <span class="label">当前积分：</span>
            <span class="value">{{ pointsBalance }}</span>
          </div>
          <el-button type="primary" @click="exchangeVisible = true">积分兑换</el-button>
        </div>
        <el-table :data="pointsRecords" v-loading="pointsLoading" stripe>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.points > 0 ? 'success' : 'danger'" size="small">
                {{ row.points > 0 ? '获得' : '消费' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="points" label="积分变动" width="100">
            <template #default="{ row }">
              <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c' }">
                {{ row.points > 0 ? '+' : '' }}{{ row.points }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" />
        </el-table>

        <el-dialog v-model="exchangeVisible" title="积分兑换" width="400px" destroy-on-close>
          <el-form label-width="80px">
            <el-form-item label="兑换积分">
              <el-input-number v-model="exchangePoints" :min="100" :max="pointsBalance" :step="100" />
            </el-form-item>
            <el-form-item label="兑换说明">
              <span>{{ exchangePoints }}积分可兑换¥{{ (exchangePoints / 100).toFixed(2) }}优惠券</span>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="exchangeVisible = false">取消</el-button>
            <el-button type="primary" :loading="exchangeLoading" @click="handleExchange">确认兑换</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- 消息中心 -->
      <el-tab-pane label="消息中心" name="message">
        <div class="message-header">
          <el-button type="primary" link @click="handleMarkAllRead" :disabled="unreadCount === 0">
            全部标为已读 ({{ unreadCount }})
          </el-button>
        </div>
        <div v-loading="msgLoading">
          <div v-for="msg in messages" :key="msg.id" :class="['message-item', !msg.isRead ? 'unread' : '']" @click="handleReadMessage(msg)">
            <div class="msg-header">
              <el-badge is-dot :hidden="msg.isRead" class="msg-dot">
                <span class="msg-title">{{ msg.title }}</span>
              </el-badge>
              <span class="msg-time">{{ msg.createTime }}</span>
            </div>
            <p class="msg-content">{{ msg.content }}</p>
          </div>
          <el-empty v-if="!msgLoading && messages.length === 0" description="暂无消息" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store'
import { updateUser, verify } from '@/api/user'
import { getMyCoupons } from '@/api/coupon'
import { getPointsRecords, exchangePoints as exchangePointsApi } from '@/api/points'
import { getMessages, getUnreadCount, markRead, markAllRead } from '@/api/message'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')

// === 个人信息 ===
const infoFormRef = ref(null)
const infoLoading = ref(false)
const verifyVisible = ref(false)
const verifyLoading = ref(false)
const verifyFormRef = ref(null)

const infoForm = reactive({
  username: '',
  phone: '',
  email: '',
  nickname: '',
  verified: false
})

const infoRules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const verifyForm = reactive({ realName: '', idCard: '' })
const verifyRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }]
}

const handleUpdateInfo = () => {
  infoFormRef.value.validate(async (valid) => {
    if (!valid) return
    infoLoading.value = true
    try {
      await updateUser(infoForm)
      ElMessage.success('保存成功')
      userStore.getUserInfo()
    } finally {
      infoLoading.value = false
    }
  })
}

const handleVerify = () => {
  verifyFormRef.value.validate(async (valid) => {
    if (!valid) return
    verifyLoading.value = true
    try {
      await verify(verifyForm)
      ElMessage.success('认证提交成功')
      verifyVisible.value = false
      infoForm.verified = true
    } finally {
      verifyLoading.value = false
    }
  })
}

// === 优惠券 ===
const couponTab = ref('unused')
const couponLoading = ref(false)
const couponList = ref([])

const fetchCoupons = async () => {
  couponLoading.value = true
  try {
    const res = await getMyCoupons({ status: couponTab.value })
    couponList.value = res.data?.records || res.data || []
  } finally {
    couponLoading.value = false
  }
}

// === 积分 ===
const pointsLoading = ref(false)
const pointsBalance = ref(0)
const pointsRecords = ref([])
const exchangeVisible = ref(false)
const exchangeLoading = ref(false)
const exchangePointsVal = ref(100)
// Rename to avoid conflict
const exchangePoints = exchangePointsVal

const fetchPoints = async () => {
  pointsLoading.value = true
  try {
    const res = await getPointsRecords()
    pointsRecords.value = res.data?.records || res.data || []
    pointsBalance.value = res.data?.balance || 
  } finally {
    pointsLoading.value = false
  }
}

const handleExchange = async () => {
  exchangeLoading.value = true
  try {
    await exchangePointsApi({ points: exchangePointsVal.value })
    ElMessage.success('兑换成功')
    exchangeVisible.value = false
    fetchPoints()
  } finally {
    exchangeLoading.value = false
  }
}

// === 消息 ===
const msgLoading = ref(false)
const messages = ref([])
const unreadCount = ref(0)

const fetchMessages = async () => {
  msgLoading.value = true
  try {
    const [msgRes, countRes] = await Promise.allSettled([
      getMessages(),
      getUnreadCount()
    ])
    if (msgRes.status === 'fulfilled') messages.value = msgRes.value.data?.records || msgRes.value.data || []
    if (countRes.status === 'fulfilled') unreadCount.value = countRes.value.data || 
  } finally {
    msgLoading.value = false
  }
}

const handleReadMessage = async (msg) => {
  if (!msg.isRead) {
    await markRead(msg.id)
    msg.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const handleMarkAllRead = async () => {
  await markAllRead()
  messages.value.forEach(m => m.isRead = true)
  unreadCount.value = 0
  ElMessage.success('已全部标为已读')
}

// === 初始化 ===
onMounted(async () => {
  const user = userStore.userInfo || {}
  Object.assign(infoForm, {
    username: user.username || '',
    phone: user.phone || '',
    email: user.email || '',
    nickname: user.nickname || '',
    verified: user.verified || false
  })
  fetchCoupons()
  fetchPoints()
  fetchMessages()
})
</script>

<style scoped>
.coupon-card {
  text-align: center;
  border-left: 4px solid #409eff;
}
.coupon-disabled {
  opacity: 0.5;
  border-left-color: #c0c4cc;
}
.coupon-value {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}
.coupon-name {
  font-size: 14px;
  font-weight: bold;
  margin: 6px 0;
}
.coupon-condition, .coupon-time {
  font-size: 12px;
  color: #909399;
}
.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.points-balance .label {
  font-size: 14px;
  color: #606266;
}
.points-balance .value {
  font-size: 28px;
  font-weight: bold;
  color: #e6a23c;
}
.message-header {
  margin-bottom: 16px;
  text-align: right;
}
.message-item {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.2s;
}
.message-item:hover {
  background: #f5f7fa;
}
.message-item.unread {
  background: #ecf5ff;
}
.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.msg-title {
  font-weight: bold;
  color: #303133;
}
.msg-time {
  font-size: 12px;
  color: #909399;
}
.msg-content {
  color: #606266;
  font-size: 13px;
  margin: 6px 0 0 0;
}
</style>
