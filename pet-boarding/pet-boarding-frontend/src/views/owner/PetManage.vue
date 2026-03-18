<template>
  <div class="pet-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>&#25105;&#30340;&#23456;&#29289;</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>
            &#28155;&#21152;&#23456;&#29289;
          </el-button>
        </div>
      </template>

      <el-table :data="petList" v-loading="loading" stripe>
        <el-table-column prop="name" label="&#21517;&#31216;" width="120" />
        <el-table-column prop="species" label="&#29289;&#31181;" width="90">
          <template #default="{ row }">
            {{ speciesText(row.species) }}
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="&#21697;&#31181;" width="140" />
        <el-table-column label="&#24615;&#21035;" width="80">
          <template #default="{ row }">
            {{ genderText(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="&#26376;&#40836;" width="80" />
        <el-table-column prop="weight" label="&#20307;&#37325;(kg)" width="100" />
        <el-table-column prop="healthStatus" label="&#20581;&#24247;&#29366;&#20917;" min-width="140" />
        <el-table-column prop="vaccination" label="&#30123;&#33590;&#25509;&#31181;" min-width="160" />
        <el-table-column prop="emergencyContact" label="&#32039;&#24613;&#32852;&#31995;&#20154;" width="120" />
        <el-table-column label="&#25805;&#20316;" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">
              &#32534;&#36753;
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              &#21024;&#38500;
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '&#32534;&#36753;&#23456;&#29289;' : '&#28155;&#21152;&#23456;&#29289;'"
      width="640px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="&#21517;&#31216;" prop="name">
              <el-input
                v-model="form.name"
                placeholder="&#35831;&#36755;&#20837;&#23456;&#29289;&#21517;&#31216;"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="&#29289;&#31181;" prop="species">
              <el-select
                v-model="form.species"
                placeholder="&#35831;&#36873;&#25321;&#29289;&#31181;"
                allow-create
                filterable
                default-first-option
                style="width: 100%"
              >
                <el-option
                  v-for="item in speciesOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="&#21697;&#31181;" prop="breed">
              <el-input
                v-model="form.breed"
                placeholder="&#35831;&#36755;&#20837;&#23456;&#29289;&#21697;&#31181;"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="&#24615;&#21035;" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio v-for="item in genderOptions" :key="item.value" :label="item.value">
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="&#26376;&#40836;" prop="age">
              <el-input-number v-model="form.age" :min="0" :max="360" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="&#20307;&#37325;(kg)" prop="weight">
              <el-input-number v-model="form.weight" :min="0" :max="200" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="&#20581;&#24247;&#29366;&#20917;" prop="healthStatus">
          <el-input
            v-model="form.healthStatus"
            placeholder="&#35831;&#25551;&#36848;&#23456;&#29289;&#20581;&#24247;&#29366;&#20917;"
            maxlength="255"
          />
        </el-form-item>

        <el-form-item label="&#30123;&#33590;&#25509;&#31181;" prop="vaccination">
          <el-input
            v-model="form.vaccination"
            placeholder="&#35831;&#36755;&#20837;&#30123;&#33590;&#25509;&#31181;&#24773;&#20917;"
            maxlength="255"
          />
        </el-form-item>

        <el-form-item label="&#36807;&#25935;&#20449;&#24687;" prop="allergies">
          <el-input
            v-model="form.allergies"
            type="textarea"
            :rows="2"
            placeholder="&#35831;&#36755;&#20837;&#36807;&#25935;&#20449;&#24687;"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="&#29983;&#27963;&#20064;&#24815;" prop="habits">
          <el-input
            v-model="form.habits"
            type="textarea"
            :rows="3"
            placeholder="&#35831;&#36755;&#20837;&#23456;&#29289;&#26085;&#24120;&#20064;&#24815;"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="&#32039;&#24613;&#32852;&#31995;&#20154;" prop="emergencyContact">
              <el-input
                v-model="form.emergencyContact"
                placeholder="&#35831;&#36755;&#20837;&#32039;&#24613;&#32852;&#31995;&#20154;"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="&#32852;&#31995;&#30005;&#35805;" prop="emergencyPhone">
              <el-input
                v-model="form.emergencyPhone"
                placeholder="&#35831;&#36755;&#20837;&#32852;&#31995;&#30005;&#35805;"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">
          &#21462;&#28040;
        </el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          &#30830;&#23450;
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPet, deletePet, getPetList, updatePet } from '@/api/pet'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const petList = ref([])
const formRef = ref(null)

const speciesOptions = [
  { label: '\u72d7', value: '\u72d7' },
  { label: '\u732b', value: '\u732b' },
  { label: '\u5176\u4ed6', value: '\u5176\u4ed6' }
]

const genderOptions = [
  { label: '\u516c', value: 1 },
  { label: '\u6bcd', value: 2 }
]

const genderTextMap = {
  0: '\u672a\u77e5',
  1: '\u516c',
  2: '\u6bcd'
}

const createDefaultForm = () => ({
  id: undefined,
  name: '',
  species: '',
  breed: '',
  gender: 1,
  age: 0,
  weight: null,
  healthStatus: '',
  vaccination: '',
  allergies: '',
  habits: '',
  emergencyContact: '',
  emergencyPhone: ''
})

const form = reactive(createDefaultForm())

const rules = {
  name: [{ required: true, message: '\u8bf7\u8f93\u5165\u5ba0\u7269\u540d\u79f0', trigger: 'blur' }],
  species: [{ required: true, message: '\u8bf7\u9009\u62e9\u6216\u8f93\u5165\u7269\u79cd', trigger: 'change' }],
  gender: [{ required: true, message: '\u8bf7\u9009\u62e9\u6027\u522b', trigger: 'change' }]
}

function normalizeSpecies(value) {
  const species = String(value || '').trim()
  if (species === 'dog') return '\u72d7'
  if (species === 'cat') return '\u732b'
  if (species === 'other') return '\u5176\u4ed6'
  return species
}

function normalizePetForm(source = {}) {
  return {
    id: source.id,
    name: source.name || '',
    species: normalizeSpecies(source.species),
    breed: source.breed || '',
    gender: Number(source.gender ?? 1),
    age: Number(source.age ?? 0),
    weight: source.weight === null || source.weight === undefined ? null : Number(source.weight),
    healthStatus: source.healthStatus || '',
    vaccination: source.vaccination || '',
    allergies: source.allergies || '',
    habits: source.habits || '',
    emergencyContact: source.emergencyContact || '',
    emergencyPhone: source.emergencyPhone || ''
  }
}

function resetForm(source = {}) {
  const nextForm = normalizePetForm(source)

  for (const key of Object.keys(form)) {
    delete form[key]
  }

  Object.assign(form, createDefaultForm(), nextForm)

  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

function buildPetPayload() {
  const payload = {
    name: form.name.trim(),
    species: normalizeSpecies(form.species),
    breed: form.breed.trim(),
    gender: Number(form.gender),
    age: Number(form.age ?? 0),
    weight: form.weight === null || form.weight === undefined ? null : Number(form.weight),
    healthStatus: form.healthStatus.trim(),
    vaccination: form.vaccination.trim(),
    allergies: form.allergies.trim(),
    habits: form.habits.trim(),
    emergencyContact: form.emergencyContact.trim(),
    emergencyPhone: form.emergencyPhone.trim()
  }

  if (isEdit.value) {
    payload.id = form.id
  }

  return payload
}

function speciesText(value) {
  return normalizeSpecies(value) || '-'
}

function genderText(value) {
  return genderTextMap[value] || genderTextMap[0]
}

async function fetchPets() {
  loading.value = true
  try {
    const res = await getPetList()
    petList.value = res.data?.records || res.data || []
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  isEdit.value = !!row
  resetForm(row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) {
    return
  }

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitLoading.value = true
  try {
    const payload = buildPetPayload()

    if (isEdit.value) {
      await updatePet(payload)
      ElMessage.success('\u66f4\u65b0\u6210\u529f')
    } else {
      await addPet(payload)
      ElMessage.success('\u6dfb\u52a0\u6210\u529f')
    }

    dialogVisible.value = false
    await fetchPets()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(
    '\u786e\u5b9a\u8981\u5220\u9664\u8be5\u5ba0\u7269\u4fe1\u606f\u5417\uff1f',
    '\u63d0\u793a',
    { type: 'warning' }
  )
    .then(async () => {
      await deletePet(row.id)
      ElMessage.success('\u5220\u9664\u6210\u529f')
      await fetchPets()
    })
    .catch(() => {})
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
