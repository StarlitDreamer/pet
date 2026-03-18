<template>
  <div class="attendance-manage">
    <el-card shadow="hover">
      <template #header>
        <span>&#32771;&#21220;&#31649;&#29702;</span>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="&#26085;&#26399;">
          <el-date-picker
            v-model="filterDate"
            type="date"
            placeholder="&#36873;&#25321;&#26085;&#26399;"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="&#21592;&#24037;">
          <el-select
            v-model="filterEmployee"
            placeholder="&#20840;&#37096;&#21592;&#24037;"
            clearable
            style="width: 180px"
          >
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchAttendance">&#26597;&#35810;</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="20" class="clock-row">
        <el-col :xs="24" :sm="10" :md="8">
          <el-select
            v-model="clockEmployeeId"
            placeholder="&#35831;&#36873;&#25321;&#35201;&#25171;&#21345;&#30340;&#21592;&#24037;"
            style="width: 100%"
          >
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="7" :md="4">
          <el-button
            type="success"
            size="large"
            style="width: 100%"
            :loading="clockInLoading"
            :disabled="!clockEmployeeId"
            @click="handleClockIn"
          >
            <el-icon><Clock /></el-icon>
            &#19978;&#29677;&#25171;&#21345;
          </el-button>
        </el-col>
        <el-col :xs="12" :sm="7" :md="4">
          <el-button
            type="warning"
            size="large"
            style="width: 100%"
            :loading="clockOutLoading"
            :disabled="!clockEmployeeId"
            @click="handleClockOut"
          >
            <el-icon><Clock /></el-icon>
            &#19979;&#29677;&#25171;&#21345;
          </el-button>
        </el-col>
      </el-row>

      <el-table :data="attendanceList" v-loading="loading" stripe>
        <el-table-column prop="employeeName" label="&#21592;&#24037;" width="120" />
        <el-table-column prop="date" label="&#26085;&#26399;" width="120" />
        <el-table-column prop="clockInTime" label="&#19978;&#29677;&#26102;&#38388;" width="100" />
        <el-table-column prop="clockOutTime" label="&#19979;&#29677;&#26102;&#38388;" width="100" />
        <el-table-column prop="duration" label="&#24037;&#20316;&#26102;&#38271;" width="110" />
        <el-table-column label="&#29366;&#24577;" width="100">
          <template #default="{ row }">
            <el-tag :type="attendanceStatusType(row.status)" size="small">
              {{ attendanceStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="&#22791;&#27880;" min-width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAttendanceList, clockIn, clockOut } from '@/api/attendance'
import { getEmployees } from '@/api/employee'

const loading = ref(false)
const clockInLoading = ref(false)
const clockOutLoading = ref(false)
const filterDate = ref('')
const filterEmployee = ref('')
const clockEmployeeId = ref('')
const employees = ref([])
const attendanceList = ref([])

const attendanceStatusMap = {
  0: { text: '\u672a\u6253\u5361', type: 'info' },
  1: { text: '\u6b63\u5e38', type: 'success' },
  2: { text: '\u8fdf\u5230', type: 'warning' },
  3: { text: '\u65e9\u9000', type: 'warning' },
  4: { text: '\u7f3a\u52e4', type: 'danger' },
  5: { text: '\u8bf7\u5047', type: 'info' }
}

const attendanceStatusText = status => attendanceStatusMap[status]?.text || '\u6b63\u5e38'
const attendanceStatusType = status => attendanceStatusMap[status]?.type || 'success'

const today = () => new Date().toISOString().split('T')[0]

const findEmployeeName = employeeId => {
  const employee = employees.value.find(item => item.id === employeeId)
  return employee?.name || `\u5458\u5de5#${employeeId}`
}

const formatTime = value => {
  if (!value) {
    return '-'
  }
  const time = value.split('T')[1]
  return time ? time.slice(0, 5) : value
}

const formatDuration = (clockInTime, clockOutTime) => {
  if (!clockInTime || !clockOutTime) {
    return '-'
  }

  const start = new Date(clockInTime)
  const end = new Date(clockOutTime)
  const diffMinutes = Math.max(0, Math.floor((end - start) / 60000))
  const hours = Math.floor(diffMinutes / 60)
  const minutes = diffMinutes % 60

  if (hours && minutes) {
    return `${hours}\u5c0f\u65f6${minutes}\u5206\u949f`
  }
  if (hours) {
    return `${hours}\u5c0f\u65f6`
  }
  return `${minutes}\u5206\u949f`
}

const normalizeAttendanceItem = item => ({
  ...item,
  employeeName: findEmployeeName(item.employeeId),
  clockInTime: formatTime(item.clockIn),
  clockOutTime: formatTime(item.clockOut),
  duration: formatDuration(item.clockIn, item.clockOut),
  remark: item.remark || '-'
})

const fetchEmployees = async () => {
  try {
    const res = await getEmployees()
    employees.value = (res.data?.records || res.data || []).filter(item => item.status !== 0)

    if (!clockEmployeeId.value && employees.value.length === 1) {
      clockEmployeeId.value = employees.value[0].id
    }
  } catch {
    employees.value = []
  }
}

const fetchAttendance = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterDate.value) {
      params.date = filterDate.value
    }
    if (filterEmployee.value) {
      params.employeeId = filterEmployee.value
    }

    const res = await getAttendanceList(params)
    const records = res.data?.records || res.data || []
    attendanceList.value = records.map(normalizeAttendanceItem)
  } finally {
    loading.value = false
  }
}

const resolveClockEmployeeId = () => clockEmployeeId.value || filterEmployee.value || ''

const handleClockIn = async () => {
  const employeeId = resolveClockEmployeeId()
  if (!employeeId) {
    ElMessage.warning('\u8bf7\u5148\u9009\u62e9\u8981\u6253\u5361\u7684\u5458\u5de5')
    return
  }

  clockInLoading.value = true
  try {
    await clockIn({ employeeId })
    ElMessage.success('\u4e0a\u73ed\u6253\u5361\u6210\u529f')
    await fetchAttendance()
  } finally {
    clockInLoading.value = false
  }
}

const handleClockOut = async () => {
  const employeeId = resolveClockEmployeeId()
  if (!employeeId) {
    ElMessage.warning('\u8bf7\u5148\u9009\u62e9\u8981\u6253\u5361\u7684\u5458\u5de5')
    return
  }

  clockOutLoading.value = true
  try {
    await clockOut({ employeeId })
    ElMessage.success('\u4e0b\u73ed\u6253\u5361\u6210\u529f')
    await fetchAttendance()
  } finally {
    clockOutLoading.value = false
  }
}

watch(filterEmployee, value => {
  if (value) {
    clockEmployeeId.value = value
  }
})

onMounted(async () => {
  filterDate.value = today()
  await fetchEmployees()
  await fetchAttendance()
})
</script>

<style scoped>
.filter-form {
  margin-bottom: 16px;
}

.clock-row {
  margin-bottom: 20px;
}
</style>
