import request from './request'

export function list() {
  return request({
    url: '/species/list',
    method: 'get'
  })
}

export function getDetail(id) {
  return request({
    url: `/species/${id}`,
    method: 'get'
  })
}
