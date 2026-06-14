<template>
  <div class="page-container">
    <div class="page-header">
      <h3>钓点管理</h3>
      <el-button type="primary" @click="handleAdd">新增钓点</el-button>
    </div>
    <el-table :data="tableData" border stripe class="data-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="钓点名称" />
      <el-table-column prop="waterType" label="水域类型" width="120" />
      <el-table-column prop="lng" label="经度" width="120" />
      <el-table-column prop="lat" label="纬度" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          <el-button size="small" type="success" link @click="handleFavorite(scope.row)">收藏</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listSpot, deleteSpot, toggleFavorite } from '@/api/spot'

const tableData = reactive([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

function loadData() {
  listSpot({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  }).then(res => {
    const data = res.data || res
    if (data.records) {
      tableData.splice(0, tableData.length, ...data.records)
      pagination.total = data.total
    } else if (Array.isArray(data)) {
      tableData.splice(0, tableData.length, ...data)
      pagination.total = data.length
    }
  }).catch(() => {
  })
}

function handleAdd() {
  ElMessage.info('新增钓点功能开发中')
}

function handleEdit(row) {
  ElMessage.info('编辑钓点功能开发中')
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该钓点吗？', '提示', {
    type: 'warning'
  }).then(() => {
    deleteSpot(row.id).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  }).catch(() => {
  })
}

function handleFavorite(row) {
  toggleFavorite(row.id).then(() => {
    ElMessage.success('操作成功')
  }).catch(() => {
  })
}

function handleSizeChange(size) {
  pagination.pageSize = size
  loadData()
}

function handleCurrentChange(page) {
  pagination.pageNum = page
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.data-table {
  width: 100%;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
