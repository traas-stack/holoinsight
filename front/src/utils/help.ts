import Cookies from 'js-cookie';
import qs from 'query-string';
import { getCurrentUser, getSys } from '../services/user/api';

export function initEnv(initData: any) {
  const params = qs.parse(location.search);
  const tenants = initData?.currentUser?.tenants || [];
  const currentTenant =
    params?.tenant ||
    initData?.currentUser?.user?.loginTenant ||
    Cookies.get('tenant') ||
    tenants[0].code;
  Cookies.set('loginTenant', currentTenant);
  const user = initData?.currentUser?.user || [];
  return {
    tenants,
    currentTenant,
    user,
  };
}
export function polishingUrl(url: string) {
  let arrs = location.pathname.split('/');
  let resUrl = url;
  if (arrs.includes('console')) {
    resUrl = '/console' + resUrl;
  }
  return resUrl;
}
function filterArr(treeNode: any, res: any, id: string, type: string) {
  const relation = res.calls;
  if (Array.isArray(relation)) {
    if (type === 'left') {
      const arrow = 'sourceId';
      for (let i = 0; i < relation.length; i++) {
        if (relation[i][arrow] === id && relation[i][arrow] !== relation[i]['destId']) {
          let destTreeNode: any = {};
          (destTreeNode.nodeId = relation[i].destId),
            (destTreeNode.name = relation[i].destName);
          destTreeNode.edgeMetric = relation[i].metric;
          destTreeNode.children = [];
          destTreeNode.level = 1;
          res.nodes.forEach((ele: any) => {
            if (ele.id === relation[i].destId) {
              destTreeNode.data = ele.metric || {};
              destTreeNode.nodeType = ele.real ? 'app' : ele.type;
              destTreeNode.serviceName = ele.serviceName;
            }
          });

          treeNode.children.push(destTreeNode);

          filterArr(destTreeNode, res, relation[i]['destId'], type);
        }
      }
    } else {
      const arrow = 'destId';
      for (let i = 0; i < relation.length; i++) {
        if (relation[i][arrow] === id && relation[i][arrow] !== relation[i]['destId']) {
          let sourceTreeNode: any = {};
          (sourceTreeNode.nodeId = relation[i].sourceId),
            (sourceTreeNode.name = relation[i].sourceName);
          sourceTreeNode.edgeMetric = relation[i].metric;
          sourceTreeNode.children = [];
          sourceTreeNode.level = -1;
          res?.nodes?.forEach((ele: any) => {
            if (ele.id === relation[i].sourceId) {
              sourceTreeNode.data = ele.metric || {};
              sourceTreeNode.nodeType = ele.real ? 'app' : ele.type;
              sourceTreeNode.serviceName = ele.serviceName;
            }

          });
          treeNode.children.push(sourceTreeNode);

          filterArr(sourceTreeNode, res, relation[i]['sourceId'], type);
        }
      }
    }
  }
}

export function getTreeData(res: any, treeNode: any, centerType: string) {
  let nodeId: string = '';
  let name: string = '';
  let metric = {};
  let nodeType = '';
  let serviceName = '';
  (res.nodes || []).forEach((element: any) => {
    if (element.name === centerType) {
      nodeId = element.id;
      name = element.name;
      metric = element.metric;
      nodeType = element.real ? 'app' : element.type;
      serviceName = element.serviceName;
    }
  });
  treeNode.nodeId = nodeId;
  treeNode.name = name;
  treeNode.data = metric || {};
  treeNode.children = [];
  treeNode.level = 0;
  treeNode.serviceName = serviceName;
  treeNode.data.appType = nodeType;
  filterArr(treeNode, res, nodeId, 'right');
  filterArr(treeNode, res, nodeId, 'left');
  return name;
}

export async function InitSystemInfo() {
  const config = await getSys();
  const currentUser = await getCurrentUser();
  return {
    config,
    currentUser,
  };
}
export function getQueryString(name: any) {
  let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
  let r = window.location.search.substr(1).match(reg);
  if (r !== null) return decodeURI(r[2]);
  return null;
}

export function uuid() {
  let s = [];
  let hexDigits = '0123456789abcdef';
  for (let i = 0; i < 36; i++) {
    s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
  }
  s[14] = '4';
  s[19] = hexDigits.substr(((s[19] as any) & 0x3) | 0x8, 1);
  s[8] = s[13] = s[18] = s[23] = '-';

  let uuid = s.join('');
  return uuid;
}


export function getUrlQueryString() {
  let arrs = location.pathname.split('/');
  if (arrs.includes('console')) {
    arrs = arrs.filter((item) => {
      return item !== 'console';
    });
  }
  return arrs.join('/');
}
export function getQuery() {
  return qs.parse(window.location.search);
}