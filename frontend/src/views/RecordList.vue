<template>
  <div class="page-container">
    <div class="page-header">
      <h3>渔获记录</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增记录
      </el-button>
    </div>

    <div class="filter-bar">
      <el-select v-model="filter.spotId" placeholder="选择钓点" clearable style="width: 200px">
        <el-option v-for="s in spotOptions" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-table :data="tableData" border stripe class="data-table" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="spotName" label="钓点" width="140" />
      <el-table-column prop="fishingDate" label="垂钓日期" width="120" />
      <el-table-column prop="fishingMethod" label="钓法" width="100" />
      <el-table-column prop="weather" label="天气" width="100" />
      <el-table-column prop="temperature" label="温度(℃)" width="100" />
      <el-table-column prop="bait" label="饵料" width="140" show-overflow-tooltip />
      <el-table-column prop="totalCount" label="总尾数" width="90" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
          <el-button size="small" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog v-model="dialogVisible" title="新增渔获记录" width="640px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="关联钓点" prop="spotId">
          <el-select v-model="formData.spotId" placeholder="请选择钓点" style="width: 100%">
            <el-option v-for="s in spotOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="垂钓日期" prop="fishingDate">
          <el-date-picker v-model="formData.fishingDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="钓法" prop="fishingMethod">
              <el-select v-model="formData.fishingMethod" placeholder="请选择" style="width: 100%">
                <el-option label="台钓" value="台钓" />
                <el-option label="路亚" value="路亚" />
                <el-option label="海竿" value="海竿" />
                <el-option label="筏钓" value="筏钓" />
                <el-option label="矶钓" value="矶钓" />
                <el-option label="传统钓" value="传统钓" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="天气" prop="weather">
              <el-select v-model="formData.weather" placeholder="请选择" style="width: 100%">
                <el-option label="晴" value="晴" />
                <el-option label="多云" value="多云" />
                <el-option label="阴" value="阴" />
                <el-option label="小雨" value="小雨" />
                <el-option label="中雨" value="中雨" />
                <el-option label="雷阵雨" value="雷阵雨" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="温度(℃)">
              <el-input v-model="formData.temperature" placeholder="例：25" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="气压(hPa)">
              <el-input v-model="formData.pressure" placeholder="例：1013" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="饵料">
          <el-input v-model="formData.bait" placeholder="例：老三样" />
        </el-form-item>
        <el-form-item label="鱼种明细">
          <div style="width: 100%">
            <el-table :data="formData.catchDetails" border size="small">
              <el-table-column label="鱼种">
                <template #default="scope">
                  <el-select v-model="scope.row.speciesId" placeholder="选择" style="width: 100%">
                    <el-option v-for="sp in speciesOptions" :key="sp.id" :label="sp.name" :value="sp.id" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="数量(尾)" width="140">
                <template #default="scope">
                  <el-input-number v-model="scope.row.count" :min="1" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="重量(kg)" width="140">
                <template #default="scope">
                  <el-input-number v-model="scope.row.weight" :precision="2" :step="0.1" :min="0" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="scope">
                  <el-button size="small" type="danger" link @click="removeCatchDetail(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button size="small" style="margin-top: 8px" @click="addCatchDetail">
              <el-icon><Plus /></el-icon>添加明细
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="渔获记录详情" width="560px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="钓点">{{ currentDetail.spotName }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentDetail.fishingDate }}</el-descriptions-item>
        <el-descriptions-item label="钓法">{{ currentDetail.fishingMethod }}</el-descriptions-item>
        <el-descriptions-item label="天气">{{ currentDetail.weather }}</el-descriptions-item>
        <el-descriptions-item label="温度">{{ currentDetail.temperature }}℃</el-descriptions-item>
        <el-descriptions-item label="饵料">{{ currentDetail.bait }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentDetail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentDetail && currentDetail.catchDetails && currentDetail.catchDetails.length" style="margin-top: 20px">
        <div style="font-weight: 600; margin-bottom: 10px">鱼种明细</div>
        <el-table :data="currentDetail.catchDetails" border size="small">
          <el-table-column prop="speciesName" label="鱼种" />
          <el-table-column prop="count" label="数量(尾)" width="120" />
          <el-table-column prop="weight" label="重量(kg)" width="120" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { list, add, deleteById, getDetail } from '@/api/record'
import { list as listSpot } from '@/api/spot'
import { listAll as listSpecies } from '@/api/species'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = reactive([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const filter = reactive({
  spotId: null
})

const spotOptions = reactive([])
const speciesOptions = reactive([])

const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const currentDetail = ref(null)
const formData = reactive({
  spotId: null,
  fishingDate: '',
  fishingMethod: '',
  weather: '',
  temperature: '',
  pressure: '',
  bait: '',
  remark: '',
  catchDetails: []
})

const formRules = {
  spotId: [{ required: true, message: '请选择钓点', trigger: 'change' }],
  fishingDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  fishingMethod: [{ required: true, message: '请选择钓法', trigger: 'change' }],
  weather: [{ required: true, message: '请选择天气', trigger: 'change' }]
}

function resetForm() {
  formData.spotId = null
  formData.fishingDate = ''
  formData.fishingMethod = ''
  formData.weather = ''
  formData.temperature = ''
  formData.pressure = ''
  formData.bait = ''
  formData.remark = ''
  formData.catchDetails = []
  formRef.value?.clearValidate()
}

function resetFilter() {
  filter.spotId = null
  pagination.pageNum = 1
  loadData()
}

function addCatchDetail() {
  formData.catchDetails.push({
    speciesId: null,
    count: 1,
    weight: 0
  })
}

function removeCatchDetail(index) {
  formData.catchDetails.splice(index, 1)
}

function loadSpotOptions() {
  listSpot({ pageNum: 1, pageSize: 100 }).then(res => {
    const data = res || {}
    const arr = data.records || data || []
    spotOptions.splice(0, spotOptions.length, ...arr)
  })
}

function loadSpeciesOptions() {
  listSpecies().then(res => {
    const arr = res || []
    speciesOptions.splice(0, speciesOptions.length, ...arr)
  })
}

function loadData() {
  loading.value = true
  const params = {
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  }
  if (filter.spotId) params.spotId = filter.spotId
  list(params).then(res => {
    const data = res || {}
    if (data.records) {
      tableData.splice(0, tableData.length, ...data.records)
      pagination.total = data.total || 0
    } else if (Array.isArray(data)) {
      tableData.splice(0, tableData.length, ...data)
      pagination.total = data.length
    }
  }).finally(() => {
    loading.value = false
  })
}

function handleAdd() {
  resetForm()
  if (!formData.catchDetails.length) addCatchDetail()
  dialogVisible.value = true
}

function handleViewDetail(row) {
  getDetail(row.id).then(res => {
    currentDetail.value = res || row
    detailVisible.value = true
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' }).then(() => {
    deleteById(row.id).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  }).catch(() => {})
}

function handleSubmit() {
  formRef.value?.validate(valid => {
    if (!valid) return
    submitLoading.value = true
    add(formData).then(() => {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      loadData()
    }).finally(() => {
      submitLoading.value = false
    })
  })
}

onMounted(() => {
  loadSpotOptions()
  loadSpeciesOptions()
  loadData()
})
</script>
