// @ts-ignore
/* eslint-disable */
import request from '../request';

export async function getSys(options?: { [key: string]: any }) {
  return request('/webapi/sys/config', {
    method: 'GET',
    ...(options || {}),
  });
}
export async function querySchemaV2(name: string, options?: {}) {
  return request(`/webapi/v1/query/schema?name=${name}`, {
    method: 'GET',
    params: {},
    ...(options || {}),
  });
}

export async function getTenantConfig(
  name: string,
  options?: { [key: string]: any },
) {
  return request(`/webapi/tenant/config/${name}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function queryMetricLike(name?: string, options?: {}) {
  return request(`/webapi/v1/query/metric?name=${name}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function queryUserInfo(name?: string, options?: {}) {
  return request(`/webapi/user/getTenantUser?loginName=${name}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function getTenantUserById(name?: string, options?: {}) {
  return request(`/webapi/user/getTenantUserById?userId=${name}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function querySchema(param: any, options?: {}) {
  return request(`/webapi/v1/query/schema`, {
    method: 'GET',
    params: {
      ...param,
    },
    ...(options || {}),
  });
}