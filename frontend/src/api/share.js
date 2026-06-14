import request from './request'

export function share(data) {
  return request({
    url: '/share',
    method: 'post',
    data
  })
}

export function list(params) {
  return request({
    url: '/share/list',
    method: 'get',
    params
  })
}

export function getDetail(id) {
  return request({
    url: `/share/${id}`,
    method: 'get'
  })
}

export function like(id) {
  return request({
    url: `/share/${id}/like`,
    method: 'post'
  })
}

export function deleteById(id) {
  return request({
    url: `/share/${id}`,
    method: 'delete'
  })
}
