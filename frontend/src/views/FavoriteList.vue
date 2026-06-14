<template>
  <div class="page-container">
    <div class="page-header">
      <h3>我的收藏</h3>
    </div>

    <div class="filter-bar">
      <el-input v-model="filter.keyword" placeholder="搜索钓点名称" clearable style="width: 240px" />
      <el-select v-model="filter.waterType" placeholder="水域类型" clearable style="width: 160px">
        <el-option label="河流" value="RIVER" />
        <el-option label="湖泊" value="LAKE" />
        <el-option label="水库" value="RESERVOIR" />
        <el-option label="池塘" value="POND" />
        <el-option label="近海" value="SEA" />
        <el-option label="其他" value="OTHER" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-table :data="tableData" border stripe class="data-table" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="钓点信息" min-width="220">
        <template #default="scope">
          <div class="spot-info">
            <div class="spot-name">{{ scope.row.spotName || scope.row.name }}</div>
            <div class="spot-water" v-if="scope.row.waterType">
              <el-tag size="small" type="info">{{ waterTypeLabel(scope.row.waterType) }}</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="lng" label="经度" width="120" />
      <el-table-column prop="lat" label="纬度" width="120" />
      <el-table-column label="描述" min-width="200" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.description || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="收藏时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleViewSpot(scope.row)">查看钓点</el-button>
          <el-button size="small" type="danger" link @click="handleCancelFavorite(scope.row)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无收藏数据" style="padding: 60px 0" />

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

    <el-dialog v-model="spotDetailVisible" title="钓点详情" width="560px">
      <el-descriptions :column="2" border v-if="currentSpot">
        <el-descriptions-item label="钓点名称" :span="2">{{ currentSpot.spotName || currentSpot.name }}</el-descriptions-item>
        <el-descriptions-item label="水域类型">{{ waterTypeLabel(currentSpot.waterType) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentSpot.spotCreateTime || currentSpot.createTime }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ currentSpot.lng }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ currentSpot.lat }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentSpot.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { list, deleteById } from '@/api/favorite'
import { getDetail as getSpotDetail } from '@/api/spot'

const router = useRouter()
const loading = ref(false)
const tableData = reactive([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const filter = reactive({
  keyword: '',
  waterType: null
})

const spotDetailVisible = ref(false)
const currentSpot = ref(null)

const waterTypeMap = {
  RIVER: '河流',
  LAKE: '湖泊',
  RESERVOIR: '水库',
  POND: '池塘',
  SEA: '近海',
  OTHER: '其他'
}

function waterTypeLabel(val) {
  return waterTypeMap[val] || val || '-'
}

function resetFilter() {
  filter.keyword = ''
  filter.waterType = null
  pagination.pageNum = 1
  loadData()
}

function loadData() {
  loading.value = true
  const params = {
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  }
  if (filter.keyword) params.keyword = filter.keyword
  if (filter.waterType) params.waterType = filter.waterType

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

function handleViewSpot(row) {
  const spotId = row.spotId || row.id
  getSpotDetail(spotId).then(res => {
    currentSpot.value = res || row
    spotDetailVisible.value = true
  })
}

function handleCancelFavorite(row) {
  ElMessageBox.confirm(`确定要取消收藏钓点「${row.spotName || row.name}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '取消收藏',
    cancelButtonText: '继续收藏'
  }).then(() => {
    const favId = row.favoriteId || row.id
    deleteById(favId).then(() => {
      ElMessage.success('已取消收藏')
      loadData()
    })
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.spot-info {
  display: flex;
  align-items: center;
  gap: 10px;

  .spot-name {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }
}
</style>
