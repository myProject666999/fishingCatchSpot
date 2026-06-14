<template>
  <div class="page-container">
    <div class="page-header">
      <h3>钓点管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增钓点
      </el-button>
    </div>

    <el-table :data="tableData" border stripe class="data-table" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="钓点名称" min-width="160" />
      <el-table-column prop="waterType" label="水域类型" width="110" />
      <el-table-column prop="lng" label="经度" width="120" />
      <el-table-column prop="lat" label="纬度" width="120" />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="success" link @click="handleToggleFavorite(scope.row)">
            {{ scope.row.favorited ? '取消收藏' : '收藏' }}
          </el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑钓点' : '新增钓点'" width="560px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="钓点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入钓点名称" />
        </el-form-item>
        <el-form-item label="水域类型" prop="waterType">
          <el-select v-model="formData.waterType" placeholder="请选择水域类型" style="width: 100%">
            <el-option label="河流" value="RIVER" />
            <el-option label="湖泊" value="LAKE" />
            <el-option label="水库" value="RESERVOIR" />
            <el-option label="池塘" value="POND" />
            <el-option label="近海" value="SEA" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="经度" prop="lng">
          <el-input-number v-model="formData.lng" :precision="6" :step="0.0001" :min="-180" :max="180" style="width: 100%" />
        </el-form-item>
        <el-form-item label="纬度" prop="lat">
          <el-input-number v-model="formData.lat" :precision="6" :step="0.0001" :min="-90" :max="90" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { list, add, update, deleteById, toggleFavorite } from '@/api/spot'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = reactive([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  name: '',
  waterType: '',
  lng: null,
  lat: null,
  description: ''
})

const formRules = {
  name: [{ required: true, message: '请输入钓点名称', trigger: 'blur' }],
  waterType: [{ required: true, message: '请选择水域类型', trigger: 'change' }],
  lng: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  lat: [{ required: true, message: '请输入纬度', trigger: 'blur' }]
}

function resetForm() {
  formData.id = null
  formData.name = ''
  formData.waterType = ''
  formData.lng = null
  formData.lat = null
  formData.description = ''
  formRef.value?.clearValidate()
}

function loadData() {
  loading.value = true
  list({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  }).then(res => {
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
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  resetForm()
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    waterType: row.waterType,
    lng: row.lng,
    lat: row.lat,
    description: row.description
  })
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除钓点「${row.name}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(() => {
    deleteById(row.id).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  }).catch(() => {})
}

function handleToggleFavorite(row) {
  toggleFavorite(row.id).then(() => {
    ElMessage.success('操作成功')
    loadData()
  })
}

function handleSubmit() {
  formRef.value?.validate(valid => {
    if (!valid) return
    submitLoading.value = true
    const api = isEdit.value ? update : add
    api(formData).then(() => {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }).finally(() => {
      submitLoading.value = false
    })
  })
}

onMounted(() => {
  loadData()
})
</script>
