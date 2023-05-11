import Cookies from 'js-cookie';
import request from '../request';

function getBaseData(data: any) {
  const baseData: API.ApiList = {
    tenant: Cookies.get('loginTenant'),
    start: data?.time?.start
      ? data?.time?.start
      : new Date().getTime() - 3600000,
    end: data?.time?.endtime ? data?.time?.endtime : new Date().getTime(),
  };
  return baseData;
}

//获取应用监控list数据
export async function queryApmServiceList(data: object, options?: { [key: string]: any }) {
  const baseData = getBaseData(data);
  return request(`/webapi/v1/trace/query/serviceList`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

// 获取拓扑图
export async function getTopoDataDetail(data: object, options?: { [key: string]: any }) {
  const baseData = getBaseData(data);
  return request(`/webapi/v1/trace/query/topology`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

//调用链路查询
export async function getCallLinkList(data: API.ApiCallLink, options?: { [key: string]: any }) {
  const baseData: any = {
    tenant: Cookies.get('loginTenant'),
  };
  return request(`/webapi/v1/trace/query/basic`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

export async function getCallLinkDetail(
  data: API.ApiCallLinkDetail,
  options?: { [key: string]: any },
) {
  const baseData: any = {
    tenant: Cookies.get('loginTenant'),
  };
  return request(`/webapi/v1/trace/query`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

//DB列表组件监控

export async function getComponentMonitor(data: any, options?: { [key: string]: any }) {
  const baseData = getBaseData(data);
  return request(`/webapi/v1/trace/query/componentList`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

export async function queryTraceAgent(params?: any, options?: { [key: string]: any }) {
  return request(`/webapi/agent/traceAgent`, {
    method: 'GET',
    ...(options || {}),
    params: {
      ...params,
    },
  });
}

//组件链路列表
export async function getComponentTraceIds(data: any, options?: { [key: string]: any }) {
  const baseData: any = {
    tenant: Cookies.get('loginTenant'),
  };
  return request(`/webapi/v1/trace/query/componentTraceIds`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}

//获取接口列表
export async function getInterfaceList(data: any, options?: { [key: string]: any }) {
  const baseData = getBaseData(data);

  return request(`/webapi/v1/trace/query/endpointList`, {
    method: 'post',
    ...(options || {}),
    data: Object.assign(baseData, data),
  });
}
