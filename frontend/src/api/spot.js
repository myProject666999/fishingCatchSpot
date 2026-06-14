import request from './request'

export function add(data) {
  return request({
    url: '/spot',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/spot',
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

export function list(params) {
  return request({
    url: '/spot/list',
    method: 'get',
    params
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
    url: `/spot/${id}/favorite`,
    method: 'post'
  })
}
