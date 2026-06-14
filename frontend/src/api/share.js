import request from './request'

export function share(data) {
  return request({
    url: '/share/add',
    method: 'post',
    data
  })
}

export function list(data) {
  return request({
    url: '/share/list',
    method: 'post',
    data
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
    url: `/share/like/${id}`,
    method: 'post'
  })
}

export function deleteById(id) {
  return request({
    url: `/share/${id}`,
    method: 'delete'
  })
}
