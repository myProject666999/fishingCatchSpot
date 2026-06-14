<template>
  <div class="page-container">
    <div class="page-header">
      <h3>季节统计</h3>
    </div>

    <el-card shadow="never" style="margin-bottom: 20px">
      <div class="filter-bar">
        <el-form :inline="true" :model="queryForm" @submit.prevent>
          <el-form-item label="开始月份">
            <el-date-picker
              v-model="queryForm.startMonth"
              type="month"
              placeholder="选择开始月份"
              value-format="YYYY-MM"
              style="width: 180px"
            />
          </el-form-item>
          <el-form-item label="结束月份">
            <el-date-picker
              v-model="queryForm.endMonth"
              type="month"
              placeholder="选择结束月份"
              value-format="YYYY-MM"
              style="width: 180px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="loadData" :loading="loading">查询</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="never">
      <div ref="chartRef" class="chart-container"></div>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <h4 style="margin-bottom: 16px">统计明细</h4>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="month" label="月份" width="120" align="center" />
        <el-table-column prop="totalCount" label="总尾数" width="100" align="center" />
        <el-table-column prop="totalWeight" label="总重量(kg)" width="120" align="center" />
        <el-table-column label="鱼种明细">
          <template #default="{ row }">
            <el-tag
              v-for="species in row.speciesStats"
              :key="species.speciesId"
              style="margin-right: 8px; margin-bottom: 4px"
            >
              {{ species.speciesName }}: {{ species.totalCount }}尾({{ species.totalWeight }}kg)
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Search, Refresh } from '@element-plus/icons-vue'
import { statistics } from '@/api/record'

const loading = ref(false)
const chartRef = ref(null)
let chartInstance = null

const queryForm = reactive({
  startMonth: '',
  endMonth: ''
})

const tableData = ref([])

function getDefaultMonth() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

function initDefaultQuery() {
  const now = new Date()
  const endMonth = getDefaultMonth()
  const startYear = now.getFullYear()
  const startMonth = String(Math.max(1, now.getMonth())).padStart(2, '0')
  queryForm.startMonth = `${startYear}-${startMonth}`
  queryForm.endMonth = endMonth
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
}

function handleResize() {
  chartInstance?.resize()
}

function updateChart(data) {
  if (!chartInstance) return

  const months = data.map(item => item.month)
  const totalCounts = data.map(item => item.totalCount || 0)
  const totalWeights = data.map(item => Number(item.totalWeight || 0))

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['总尾数', '总重量(kg)'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisPointer: {
        type: 'shadow'
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '总尾数',
        min: 0,
        axisLabel: {
          formatter: '{value} 尾'
        }
      },
      {
        type: 'value',
        name: '总重量(kg)',
        min: 0,
        axisLabel: {
          formatter: '{value} kg'
        }
      }
    ],
    series: [
      {
        name: '总尾数',
        type: 'bar',
        data: totalCounts,
        itemStyle: {
          color: '#409eff'
        },
        barWidth: '40%'
      },
      {
        name: '总重量(kg)',
        type: 'line',
        yAxisIndex: 1,
        data: totalWeights,
        itemStyle: {
          color: '#67c23a'
        },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8
      }
    ]
  }

  chartInstance.setOption(option)
}

function loadData() {
  if (!queryForm.startMonth || !queryForm.endMonth) {
    return
  }
  if (queryForm.startMonth > queryForm.endMonth) {
    return
  }
  loading.value = true
  statistics({
    startMonth: queryForm.startMonth,
    endMonth: queryForm.endMonth
  })
    .then(res => {
      const data = res.data || res
      tableData.value = data.records || data.list || data || []
      nextTick(() => {
        updateChart(tableData.value)
      })
    })
    .finally(() => {
      loading.value = false
    })
}

function resetQuery() {
  initDefaultQuery()
  loadData()
}

onMounted(() => {
  initDefaultQuery()
  initChart()
  loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style lang="scss" scoped>
.chart-container {
  width: 100%;
  height: 450px;
}
</style>
