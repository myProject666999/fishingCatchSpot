<template>
  <div class="page-container">
    <div class="page-header">
      <h3>位点分享广场</h3>
      <el-button type="primary" @click="handleShare">
        <el-icon><Share /></el-icon>
        分享钓点
      </el-button>
    </div>

    <div class="filter-bar">
      <el-input v-model="filter.keyword" placeholder="搜索标题/钓点" clearable style="width: 240px" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="8" v-for="item in tableData" :key="item.id" style="margin-bottom: 16px">
        <el-card shadow="hover" class="share-card">
          <div class="card-header">
            <div class="user-info">
              <el-avatar :size="36" style="background: #409EFF">
                {{ (item.username || 'U').charAt(0) }}
              </el-avatar>
              <div class="user-meta">
                <div class="username">{{ item.username || '匿名用户' }}</div>
                <div class="create-time">{{ item.createTime }}</div>
              </div>
            </div>
            <el-tag v-if="item.spotName" type="success" size="small">
              <el-icon><Location /></el-icon>
              {{ item.spotName }}
            </el-tag>
          </div>
          <div class="card-title" @click="handleViewDetail(item)">
            {{ item.title }}
          </div>
          <div class="card-content" v-if="item.content">
            {{ item.content }}
          </div>
          <div class="card-images" v-if="item.images && item.images.length">
            <el-image
              v-for="(img, idx) in item.images.slice(0, 3)"
              :key="idx"
              :src="img"
              fit="cover"
              style="width: 30%; height: 80px; margin-right: 3%; border-radius: 4px"
              :preview-src-list="item.images"
            />
          </div>
          <div class="card-footer">
            <div class="likes">
              <el-button
                size="small"
                :type="item.liked ? 'danger' : 'default'"
                :icon="item.liked ? 'StarFilled' : 'Star'"
                @click="handleLike(item)"
                circle
              />
              <span class="like-count">{{ item.likeCount || 0 }}</span>
            </div>
            <el-button size="small" type="primary" link @click="handleViewDetail(item)">查看详情</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无分享数据" style="padding: 60px 0" />

    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[9, 18, 30]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog v-model="shareDialogVisible" title="分享钓点" width="560px">
      <el-form :model="shareForm" :rules="shareRules" ref="shareFormRef" label-width="90px">
        <el-form-item label="钓点" prop="spotId">
          <el-select v-model="shareForm.spotId" placeholder="选择要分享的钓点" style="width: 100%" filterable>
            <el-option v-for="s in spotOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="shareForm.title" placeholder="分享标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="shareForm.content" type="textarea" :rows="4" placeholder="分享一下你的体验吧" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shareDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitShare">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="分享详情" width="640px">
      <div v-if="currentDetail">
        <div class="detail-header">
          <el-avatar :size="44" style="background: #409EFF">
            {{ (currentDetail.username || 'U').charAt(0) }}
          </el-avatar>
          <div style="margin-left: 12px; flex: 1">
            <div style="font-size: 15px; font-weight: 600; color: #303133">{{ currentDetail.username || '匿名用户' }}</div>
            <div style="font-size: 13px; color: #909399; margin-top: 4px">{{ currentDetail.createTime }}</div>
          </div>
          <el-tag type="success" size="small">
            <el-icon><Location /></el-icon>
            {{ currentDetail.spotName }}
          </el-tag>
        </div>
        <div class="detail-title">{{ currentDetail.title }}</div>
        <div class="detail-content">{{ currentDetail.content }}</div>
        <div v-if="currentDetail.images && currentDetail.images.length" class="detail-images">
          <el-image
            v-for="(img, idx) in currentDetail.images"
            :key="idx"
            :src="img"
            fit="cover"
            :preview-src-list="currentDetail.images"
            :initial-index="idx"
            style="width: 100%; margin-bottom: 12px; border-radius: 6px"
          />
        </div>
        <div class="detail-footer">
          <el-button
            :type="currentDetail.liked ? 'danger' : 'default'"
            @click="handleLike(currentDetail)"
          >
            <el-icon>{{ currentDetail.liked ? 'StarFilled' : 'Star' }}</el-icon>
            <span style="margin-left: 4px">{{ currentDetail.likeCount || 0 }}</span>
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { list, share as shareApi, getDetail, like } from '@/api/share'
import { list as listSpot } from '@/api/spot'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = reactive([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 9,
  total: 0
})

const filter = reactive({
  keyword: ''
})

const spotOptions = reactive([])

const shareDialogVisible = ref(false)
const shareFormRef = ref(null)
const shareForm = reactive({
  spotId: null,
  title: '',
  content: ''
})
const shareRules = {
  spotId: [{ required: true, message: '请选择钓点', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const detailVisible = ref(false)
const currentDetail = ref(null)

function resetFilter() {
  filter.keyword = ''
  pagination.pageNum = 1
  loadData()
}

function loadSpotOptions() {
  listSpot({ pageNum: 1, pageSize: 100 }).then(res => {
    const data = res || {}
    const arr = data.records || data || []
    spotOptions.splice(0, spotOptions.length, ...arr)
  })
}

function loadData() {
  loading.value = true
  const params = {
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  }
  if (filter.keyword) params.keyword = filter.keyword

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

function handleShare() {
  shareForm.spotId = null
  shareForm.title = ''
  shareForm.content = ''
  shareDialogVisible.value = true
  shareFormRef.value?.clearValidate()
}

function handleSubmitShare() {
  shareFormRef.value?.validate(valid => {
    if (!valid) return
    submitLoading.value = true
    shareApi(shareForm).then(() => {
      ElMessage.success('发布成功')
      shareDialogVisible.value = false
      loadData()
    }).finally(() => {
      submitLoading.value = false
    })
  })
}

function handleViewDetail(row) {
  getDetail(row.id).then(res => {
    currentDetail.value = res || row
    detailVisible.value = true
  })
}

function handleLike(item) {
  like(item.id).then(() => {
    item.liked = !item.liked
    item.likeCount = (item.likeCount || 0) + (item.liked ? 1 : -1)
    ElMessage.success(item.liked ? '点赞成功' : '已取消点赞')
  })
}

onMounted(() => {
  loadSpotOptions()
  loadData()
})
</script>

<style lang="scss" scoped>
.share-card {
  height: 100%;

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  .user-info {
    display: flex;
    align-items: center;

    .user-meta {
      margin-left: 10px;

      .username {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }

      .create-time {
        font-size: 12px;
        color: #909399;
        margin-top: 2px;
      }
    }
  }
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  cursor: pointer;
  line-height: 1.4;

  &:hover {
    color: #409EFF;
  }
}

.card-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-images {
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .likes {
    display: flex;
    align-items: center;
    gap: 8px;

    .like-count {
      color: #909399;
      font-size: 14px;
    }
  }
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.detail-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.detail-images {
  margin-bottom: 20px;
}

.detail-footer {
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
