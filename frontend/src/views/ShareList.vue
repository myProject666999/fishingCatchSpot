<template>
  <div class="page-container">
    <div class="page-header">
      <h3>位点分享广场</h3>
      <el-button type="primary" :icon="Plus" @click="handleAdd">发布分享</el-button>
    </div>

    <el-card shadow="never" style="margin-bottom: 20px">
      <div class="filter-bar">
        <el-form :inline="true" :model="queryForm" @submit.prevent>
          <el-form-item label="关键词">
            <el-input
              v-model="queryForm.keyword"
              placeholder="搜索标题/内容"
              clearable
              style="width: 200px"
              @keyup.enter="loadData"
            />
          </el-form-item>
          <el-form-item label="水域类型">
            <el-select
              v-model="queryForm.waterType"
              placeholder="全部"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="(label, value) in waterTypeMap"
                :key="value"
                :label="label"
                :value="Number(value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="loadData" :loading="loading">搜索</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card
          v-for="item in tableData"
          :key="item.shareId"
          shadow="hover"
          class="share-card"
        >
          <div class="share-header">
            <div class="user-info">
              <el-avatar :size="40" :src="item.avatar">
                {{ item.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-detail">
                <div class="username">{{ item.nickname }}</div>
                <div class="create-time">{{ item.createTime }}</div>
              </div>
            </div>
            <div class="spot-info">
              <el-tag size="small">{{ waterTypeMap[item.waterType] || '-' }}</el-tag>
              <span class="spot-name">{{ item.spotName }}</span>
            </div>
          </div>

          <div class="share-title">{{ item.shareTitle }}</div>
          <div class="share-content">{{ item.shareContent }}</div>

          <div class="share-images" v-if="item.images && item.images.length > 0">
            <el-image
              v-for="(img, idx) in item.images.slice(0, 3)"
              :key="idx"
              :src="img"
              :preview-src-list="item.images"
              :initial-index="idx"
              fit="cover"
              class="share-image"
            />
          </div>

          <div class="share-meta">
            <div class="location">
              <el-icon><Location /></el-icon>
              <span>{{ item.address || '未知位置' }}</span>
              <span class="coord">
                ({{ formatNumber(item.blurredLng) }}, {{ formatNumber(item.blurredLat) }})
              </span>
            </div>
            <div class="actions">
              <el-button
                link
                type="primary"
                size="small"
                :icon="View"
                @click="handleViewDetail(item)"
              >
                详情
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                :icon="item.isLiked ? StarFilled : Star"
                :class="{ 'liked': item.isLiked }"
                @click="handleLike(item)"
              >
                {{ item.likeCount || 0 }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      class="pagination"
      v-model:current-page="queryForm.pageNum"
      v-model:page-size="queryForm.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog
      v-model="addDialogVisible"
      title="发布分享"
      width="560px"
      @close="resetAddForm"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="90px">
        <el-form-item label="钓点" prop="spotId">
          <el-select v-model="addForm.spotId" placeholder="请选择钓点" style="width: 100%" filterable>
            <el-option
              v-for="spot in spotList"
              :key="spot.spotId"
              :label="spot.spotName"
              :value="spot.spotId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="shareTitle">
          <el-input v-model="addForm.shareTitle" placeholder="请输入分享标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="shareContent">
          <el-input v-model="addForm.shareContent" type="textarea" :rows="4" placeholder="请输入分享内容" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
            v-model:file-list="addFileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleAddFileChange"
            :on-remove="handleAddFileRemove"
            :limit="9"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addSubmitLoading" @click="handleAddSubmit">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="分享详情"
      width="640px"
      destroy-on-close
    >
      <div v-if="currentDetail" class="detail-container">
        <div class="share-header">
          <div class="user-info">
            <el-avatar :size="48" :src="currentDetail.avatar">
              {{ currentDetail.nickname?.charAt(0) }}
            </el-avatar>
            <div class="user-detail">
              <div class="username">{{ currentDetail.nickname }}</div>
              <div class="create-time">{{ currentDetail.createTime }}</div>
            </div>
          </div>
          <el-tag>{{ waterTypeMap[currentDetail.waterType] || '-' }}</el-tag>
        </div>

        <h3 class="detail-title">{{ currentDetail.shareTitle }}</h3>
        <div class="detail-spot">
          <el-icon><Location /></el-icon>
          <span>{{ currentDetail.spotName }}</span>
          <span class="coord">
            ({{ formatNumber(currentDetail.blurredLng) }}, {{ formatNumber(currentDetail.blurredLat) }})
          </span>
        </div>
        <div class="detail-address" v-if="currentDetail.address">
          {{ currentDetail.address }}
        </div>

        <div class="detail-content">{{ currentDetail.shareContent }}</div>

        <div class="detail-description" v-if="currentDetail.description">
          <div class="label">钓点描述：</div>
          <div>{{ currentDetail.description }}</div>
        </div>

        <div class="detail-images" v-if="currentDetail.images && currentDetail.images.length > 0">
          <el-image
            v-for="(img, idx) in currentDetail.images"
            :key="idx"
            :src="img"
            :preview-src-list="currentDetail.images"
            :initial-index="idx"
            fit="cover"
            class="detail-image"
          />
        </div>

        <div class="detail-footer">
          <el-button
            type="danger"
            :icon="currentDetail.isLiked ? StarFilled : Star"
            @click="handleDetailLike"
          >
            {{ currentDetail.isLiked ? '已点赞' : '点赞' }} ({{ currentDetail.likeCount || 0 }})
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Star, StarFilled, View, Location } from '@element-plus/icons-vue'
import { list, share, getDetail, like } from '@/api/share'
import { list as spotListApi } from '@/api/spot'

const waterTypeMap = {
  1: '水库',
  2: '湖',
  3: '河',
  4: '海'
}

const loading = ref(false)
const addSubmitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const spotList = ref([])

const addDialogVisible = ref(false)
const addFormRef = ref(null)
const addFileList = ref([])

const detailDialogVisible = ref(false)
const currentDetail = ref(null)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  waterType: null
})

