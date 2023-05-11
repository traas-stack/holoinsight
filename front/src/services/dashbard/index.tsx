import request from '@/services/request';
import Cookies from 'js-cookie';

export function fetchData(
    datasources: any[],
    query: string[],
  ): Promise<{ results: any[] }> {
    return request('/webapi/v1/query', {
      method: 'POST',
      data: {
        datasources,
        query: query.join(','),
      },
    });
  }

export async function queryRange(data: any, options?: any) {
    return request(`/webapi/v1/query/pql/range`, {
      method: 'POST',
      data,
      ...(options || {}),
    });
}


export function queryDashboardById(id: string) {
    return request(`/webapi/v1/dashboard/query/${id}`);
  }
  
  export function addDashboard(conf: Record<string, any>) {
    return request('/webapi/v1/dashboard/create', {
      method: 'POST',
      data: {
        title: conf.title,
        conf,
        type: 'magi',
      },
    }) as Promise<Record<string, any>>;
  }
  export function updateDashboard(conf: Record<string, any>) {
    return request('/webapi/v1/dashboard/update', {
      method: 'POST',
      data: {
        id: conf.id,
        type: 'magi',
        title: conf.title,
        conf,
        tenant: Cookies.get('currentTenant'),
      },
    });
  }
  
  
export async function getDashBoardPageQuery(data: any, options?: { [key: string]: any }) {
  return request(`/webapi/v1/dashboard/pageQuery`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}