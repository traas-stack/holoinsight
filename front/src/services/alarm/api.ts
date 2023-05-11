import request from '../request';

/** alarm rule create */
export async function alarmRuleCreate(
  alarmRule: API.AlarmRule,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/create`, {
    method: 'POST',
    data: alarmRule,
    ...(options || {}),
  });
}
/** alarm alarmWebhook create */
export async function alarmWebHookCreate(
  alarmHook: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/create`, {
    method: 'POST',
    data: alarmHook,
    ...(options || {}),
  });
}

/** alarm alarmRobot create */
export async function alarmRobotCreate(
  alarmHook: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmDingDingRobot/create`, {
    method: 'POST',
    data: alarmHook,
    ...(options || {}),
  });
}

/** alarm alarmRobot delete */
export async function alarmRobotDelete(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmDingDingRobot/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
/** alarm rule delete */
export async function alarmRuleDelete(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
/** alarm webHook delete */
export async function alarmWebHookDelete(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
export async function alarmWebHookDeleteByName(
  name: string,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/deleteByName/${name}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
/** alarm alarmRobot update */
export async function alarmRobotUpdate(
  alarmRule: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmDingDingRobot/update`, {
    method: 'POST',
    data: alarmRule,
    ...(options || {}),
  });
}

/** alarm rule update */
export async function alarmRuleUpdate(
  alarmRule: API.AlarmRule,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/update`, {
    method: 'POST',
    data: alarmRule,
    ...(options || {}),
  });
}

/** alarm webHook update */
export async function alarmWebHookUpdate(
  alarmWebHook: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/update`, {
    method: 'POST',
    data: alarmWebHook,
    ...(options || {}),
  });
}

/** alarm rule query by id */
export async function alarmRuleQueryById(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/query/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** alram block query*/
export async function alarmBlockQueryById(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmBlock/query/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** alram block delete*/
export async function alarmBlockDelete(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmBlock/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** alarm block create */
export async function alarmWebBlockCreate(
  alarmHook: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmBlock/create`, {
    method: 'POST',
    data: alarmHook,
    ...(options || {}),
  });
}

/** alarm rule query all */
export async function alarmRuleQueryAll(options?: { [key: string]: any }) {
  return request(`/webapi/alarmRule/queryAll`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** alarm rule page query */
export async function pageQueryAlarmRule(
  alarmRuleRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/pageQuery`, {
    method: 'POST',
    data: alarmRuleRequest,
    ...(options || {}),
  });
}

export async function getSubScribeAlarmRule(
  isMyself: boolean,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/querySubscribeList?myself=${isMyself}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** alarm rule page query */
export async function pageQueryAlarmWebHook(
  alarmRuleRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/pageQuery`, {
    method: 'POST',
    data: alarmRuleRequest,
    ...(options || {}),
  });
}

/** alarm alarmRobot page query */
export async function pageQueryAlarmRobot(
  alarmRuleRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmDingDingRobot/pageQuery`, {
    method: 'POST',
    data: alarmRuleRequest,
    ...(options || {}),
  });
}

/** alarm history page query */
export async function pageQueryAlarmHistory(
  alarmHisRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmHistory/pageQuery`, {
    method: 'POST',
    data: alarmHisRequest,
    ...(options || {}),
  });
}

export async function pageQuerySubAlarmHistory(
  alarmHisRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmRule/querySubAlarmHistory`, {
    method: 'POST',
    data: alarmHisRequest,
    ...(options || {}),
  });
}

/** alarm history page query */
export async function alramhisotryDetailById(
  alarmHisRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmHistoryDetail/pageQuery`, {
    method: 'POST',
    data: alarmHisRequest,
    ...(options || {}),
  });
}

/** alarm group page query */
export async function pageQueryAlarmGroup(
  alarmGroupRequest: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmGroup/pageQuery`, {
    method: 'POST',
    data: alarmGroupRequest,
    ...(options || {}),
  });
}

/** alarm group create */
export async function alarmGroupCreate(
  alarmGroup: API.AlarmGroup,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmGroup/create`, {
    method: 'POST',
    data: alarmGroup,
    ...(options || {}),
  });
}

/** alarm group delete */
export async function alarmGroupDelete(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmGroup/delete/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** alarm group update */
export async function alarmGroupUpdate(
  alarmGroup: API.AlarmGroup,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmGroup/update`, {
    method: 'POST',
    data: alarmGroup,
    ...(options || {}),
  });
}

/** alarm group query by id */
export async function alarmGroupQueryById(
  id: number,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmGroup/query/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** alarmRulerSubscribe query by id */
export async function alarmSubscribeQueryById(
  id: string,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmSubscribe/queryByUniqueId/${id}`, {
    method: 'get',
    ...(options || {}),
  });
}

/** alarmRulerSubscribeSubmit */
export async function alarmSubscribeSubmit(
  subscribeGroup: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmSubscribe/submit`, {
    method: 'POST',
    data: subscribeGroup,
    ...(options || {}),
  });
}

export async function alramTestPreView(
  data: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/alarmWebhook/test`, {
    method: 'POST',
    data: data,
    ...(options || {}),
  });
}

export async function listAvailableFields() {
  return request(`/webapi/alertTemplate/listAvailableFields`, {
    method: 'GET',
  });
}

export async function alramGetAllUser(options?: { [key: string]: any }) {
  return request(`/webapi/user/getTenantUsers`, {
    method: 'get',
    ...(options || {}),
  });
}

export async function alarmQueryTagValues(
  data: any,
  options?: { [key: string]: any },
) {
  return request(`/webapi/v1/query/queryTagValues`, {
    method: 'POST',
    data: data,
    ...(options || {}),
  });
}
export async function alarmQueryPqlRange(
  data: {
    tenant: string;
    query: string;
    start?: number | string;
    end?: number | string;
    step?: number;
  },
  options?: { [key: string]: any },
) {
  return request(`/webapi/v1/query/pql/range`, {
    method: 'POST',
    data: data,
    ...(options || {}),
  });
}
export async function alarmQueryPqlParse(
  data: {
    pql: string;
  },
  options?: { [key: string]: any },
) {
  return request(`/webapi/v1/query/pql/parse`, {
    method: 'POST',
    data: data,
    ...(options || {}),
  });
}

export async function queryAlarmKey(options?: { [key: string]: any }) {
  return request('/webapi/alertTemplate/listAvailableFields', {
    method: 'get',
    ...(options || {}),
  });
}

export async function queryTagsApi(data: any) {
  return request(`/webapi/v1/query/tags`, {
    method: 'POST',
    data,
  });
}
export async function queryAllApi(data: any) {
  return request(`/webapi/v1/query`, {
    method: 'POST',
    data,
  });
}

export async function deleteTagsApi(option: any, isAll: boolean, keys?: any) {
  return request(`/webapi/v1/query/deltags`, {
    method: 'DELETE',
    data: {
      ...option,
      all: isAll,
      keys,
    },
  });
}
