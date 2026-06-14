<template>
  <div class="page-container">
    <div class="page-header">
      <h3>我的收藏</h3>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="favoriteId" label="ID" width="70" align="center" />
        <el-table-column label="钓点信息" min-width="200">
          <template #default="{ row }">
            <div class="spot-info">
              <div class="spot-name">{{ row.spotName }}</div>
              <div class="spot-meta">
                <el-tag size="small">{{ waterTypeMap[row.waterType] || '-' }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="水域类型" width="100" align="center">
          <template #default="{ row }">
            {{ waterTypeMap[row.waterType] || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="位置" min-width="200">
          <template #default="{ row }">
            <div class="location">
              <el-icon><Location /></el-icon>
              <span>{{ row.address || '未知' }}</span>
            </div>
            <div class="coord">
              ({{ formatNumber(row.blurredLng) }}, {{ formatNumber(row.blurredLat) }})
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column label="记录数" width="80" align="center">
          <template #default="{ row }">
            {{ row.totalRecords || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="总重量(kg)" width="110" align="center">
          <template #default="{ row }">
            {{ row.totalWeight || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="收藏时间" width="170" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewSpot(row)">查看</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">取消收藏</el-button>
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
      v-model="detailDialogVisible"
      title="钓点详情"
      width="640px"
      destroy-on-close
    >
      <div v-if="currentSpot" class="detail-container">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="钓点名">
            {{ currentSpot.spotName }}
          </el-descriptions-item>
          <el-descriptions-item label="水域类型">
            {{ waterTypeMap[currentSpot.waterType] || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="经度">
            {{ formatNumber(currentSpot.blurredLng) }}
          </el-descriptions-item>
          <el-descriptions-item label="纬度">
            {{ formatNumber(currentSpot.blurredLat) }}
          </el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">
            {{ currentSpot.address || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ currentSpot.description || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="记录数">
            {{ currentSpot.totalRecords || 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="总重量(kg)">
            {{ currentSpot.totalWeight || 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="创建人">
            {{ currentSpot.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ currentSpot.createTime || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="currentSpot.images && currentSpot.images.length > 0" class="detail-images">
          <h4 style="margin: 20px 0 12px">图片</h4>
          <div class="image-list">
            <el-image
              v-for="(img, idx) in currentSpot.images"
              :key="idx"
              :src="img"
              :preview-src-list="currentSpot.images"
              :initial-index="idx"
              fit="cover"
              class="detail-image"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location } from '@element-plus/icons-vue'
import { list, deleteById } from '@/api/favorite'
import { getDetail as getSpotDetail } from '@/api/spot'

const waterTypeMap = {
  1: '水库',
  2: '湖',
  3: '河',
  4: '海'
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const detailDialogVisible = ref(false)
const currentSpot = ref(null)

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

function formatNumber(val) {
  if (val === null || val === undefined || val === '') return '-'
  return Number(val).toFixed(6)
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

function handleViewSpot(row) {
  loading.value = true
  getSpotDetail(row.spotId)
    .then(res => {
      const data = res.data || res
      currentSpot.value = data
      detailDialogVisible.value = true
    })
    .finally(() => {
      loading.value = false
    })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定取消收藏钓点「${row.spotName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteById(row.favoriteId).then(() => {
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
  .spot-name {
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }
}

.location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 13px;
  margin-bottom: 2px;
}

.coord {
  font-size: 12px;
  color: #909399;
}

.detail-container {
  .detail-images {
    .image-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .detail-image {
      width: 150px;
      height: 150px;
      border-radius: 8px;
    }
  }
}
</style>
