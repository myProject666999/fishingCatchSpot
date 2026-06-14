import request from './request'

export function add(data) {
  return request({
    url: '/favorite',
    method: 'post',
    data
  })
}

export function deleteById(id) {
  return request({
    url: `/favorite/${id}`,
    method: 'delete'
  })
}

export function list(params) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params
  })
}
