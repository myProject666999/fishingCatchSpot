import request from './request'

export function login(dto) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: dto
  })
}
