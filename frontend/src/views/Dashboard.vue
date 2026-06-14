<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <el-icon class="logo-icon"><LocationFilled /></el-icon>
        <span>渔获钓点系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="layout-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard/spot">
          <el-icon><Location /></el-icon>
          <span>钓点管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/record">
          <el-icon><Document /></el-icon>
          <span>渔获记录</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>季节统计</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/share">
          <el-icon><Share /></el-icon>
          <span>位点分享广场</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/favorite">
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-title">{{ pageTitle }}</div>
        <div class="header-user">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              <span>{{ nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUser, removeUserId } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const userInfo = computed(() => getUser() || {})
const nickname = computed(() => userInfo.value.nickname || userInfo.value.username || '用户')

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '')

function handleCommand(command) {
  if (command === 'logout') {
    removeUserId()
    localStorage.removeItem('user-info')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  background-color: #2b2f3a;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  .logo-icon {
    font-size: 22px;
    color: #409EFF;
  }
}

.layout-menu {
  border-right: none;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.header-user .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
  padding: 0 12px;
  height: 100%;

  &:hover {
    color: #409EFF;
  }
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
