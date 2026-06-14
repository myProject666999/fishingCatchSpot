<template>
  <div class="page-container">
    <div class="page-header">
      <h3>季节统计</h3>
      <div class="filter-box">
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY年MM月"
          value-format="YYYY-MM"
          @change="handleMonthChange"
        />
      </div>
    </div>
    <div class="charts-container">
      <div class="chart-card">
        <h4>月度渔获统计</h4>
        <div class="chart-placeholder" ref="chartRef1">
          <el-icon class="placeholder-icon"><DataAnalysis /></el-icon>
          <p>图表区域占位</p>
        </div>
      </div>
      <div class="chart-card">
        <h4>鱼种分布</h4>
        <div class="chart-placeholder" ref="chartRef2">
          <el-icon class="placeholder-icon"><PieChart /></el-icon>
          <p>图表区域占位</p>
        </div>
      </div>
    </div>
    <div class="stats-summary">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总钓点数</div>
            <div class="stat-value">{{ stats.spotCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总记录数</div>
            <div class="stat-value">{{ stats.recordCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总渔获量</div>
            <div class="stat-value">{{ stats.totalWeight || 0 }}g</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">鱼种数量</div>
            <div class="stat-value">{{ stats.speciesCount || 0 }}</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { getSeasonStatistics } from '@/api/record'

const selectedMonth = ref('')
const stats = reactive({
  spotCount: 0,
  recordCount: 0,
  totalWeight: 0,
  speciesCount: 0
})

function handleMonthChange(val) {
  if (val) {
    loadStatistics(val)
  }
}

function loadStatistics(month) {
  getSeasonStatistics({ month }).then(res => {
    const data = res.data || res
    if (data) {
      Object.assign(stats, data)
    }
  }).catch(() => {
  })
}
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

.filter-box {
  display: flex;
  gap: 10px;
}

.charts-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  flex: 1;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 20px;
}

.chart-card h4 {
  margin: 0 0 1