import request from './request'

export function add(data) {
  return request({
    url: '/spot/add',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/spot/update',
    method: 'put',
    data
  })
}

export function deleteById(id) {
  return request({
    url: `/spot/${id}`,
    method: 'delete'
  })
}

export function getDetail(id) {
  return request({
    url: `/spot/${id}`,
    method: 'get'
  })
}

export function list(data) {
  return request({
    url: '/spot/list',
    method: 'post',
    data
  })
}

export function nearby(lng, lat, radius) {
  return request({
    url: '/spot/nearby',
    method: 'get',
    params: { lng, lat, radius }
  })
}

export function toggleFavorite(id) {
  return request({
    url: `/spot/toggleFavorite/${id}`,
    method: 'post'
  })
}
