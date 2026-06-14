<template>
  <div class="page-container">
    <div class="page-header">
      <h3>渔获记录</h3>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增记录</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="recordId" label="ID" width="70" align="center" />
        <el-table-column prop="spotName" label="钓点" min-width="120" />
        <el-table-column prop="fishingDate" label="日期" width="120" align="center" />
        <el-table-column label="时间" width="140" align="center">
          <template #default="{ row }">
            {{ row.startTime || '-' }} ~ {{ row.endTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="钓法" width="90" align="center">
          <template #default="{ row }">
            {{ fishingMethodMap[row.fishingMethod] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bait" label="饵料" width="100" />
        <el-table-column prop="weather" label="天气" width="80" />
        <el-table-column prop="temperature" label="气温" width="80" />
        <el-table-column prop="totalCount" label="总尾数" width="80" align="center" />
        <el-table-column prop="totalWeight" label="总重量(kg)" width="110" align="center" />
        <el-table-column prop="maxWeight" label="最大(kg)" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="720px"
      @close="resetForm"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="钓点" prop="spotId">
              <el-select v-model="form.spotId" placeholder="请选择钓点" style="width: 100%" filterable>
                <el-option
                  v-for="spot in spotList"
                  :key="spot.spotId"
                  :label="spot.spotName"
                  :value="spot.spotId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日期" prop="fishingDate">
              <el-date-picker
                v-model="form.fishingDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                placeholder="选择开始时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="form.endTime"
                placeholder="选择结束时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="钓法" prop="fishingMethod">
              <el-select v-model="form.fishingMethod" placeholder="请选择钓法" style="width: 100%">
                <el-option
                  v-for="(label, value) in fishingMethodMap"
                  :key="value"
                  :label="label"
                  :value="Number(value)"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="饵料" prop="bait">
              <el-input v-model="form.bait" placeholder="请输入饵料" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="天气" prop="weather">
              <el-input v-model="form.weather" placeholder="如：晴" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="气温" prop="temperature">
              <el-input v-model="form.temperature" placeholder="如：25℃" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="风向" prop="windDirection">
              <el-input v-model="form.windDirection" placeholder="如：东风" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="风力" prop="windLevel">
              <el-input v-model="form.windLevel" placeholder="如：3级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="水位" prop="waterLevel">
              <el-input v-model="form.waterLevel" placeholder="如：中" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :limit="9"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-divider content-position="left">渔获明细</el-divider>
        <el-form-item label="鱼种明细">
          <el-button type="primary" link :icon="Plus" @click="addCatchDetail">添加鱼种</el-button>
        </el-form-item>
        <el-table :data="form.catchDetails" border size="small" style="margin-bottom: 20px">
          <el-table-column label="鱼种" min-width="150">
            <template #default="{ row, $index }">
              <el-select v-model="row.speciesId" placeholder="选择鱼种" style="width: 100%" @change="handleSpeciesChange($event, row)">
                <el-option
                  v-for="species in speciesList"
                  :key="species.speciesId"
                  :label="species.speciesName"
                  :value="species.speciesId"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="尾数" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.catchCount" :min="1" :step="1" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="总重量(kg)" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.totalWeight" :min="0" :step="0.1" :precision="2" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="最大(kg)" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.maxWeight" :min="0" :step="0.1" :precision="2" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button link type="danger" size="small" @click="removeCatchDetail($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { list, add, update, deleteById } from '@/api/record'
import { list as spotListApi } from '@/api/spot'
import { list as speciesListApi } from '@/api/species'

const fishingMethodMap = {
  1: '手竿',
  2: '矶竿',
  3: '路亚',
  4: '海钓'
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)
const fileList = ref([])
const spotList = ref([])
const speciesList = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const defaultForm = () => ({
  recordId: null,
  spotId: null,
  fishingDate: '',
  startTime: '',
  endTime: '',
  fishingMethod: null,
  bait: '',
  weather: '',
  temperature: '',
  windDirection: '',
  windLevel: '',
  waterLevel: '',
  remark: '',
  images: [],
  catchDetails: []
})

const defaultCatchDetail = () => ({
  speciesId: null,
  speciesName: '',
  catchCount: 1,
  totalWeight: 0,
  maxWeight: null
})

const form = reactive(defaultForm())

const rules = {
  spotId: [{ required: true, message: '请选择钓点', trigger: 'change' }],
  fishingDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  fishingMethod: [{ required: true, message: '请选择钓法', trigger: 'change' }]
}

function handleFileChange(file) {
  const reader = new FileReader()
  reader.onload = (e) => {
    form.images.push(e.target.result)
  }
  reader.readAsDataURL(file.raw)
}

function handleFileRemove(file) {
  const index = form.images.findIndex(img => img === file.url)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

function handleSpeciesChange(speciesId, row) {
  const species = speciesList.value.find(s => s.speciesId === speciesId)
  if (species) {
    row.speciesName = species.speciesName
  }
}

function addCatchDetail() {
  form.catchDetails.push(defaultCatchDetail())
}

function removeCatchDetail(index) {
  form.catchDetails.splice(index, 1)
}

function loadSpotList() {
  spotListApi({ pageNum: 1, pageSize: 1000 }).then(res => {
    const data = res.data || res
    spotList.value = data.records || data.list || []
  })
}

function loadSpeciesList() {
  speciesListApi().then(res => {
    const data = res.data || res
    speciesList.value = data.records || data.list || []
  })
}

function loadData() {
  loading.value = true
  list({ pageNum: query.pageNum, pageSize: query.pageSize })
    .then(res => {
      const data = res.data || res
      tableData.value = data.records || data.list || []
      total.value = data.total || 0
    })
    .finally(() => {
      loading.value = false
    })
}

function handleAdd() {
  dialogTitle.value = '新增记录'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑记录'
  Object.assign(form, {
    recordId: row.recordId,
    spotId: row.spotId,
    fishingDate: row.fishingDate,
    startTime: row.startTime,
    endTime: row.endTime,
    fishingMethod: row.fishingMethod,
    bait: row.bait,
    weather: row.weather,
    temperature: row.temperature,
    windDirection: row.windDirection,
    windLevel: row.windLevel,
    waterLevel: row.waterLevel,
    remark: row.remark,
    images: row.images ? [...row.images] : [],
    catchDetails: row.catchDetails ? row.catchDetails.map(d => ({ ...d })) : []
  })
  fileList.value = form.images.map((img, idx) => ({
    uid: idx,
    name: `image${idx}`,
    url: img
  }))
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, defaultForm())
  fileList.value = []
  formRef.value && formRef.value.resetFields()
}

function validateCatchDetails() {
  if (form.catchDetails.length === 0) {
    ElMessage.warning('请至少添加一条渔获明细')
    return false
  }
  for (let i = 0; i < form.catchDetails.length; i++) {
    const detail = form.catchDetails[i]
    if (!detail.speciesId) {
      ElMessage.warning(`第${i + 1}条明细请选择鱼种`)
      return false
    }
    if (!detail.catchCount || detail.catchCount <= 0) {
      ElMessage.warning(`第${i + 1}条明细尾数必须大于0`)
      return false
    }
    if (detail.totalWeight === null || detail.totalWeight === undefined || detail.totalWeight < 0) {
      ElMessage.warning(`第${i + 1}条明细请填写总重量`)
      return false
    }
  }
  return true
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (!validateCatchDetails()) return
    submitLoading.value = true
    const submitData = { ...form }
    const api = form.recordId ? update : add
    api(submitData)
      .then(() => {
        ElMessage.success(form.recordId ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除这条渔获记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteById(row.recordId).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadSpotList()
  loadSpeciesList()
})
</script>

<style lang="scss" scoped>
</style>
