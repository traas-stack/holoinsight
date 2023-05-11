import request from '../request';
export async function pageQuery(
  userFavRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/userFavorite/pageQuery`, {
    method: 'POST',
    data: userFavRequest,
    ...(options || {}),
  });
}

export async function userFavoriteQueryAll(options?: { [key: string]: any }) {
  return request('/webapi/userFavorite/queryAll', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function userFavoriteCreate(
  userFavorite: any,
  options?: { [key: string]: any },
) {
  return request('/webapi/userFavorite/create', {
    method: 'POST',
    data: { ...userFavorite, url: userFavorite?.url },
    ...(options || {}),
  });
}

export async function userFavoriteDeleteById(
  type: string,
  relateId: string,
  options?: { [key: string]: any },
) {
  return request(`/webapi/userFavorite/deleteByRelateId/${type}/${relateId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

export async function userFavoriteQueryById(
  type: string,
  relateId: string,
  options?: { [key: string]: any },
) {
  return request(`/webapi/userFavorite/queryByRelateId/${type}/${relateId}`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function deleteById(id: number, options?: any) {
  return request(`/webapi/userFavorite/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

export async function getUserFavorite(
  data: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/userFavorite/queryByCondition`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
