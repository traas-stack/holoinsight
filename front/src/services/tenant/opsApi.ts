import request from '@/services/request';

/** ops log query */
export async function pageQuery(
  userOpLogRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/userOpLog/pageQuery`, {
    method: 'POST',
    data: userOpLogRequest,
    ...(options || {}),
  });
}
