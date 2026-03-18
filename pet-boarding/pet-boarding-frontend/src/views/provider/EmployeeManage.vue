<template>
  <div class="employee-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>员工管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 添加员工
          </el-button>
        </div>
      </template>

      <el-table :data="employeeList" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="entryDate" label="入职日期" width="110" />
        <el-table-column prop="salary" label="薪资" width="90">
          <template #default="{ row }">
            ¥{{ row.salary }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '添加员工'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="form.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker v-model="form.entryDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="薪资" prop="salary">
          <el-input-number v-model="form.salary" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getEmployees, addEmployee, updateEmployee, deleteEmployee } from '@/api/employee'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const employeeList = ref([])
const formRef = ref(null)

const defaultForm = {
  name: '', phone: '', position: '',
  idCard: '', entryDate: '', salary: 0
}
const form = reactive({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }]
}

const fetchEmployees = async () => {
  loading.value = true
  try {
    const res = await getEmployees()
    employeeList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, defaultForm)
  }
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateEmployee(form)
        ElMessage.success('更新成功')
      } else {
        await addEmployee(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchEmployees()
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该员工吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteEmployee(row.id)
    ElMessage.success('删除成功')
    fetchEmployees()
  }).catch(() => {})
}

onMounted(fetchEmployees)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
