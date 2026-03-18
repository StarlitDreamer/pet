<template>
  <div class="attendance-manage">
    <el-card shadow="hover">
      <template #header>
        <span>考勤管理</span>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="日期">
          <el-date-picker v-model="filterDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="员工">
          <el-select v-model="filterEmployee" placeholder="全部员工" clearable style="width:160px">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchAttendance">查询</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="20" style="margin-bottom:20px">
        <el-col :span="6">
          <el-button type="success" size="large" style="width:100%" @click="handleClockIn" :loading="clockInLoading">
            <el-icon><Clock /></el-icon> 上班打卡
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" size="large" style="width:100%" @click="handleClockOut" :loading="clockOutLoading">
            <el-icon><Clock /></el-icon> 下班打卡
          </el-button>
        </el-col>
      </el-row>

      <el-table :data="attendanceList" v-loading="loading" stripe>
        <el-table-column prop="employeeName" label="员工" width="100" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="clockInTime" label="上班时间" width="100" />
        <el-table-column prop="clockOutTime" label="下班时间" width="100" />
        <el-table-column prop="duration" label="工作时长" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="attendanceStatusType(row.status)" size="small">{{ attendanceStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAttendanceList, clockIn, clockOut } from '@/api/attendance'
import { getEmployees } from '@/api/employee'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const clockInLoading = ref(false)
const clockOutLoading = ref(false)
const filterDate = ref('')
const filterEmployee = ref('')
const employees = ref([])
const attendanceList = ref([])

const attendanceStatusMap = {
  normal: { text: '正常', type: 'success' },
  late: { text: '迟到', type: 'warning' },
  early: { text: '早退', type: 'warning' },
  absent: { text: '缺勤', type: 'danger' }
}
const attendanceStatusText = (s) => attendanceStatusMap[s]?.text || s || '正常'
const attendanceStatusType = (s) => attendanceStatusMap[s]?.type || 'success'

const fetchEmployees = async () => {
  try {
    const res = await getEmployees()
    employees.value = res.data?.records || res.data || []
  } catch {
    // ignore
  }
}

const fetchAttendance = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterDate.value) params.date = filterDate.value
    if (filterEmployee.value) params.employeeId = filterEmployee.value
    const res = await getAttendanceList(params)
    attendanceList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const handleClockIn = async () => {
  clockInLoading.value = true
  try {
    await clockIn({ date: new Date().toISOString().split('T')[0] })
    ElMessage.success('上班打卡成功')
    fetchAttendance()
  } finally {
    clockInLoading.value = false
  }
}

const handleClockOut = async () => {
  clockOutLoading.value = true
  try {
    await clockOut({ date: new Date().toISOString().split('T')[0] })
    ElMessage.success('下班打卡成功')
    fetchAttendance()
  } finally {
    clockOutLoading.value = false
  }
}

onMounted(() => {
  fetchEmployees()
  filterDate.value = new Date().toISOString().split('T')[0]
  fetchAttendance()
})
</script>

<style scoped>
.filter-form {
  margin-bottom: 16px;
}
</style>
