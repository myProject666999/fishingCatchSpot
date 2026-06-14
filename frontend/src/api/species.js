import request from './request'

export function listAll() {
  return request({
    url: '/species/list',
    method: 'get'
  })
}

export function getById(id) {
  return request({
    url: `/species/${id}`,
    method: 'get'
  })
}
