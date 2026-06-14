import request from './request'

export function add(data) {
  return request({
    url: '/record/add',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/record/update',
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

export function listBySpot(spotId, data) {
  return request({
    url: `/record/listBySpot/${spotId}`,
    method: 'post',
    data
  })
}

export function list(data) {
  return request({
    url: '/record/list',
    method: 'post',
    data
  })
}

export function statistics(data) {
  return request({
    url: '/record/statistics',
    method: 'post',
    data
  })
}
