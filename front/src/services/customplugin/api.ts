import request from '../request';

// 日志切分
export const splitReq = async (params: any) => {
  return await request('/webapi/customPlugin/log/presplit', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export async function queryById(id: any, options?: any) {
  return request(`/webapi/customPlugin/query/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function queryByParentFolderId(
  parentFolderId: any,
  options?: Record<string, unknown>,
) {
  return request(
    `/webapi/customPlugin/queryByParentFolderId/${parentFolderId}`,
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

export async function deleteById(id: number, options?: any) {
  return request(`/webapi/customPlugin/delete/${id}`, {
    method: 'Delete',
    ...(options || {}),
  });
}

export async function create(params: any, options?: Record<string, unknown>) {
  return request(`/webapi/customPlugin/create`, {
    method: 'POST',
    data: JSON.stringify(params),
    ...(options || {}),
  });
}
export async function updateParentFolderId(
  params: any,
  options?: Record<string, unknown>,
) {
  return request(`/webapi/customPlugin/updateParentFolderId`, {
    method: 'POST',
    data: JSON.stringify(params),
    ...(options || {}),
  });
}

export async function update(params: any, options?: Record<string, unknown>) {
  return request(`/webapi/customPlugin/update`, {
    method: 'POST',
    data: JSON.stringify(params),
    ...(options || {}),
  });
}

export async function pageQuery(
  pageRequest: any,
  options?: Record<string, unknown>,
) {
  return request(`/webapi/customPlugin/pageQuery`, {
    method: 'POST',
    data: pageRequest,
    ...(options || {}),
  });
}

export async function queryByTenant(params: { tenant: string }) {
  return request(`/webapi/metatable/queryByTenant`, {
    method: 'GET',
    params: {
      ...params,
    },
  });
}

export async function queryAll(params: { tableName: string; tenant?: string }) {
  return request(`/webapi/meta/${params.tableName}/queryAll`, {
    method: 'GET',
    params: {
      ...params,
      tableName: undefined,
    },
  });
}
