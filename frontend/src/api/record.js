import request from './request'

export function add(data) {
  return request({
    url: '/record',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/record',
    method: 'put',
    data
  })
}

export function deleteById(id) {
  return request({
    url: `/record/${id}`,
    method: 'delete'
  })
}

export function getDetail(id) {
  return request({
    url: `/record/${id}`,
    method: 'get'
  })
}

export function listBySpot(spotId, params) {
  return request({
    url: `/record/spot/${spotId}`,
    method: 'get',
    params
  })
}

export function list(params) {
  return request({
    url: '/record/list',
    method: 'get',
    params
  })
}

export function statistics(params) {
  return request({
    url: '/record/statistics/season',
    method: 'get',
    params
  })
}
