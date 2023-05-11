import request from '@/services/request';

export async function getSys(options?: { [key: string]: any }) {
  return request('/webapi/sys/config', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function getCurrentUser(options?: { [key: string]: any }) {
  return request('/webapi/user/getCurrentUser', {
    method: 'GET',
    ...(options || {}),
  });
}
