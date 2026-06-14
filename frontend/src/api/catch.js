import request from '@/utils/request'

export function listByRecord(recordId) {
  return request({
    url: `/catch/record/${recordId}`,
    method: 'get'
  })
}
