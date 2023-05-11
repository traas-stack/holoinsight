import request from '@/services/request';
import Cookies from 'js-cookie';


export interface GrafanaItemData{
  metric: string;
  tags: {
    service_name: string;
    endpoint_name: string;
  };
  values: [number, string][];
}
/** table schema create */
export async function tableCreate(metaTable: any, options?: { [key: string]: any }) {
  return request(`/webapi/metatable/create`, {
    method: 'POST',
    data: metaTable,
    ...(options || {}),
  });
}

/** table schema update */
export async function tableUpdate(metaTable: any, options?: { [key: string]: any }) {
  return request(`/webapi/metatable/update`, {
    method: 'POST',
    data: metaTable,
    ...(options || {}),
  });
}

/** 获取租户table schema */
export async function getTableList(options?: { [key: string]: any }) {
  return request('/webapi/metatable/queryByTenant', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取租户table meta data */
export async function getTableDataByName(tableName: string, options?: { [key: string]: any }) {
  return request(`/webapi/meta/${tableName}/queryAll`, {
    method: 'GET',
    ...(options || {}),
  });
}

export async function getTableDataByCondition(
  tableName: string,
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/${tableName}/queryByCondition`, {
    method: 'POST',
    data: condition,
    ...(options || {}),
  });
}

export async function getTenantServerByCondition(
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/queryByTenantServer`, {
    method: 'POST',
    data: condition,
    ...(options || {}),
  });
}

export async function getTenantAppByCondition(
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/queryByTenantApp`, {
    method: 'POST',
    data: condition,
    ...(options || {}),
  });
}

/** table data fuzzy query */
export async function fuzzyQueryByTenantServer(
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/fuzzyQueryByTenantServer`, {
    method: 'POST',
    data: condition,
    ...(options || {}),
  });
}

//租户_app

/** table data update */
export async function tableDataUpdate(
  tableName: string,
  metaList: any[],
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/${tableName}/create`, {
    method: 'POST',
    data: metaList,
    ...(options || {}),
  });
}

/** table data delete */
export async function tableDataDeleteByCondition(
  tableName: string,
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/${tableName}/deleteByCondition`, {
    method: 'DELETE',
    data: condition,
    ...(options || {}),
  });
}

/** table data fuzzy query */
export async function fuzzyQuery(
  tableName: string,
  condition: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/meta/${tableName}/fuzzyQuery`, {
    method: 'POST',
    data: condition,
    ...(options || {}),
  });
}

export interface IndicatorsMonitorParams {
  datasources: {
    metric: string;
    start: number | string;
    end: number | string;
    filters: {
      type: string;
      name: string;
      value: string;
    }[];
    downsample?: string;
    aggregator?: string;
  }[];
}

export interface IndicatorsMonitorRes {
  results: GrafanaItemData[];
}
/** table metric query */
export async function metricDataQuery(
  condition: IndicatorsMonitorParams,
  options?: { [key: string]: any },
) {
  return request<IndicatorsMonitorRes>(`/webapi/v1/query`, {
    method: 'POST',
    data: {
      ...condition,
      tenant: Cookies.get('loginTenant'),
    },
    ...(options || {}),
  });
}
export interface EndPointData {
  name: string;
  metric: {
    '@type': string;
    avgLatency: number;
    p95Latency: number;
    p99Latency: number;
    totalCount: number;
    errorCount: number;
    successRate: number;
  };
}
// 获取接口列表
export function getEndpointList(
  condition: {
    serviceName: string;
    start: string | number;
    end: string | number;
  },
  options?: { [key: string]: any },
) {
  const data:any = {
    ...condition,
    tenant: Cookies.get('loginTenant'),
  }
  return request<EndPointData[]>(
    `/webapi/v1/trace/query/endpointList`,
    {
      method: 'POST',
      data: data,
      ...(options || {}),
    },
  );
}
// 获取单机列表
export function getStandAloneList(
  condition: {
    serviceName: string;
    start: string | number;
    end: string | number;
  },
  options?: { [key: string]: any },
) {
  const data: any = {
    ...condition,
    tenant: Cookies.get('loginTenant'),
  }
  return request<EndPointData[]>(
    `/webapi/v1/trace/query/serviceInstanceList`,
    {
      method: 'POST',
      data: data,
      ...(options || {}),
    },
  );
} 



export interface GrafanaItemData {
  metric: string;
  tags: {
    service_name: string;
    endpoint_name: string;
  };
  values: [number, string][];
}

export interface IndicatorsMonitorParams {
  datasources: {
    metric: string;
    start: number | string;
    end: number | string;
    filters: {
      type: string;
      name: string;
      value: string;
    }[];
    downsample?: string;
    aggregator?: string;
  }[];
}

export interface IndicatorsMonitorRes {
  results: GrafanaItemData[];
}

export interface EndPointData {
  name: string;
  metric: {
    '@type': string;
    avgLatency: number;
    p95Latency: number;
    p99Latency: number;
    totalCount: number;
    errorCount: number;
    successRate: number;
  };
}
export async function getDashBoardPageQuery(
  data: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/v1/dashboard/pageQuery`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

export async function getInfraTemplate(type: string,refId: string, options?: { [key: string]: any }) {
    return request(`/webapi/displaytemplate/query/${type}/${refId}`, {
        method: 'GET',
        ...(options || {}),
    });
}

export async function getAPMTemplate(templateId:string, options?: { [key: string]: any }){
  return request(`/webapi/displaytemplate/query/${templateId}`, {
    method: 'GET',
    ...(options || {}),
  });
}