const defaultAddForm = () => ({
  spotId: null,
  shareTitle: '',
  shareContent: '',
  images: []
})

const addForm = reactive(defaultAddForm())

const addRules = {
  spotId: [{ required: true, message: '请选择钓点', trigger: 'change' }],
  shareTitle: [{ required: true, message: '请输入分享标题', trigger: 'blur' }]
}

function formatNumber(val) {
  if (val === null || val === undefined || val === '') return '-'
  return Number(val).toFixed(2)
}

function handleAddFileChange(file) {
  const reader = new FileReader()
  reader.onload = (e) => {
    addForm.images.push(e.target.result)
  }
  reader.readAsDataURL(file.raw)
}

function handleAddFileRemove(file) {
  const index = addForm.images.findIndex(img => img === file.url)
  if (index > -1) {
    addForm.images.splice(index, 1)
  }
}

function loadSpotList() {
  spotListApi({ pageNum: 1, pageSize: 1000 }).then(res => {
    const data = res.data || res
    spotList.value = Array.isArray(data) ? data : (data.records || data.list || [])
  })
}

function loadData() {
  loading.value = true
  const params = {
    pageNum: queryForm.pageNum,
    pageSize: queryForm.pageSize
  }
  if (queryForm.keyword) {
    params.keyword = queryForm.keyword
  }
  if (queryForm.waterType !== null && queryForm.waterType !== undefined) {
    params.waterType = queryForm.waterType
  }
  list(params)
    .then(res => {
      const data = res.data || res
      tableData.value = (data.records || data.list || []).map(item => ({
        ...item,
        isLiked: false
      }))
      total.value = data.total || 0
    })
    .finally(() => {
      loading.value = false
    })
}

function resetQuery() {
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  queryForm.keyword = ''
  queryForm.waterType = null
  loadData()
}

function handleAdd() {
  addDialogVisible.value = true
}

function resetAddForm() {
  Object.assign(addForm, defaultAddForm())
  addFileList.value = []
  addFormRef.value && addFormRef.value.resetFields()
}

function handleAddSubmit() {
  addFormRef.value.validate(valid => {
    if (!valid) return
    addSubmitLoading.value = true
    share(addForm)
      .then(() => {
        ElMessage.success('发布成功')
        addDialogVisible.value = false
        loadData()
      })
      .finally(() => {
        addSubmitLoading.value = false
      })
  })
}

function handleViewDetail(item) {
  loading.value = true
  getDetail(item.shareId)
    .then(res => {
      const data = res.data || res
      currentDetail.value = {
        ...data,
        isLiked: item.isLiked
      }
      detailDialogVisible.value = true
    })
    .finally(() => {
      loading.value = false
    })
}

function handleLike(item) {
  like(item.shareId).then(() => {
    item.isLiked = !item.isLiked
    item.likeCount = (item.likeCount || 0) + (item.isLiked ? 1 : -1)
    ElMessage.success(item.isLiked ? '点赞成功' : '已取消点赞')
  })
}

function handleDetailLike() {
  if (!currentDetail.value) return
  like(currentDetail.value.shareId).then(() => {
    currentDetail.value.isLiked = !currentDetail.value.isLiked
    currentDetail.value.likeCount = (currentDetail.value.likeCount || 0) + (currentDetail.value.isLiked ? 1 : -1)
    const listItem = tableData.value.find(item => item.shareId === currentDetail.value.shareId)
    if (listItem) {
      listItem.isLiked = currentDetail.value.isLiked
      listItem.likeCount = currentDetail.value.likeCount
    }
    ElMessage.success(currentDetail.value.isLiked ? '点赞成功' : '已取消点赞')
  })
}

onMounted(() => {
  loadData()
  loadSpotList()
})
</script>

<style lang="scss" scoped>
.share-card {
  margin-bottom: 20px;

  .share-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-detail {
        .username {
          font-weight: 600;
          color: #333;
        }
        .create-time {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .spot-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .spot-name {
        color: #606266;
        font-size: 14px;
      }
    }
  }

  .share-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }

  .share-content {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .share-images {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;

    .share-image {
      width: 120px;
      height: 120px;
      border-radius: 8px;
    }
  }

  .share-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;

    .location {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #909399;
      font-size: 13px;

      .coord {
        font-size: 12px;
        color: #c0c4cc;
      }
    }

    .actions {
      .liked {
        color: #f56c6c;
      }
    }
  }
}

.detail-container {
  .share-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-detail {
        .username {
          font-weight: 600;
          color: #333;
          font-size: 16px;
        }
        .create-time {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }

  .detail-title {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
  }

  .detail-spot {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #606266;
    margin-bottom: 4px;

    .coord {
      font-size: 12px;
      color: #909399;
      margin-left: 4px;
    }
  }

  .detail-address {
    color: #909399;
    font-size: 13px;
    margin-bottom: 16px;
    padding-left: 20px;
  }

  .detail-content {
    color: #333;
    line-height: 1.8;
    margin-bottom: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
  }

  .detail-description {
    margin-bottom: 16px;
    padding: 12px;
    background: #fafafa;
    border-radius: 8px;

    .label {
      color: #909399;
      font-size: 13px;
      margin-bottom: 4px;
    }
  }

  .detail-images {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 20px;

    .detail-image {
      width: 200px;
      height: 200px;
      border-radius: 8px;
    }
  }

  .detail-footer {
    text-align: center;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
