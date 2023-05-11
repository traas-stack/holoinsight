import request from '../request';

/** 获取当前所有插件信息 */
export async function listWithPagination(
  params: {
    /** query */
    pageNo: number;
    /** query */
    pageSize: number;
    /** query */
    env: string;
    /** query
     * 根据query模糊查询
     */
    query?: string;
    /** query */
    precise?: boolean;
  },
  options?: {},
) {
  return request('/webapi/appServices', {
    method: 'GET',
    params,
    ...(options || {}),
  });
}
export async function paths(
  data?: {
    /** query */
    serviceId?: string;
    /** query */
    from?: number;
    /** query */
    to?: number;
    context?: any;
  },
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_List_String__>(
    `/webapi/log/paths`,
    {
      // bodyData not set
      data: data,
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/count */
export async function count(
  body?: CLOUDMONITOR_API.MiniProgramLogCountQueryRequest,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.MiniProgramLogCountQueryResponse>(
    `/webapi/log/count`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/context */
export async function context(
  body?: CLOUDMONITOR_API.MiniProgramLogContextQueryRequest,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.MiniProgramLogContextQueryResponse>(
    `/webapi/log/context`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/query */
export async function query(
  body?: CLOUDMONITOR_API.MiniProgramLogQueryRequest,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.MiniProgramLogQueryResponse>(
    `/webapi/log/query`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 GET /webapi/log/multipaths */
export async function multipaths(
  params?: {
    /** query */
    serviceIds?: string;
    /** query */
    from?: number;
    /** query */
    to?: number;
  },
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_Map_String_List_String___>(
    `/webapi/log/multipaths`,
    {
      // bodyData not set
      params: {
        ...params,
      },
      method: 'GET',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/config/index */
export async function indexConfig(
  body?: CLOUDMONITOR_API.MiniProgramLogIndexConfig,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_boolean_>(
    `/webapi/log/config/index`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/config/agent */
export async function agentConfig(
  body?: CLOUDMONITOR_API.MiniProgramLogAgentConfig,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_boolean_>(
    `/webapi/log/config/agent`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 GET /webapi/log/index */
export async function getIndexConfig(
  params?: {
    /** query */
    serviceId?: string;
    /** query */
    path?: string;
  },
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_AntLogIndexConfig_>(
    `/webapi/log/index`,
    {
      // bodyData not set
      params: {
        ...params,
      },
      method: 'GET',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 POST /webapi/log/multiquery */
export async function multiQuery(
  body?: CLOUDMONITOR_API.MiniProgramMultiLogQueryRequest,
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_MiniProgramMultiLogQueryResponse_>(
    `/webapi/log/multiquery`,
    {
      data: body,
      // params not set
      method: 'POST',
      ...options,
    },
  );
}
/** 此处后端没有提供注释 GET /webapi/log/agent */
export async function getAgentConfig(
  params?: {
    /** query */
    serviceId?: string;
    /** query */
    path?: string;
  },
  options?: {
    [key: string]: any;
  },
) {
  return request<CLOUDMONITOR_API.JsonResult_Map_String_Object__>(
    `/webapi/log/agent`,
    {
      // bodyData not set
      params: {
        ...params,
      },
      method: 'GET',
      ...options,
    },
  );
}

export async function listServers(
  params?: {
    /** path */
    uuid?: string;
  },
  options?: {
    [key: string]: any;
  },
) {
  const { uuid: path0 } = params;
  return request<CLOUDMONITOR_API.ZSeagullResult_List_ServerDTO__>(
    `/webapi/appServices/${path0}/servers`,
    {
      // bodyData not set
      // params not set
      method: 'GET',
      ...options,
    },
  );
}
