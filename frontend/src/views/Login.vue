<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">渔获钓点管理系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="80px" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="tip">测试账号：fisher001 / 123456</div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleLogin() {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    login(loginForm)
      .then(res => {
        const data = res.data || res
        const userId = data?.userId
        if (!userId) {
          ElMessage.error('用户信息异常，缺少userId')
          return
        }
        localStorage.setItem('userId', userId)
        userStore.setUserInfo({
          userId,
          username: data.username || loginForm.username,
          nickname: data.nickname || data.username || loginForm.username,
          token: data.token || ''
        })
        ElMessage.success('登录成功')
        router.push('/spot')
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 26px;
  font-weight: 600;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 42px;
  font-size: 16px;
}

.tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}
</style>
