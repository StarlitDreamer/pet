<template>
  <div class="pet-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的宠物</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 添加宠物
          </el-button>
        </div>
      </template>

      <el-table :data="petList" v-loading="loading" stripe>
        <el-table-column prop="name" label="名称" width="100" />
        <el-table-column prop="species" label="种类" width="80">
          <template #default="{ row }">
            {{ speciesMap[row.species] || row.species }}
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="品种" width="120" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">
            {{ row.gender === 'male' ? '公' : '母' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="weight" label="体重(kg)" width="90" />
        <el-table-column prop="healthStatus" label="健康状况" width="100" />
        <el-table-column prop="vaccination" label="疫苗接种" width="100" />
        <el-table-column prop="emergencyContact" label="紧急联系人" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑宠物' : '添加宠物'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入宠物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="种类" prop="species">
              <el-select v-model="form.species" placeholder="请选择种类" style="width:100%">
                <el-option label="猫" value="cat" />
                <el-option label="狗" value="dog" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品种" prop="breed">
              <el-input v-model="form.breed" placeholder="请输入品种" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="male">公</el-radio>
                <el-radio label="female">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="0" :max="30" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number v-model="form.weight" :min="0" :max="200" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input v-model="form.healthStatus" placeholder="请描述健康状况" />
        </el-form-item>
        <el-form-item label="疫苗接种" prop="vaccination">
          <el-input v-model="form.vaccination" placeholder="请输入疫苗接种情况" />
        </el-form-item>
        <el-form-item label="过敏信息" prop="allergies">
          <el-input v-model="form.allergies" type="textarea" placeholder="请输入过敏信息" :rows="2" />
        </el-form-item>
        <el-form-item label="生活习惯" prop="habits">
          <el-input v-model="form.habits" type="textarea" placeholder="请输入生活习惯" :rows="2" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { getPetList, addPet, updatePet, deletePet } from '@/api/pet'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const petList = ref([])
const formRef = ref(null)

const speciesMap = { cat: '猫', dog: '狗', other: '其他' }

const defaultForm = {
  name: '', species: '', breed: '', gender: 'male',
  age: 1, weight: 5, healthStatus: '', vaccination: '',
  allergies: '', habits: '', emergencyContact: '', emergencyPhone: ''
}

const form = reactive({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  species: [{ required: true, message: '请选择种类', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const fetchPets = async () => {
  loading.value = true
  try {
    const res = await getPetList()
    petList.value = res.data?.records || res.data || []
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
        await updatePet(form)
        ElMessage.success('更新成功')
      } else {
        await addPet(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchPets()
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该宠物信息吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deletePet(row.id)
    ElMessage.success('删除成功')
    fetchPets()
  }).catch(() => {})
}

onMounted(fetchPets)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
