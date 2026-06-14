<template>
  <div class="page-container">
    <div class="page-header">
      <h3>钓点管理</h3>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增钓点</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="spotId" label="ID" width="70" align="center" />
        <el-table-column prop="spotName" label="钓点名" min-width="140" />
        <el-table-column label="水域类型" width="100" align="center">
          <template #default="{ row }">
            {{ waterTypeMap[row.waterType] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="longitude" label="经度" width="110" align="center">
          <template #default="{ row }">
            {{ formatNumber(row.longitude) }}
          </template>
        </el-table-column>
        <el-table-column prop="latitude" label="纬度" width="110" align="center">
          <template #default="{ row }">
            {{ formatNumber(row.latitude) }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="140" show-overflow-tooltip />
        <el-table-column prop="totalRecords" label="记录数" width="80" align="center" />
        <el-table-column prop="totalWeight" label="总重量(kg)" width="110" align="center">
          <template #default="{ row }">
            {{ row.totalWeight || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="收藏" width="70" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.isFavorite === 1" class="star-on"><StarFilled /></el-icon>
            <el-icon v-else class="star-off"><Star /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="分享" width="70" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.isShared === 1" class="share-on"><Share /></el-icon>
            <el-icon v-else class="share-off"><Share /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="handleToggleFavorite(row)">
              {{ row.isFavorite === 1 ? '取消收藏' : '收藏' }}
            </el-button>
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
      width="560px"
      @close="resetForm"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="钓点名" prop="spotName">
          <el-input v-model="form.spotName" placeholder="请输入钓点名" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="水域类型" prop="waterType">
          <el-select v-model="form.waterType" placeholder="请选择水域类型" style="width: 100%">
            <el-option
              v-for="(label, value) in waterTypeMap"
              :key="value"
              :label="label"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.001" :min="-180" :max="180" style="width: 100%" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.001" :min="-90" :max="90" style="width: 100%" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" maxlength="500" show-word-limit />
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
import { Plus, Star, StarFilled, Share } from '@element-plus/icons-vue'
import { list, add, update, deleteById, toggleFavorite } from '@/api/spot'

const waterTypeMap = {
  1: '水库',
  2: '湖',
  3: '河',
  4: '海'
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增钓点')
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)
const fileList = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const defaultForm = () => ({
  spotId: null,
  spotName: '',
  waterType: null,
  longitude: null,
  latitude: null,
  address: '',
  description: '',
  images: []
})

const form = reactive(defaultForm())

const rules = {
  spotName: [{ required: true, message: '请输入钓点名', trigger: 'blur' }],
  waterType: [{ required: true, message: '请选择水域类型', trigger: 'change' }],
  longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }]
}

function formatNumber(val) {
  if (val === null || val === undefined || val === '') return '-'
  return Number(val).toFixed(6)
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
  dialogTitle.value = '新增钓点'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑钓点'
  Object.assign(form, {
    spotId: row.spotId,
    spotName: row.spotName,
    waterType: row.waterType,
    longitude: row.longitude,
    latitude: row.latitude,
    address: row.address,
    description: row.description,
    images: row.images ? [...row.images] : []
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

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    submitLoading.value = true
    const submitData = { ...form }
    const api = form.spotId ? update : add
    api(submitData)
      .then(() => {
        ElMessage.success(form.spotId ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

function handleToggleFavorite(row) {
  toggleFavorite(row.spotId).then(() => {
    ElMessage.success(row.isFavorite === 1 ? '已取消收藏' : '已收藏')
    loadData()
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除钓点「${row.spotName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteById(row.spotId).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.star-on {
  color: #f56c6c;
  font-size: 18px;
}

.star-off {
  color: #c0c4cc;
  font-size: 18px;
}

.share-on {
  color: #409eff;
  font-size: 18px;
}

.share-off {
  color: #c0c4cc;
  font-size: 18px;
}
</style>
