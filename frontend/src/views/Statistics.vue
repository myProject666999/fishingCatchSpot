<template>
  <div class="page-container">
    <div class="page-header">
      <h3>季节统计</h3>
    </div>

    <div class="filter-bar">
      <el-date-picker
        v-model="monthRange"
        type="monthrange"
        range-separator="至"
        start-placeholder="开始月份"
        end-placeholder="结束月份"
        value-format="YYYY-MM"
        style="width: 300px"
      />
      <el-select v-model="filter.speciesId" placeholder="选择鱼种" clearable style="width: 180px">
        <el-option v-for="sp in speciesOptions" :key="sp.id" :label="sp.name" :value="sp.id" />
      </el-select>
      <el-button type="primary" @click="loadData">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-row :gutter="16" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">总记录数</div>
            <div class="stat-value">{{ stats.totalRecords || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">总尾数</div>
            <div class="stat-value">{{ stats.totalCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">总重量(kg)</div>
            <div class="stat-value">{{ stats.totalWeight || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">涉及鱼种</div>
            <div class="stat-value">{{ stats.speciesCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <span style="font-weight: 600">每月各鱼种上钩数（柱状图）</span>
      </template>
      <div ref="barChartRef" style="height: 400px" v-loading="loading"></div>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <span style="font-weight: 600">每月上钩率趋势（折线图）</span>
      </template>
      <div ref="lineChartRef" style="height: 400px" v-loading="loading"></div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { statistics } from '@/api/record'
import { listAll as listSpecies } from '@/api/species'

const loading = ref(false)
const barChartRef = ref(null)
const lineChartRef = ref(null)
let barChart = null
let lineChart = null

const speciesOptions = reactive([])
const stats = reactive({
  totalRecords: 0,
  totalCount: 0,
  totalWeight: 0,
  speciesCount: 0
})

const filter = reactive({
  speciesId: null
})

const monthRange = ref([])

function resetFilter() {
  monthRange.value = []
  filter.speciesId = null
  loadData()
}

function loadSpeciesOptions() {
  listSpecies().then(res => {
    const arr = res || []
    speciesOptions.splice(0, speciesOptions.length, ...arr)
  })
}

function buildChartData(data) {
  const list = Array.isArray(data) ? data : (data?.monthlyStats || [])
  const speciesSet = new Set()
  const monthSet = new Set()

  list.forEach(item => {
    monthSet.add(item.month)
    if (item.speciesName) speciesSet.add(item.speciesName)
  })

  const months = Array.from(monthSet).sort()
  const speciesList = Array.from(speciesSet)

  const seriesBar = speciesList.map(sname => ({
    name: sname,
    type: 'bar',
    stack: 'total',
    emphasis: { focus: 'series' },
    data: months.map(m => {
      const found = list.find(i => i.month === m && i.speciesName === sname)
      return found ? (found.count || 0) : 0
    })
  }))

  const totalByMonth = months.map(m => {
    return list.filter(i => i.month === m).reduce((s, i) => s + (i.count || 0), 0)
  })

  return { months, speciesList, seriesBar, totalByMonth }
}

function initBarChart() {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
}

function initLineChart() {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
}

function renderBarChart({ months, seriesBar }) {
  if (!barChart) return
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { type: 'scroll' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '上钩尾数'
    },
    series: seriesBar
  }
  barChart.setOption(option)
}

function renderLineChart({ months, totalByMonth }) {
  if (!lineChart) return
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['上钩总数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months,
      axisLabel: { rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '尾数'
    },
    series: [
      {
        name: '上钩总数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.4)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        },
        lineStyle: { width: 3, color: '#409EFF' },
        itemStyle: { color: '#409EFF' },
        data: totalByMonth
      }
    ]
  }
  lineChart.setOption(option)
}

function loadData() {
  loading.value = true
  const params = {}
  if (monthRange.value && monthRange.value.length === 2) {
    params.startMonth = monthRange.value[0]
    params.endMonth = monthRange.value[1]
  }
  if (filter.speciesId) {
    params.speciesId = filter.speciesId
  }
  statistics(params).then(res => {
    const data = res || {}
    const monthlyList = data.monthlyStats || data.records || data || []

    const { months, speciesList, seriesBar, totalByMonth } = buildChartData(monthlyList)

    stats.totalRecords = data.totalRecords || 0
    stats.totalCount = data.totalCount || totalByMonth.reduce((a, b) => a + b, 0)
    stats.totalWeight = data.totalWeight || 0
    stats.speciesCount = speciesList.length || data.speciesCount || 0

    nextTick(() => {
      if (!barChart) initBarChart()
      if (!lineChart) initLineChart()
      renderBarChart({ months, seriesBar })
      renderLineChart({ months, totalByMonth })
    })
  }).finally(() => {
    loading.value = false
  })
}

function handleResize() {
  barChart?.resize()
  lineChart?.resize()
}

onMounted(() => {
  loadSpeciesOptions()
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  lineChart?.dispose()
})
</script>

<style lang="scss" scoped>
.stat-item {
  text-align: center;

  .stat-label {
    color: #909399;
    font-size: 14px;
    margin-bottom: 10px;
  }

  .stat-value {
    color: #303133;
    font-size: 28px;
    font-weight: 600;
  }
}
</style>
