import $i18n from '../../i18n';
export const APP_ROUTER_TYPE = {
  all: {
    key: 'all',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.ApplicationOverview',
      dm: '应用总览',
    }),
  },
  componentMonitor: {
    key: 'componentMonitor',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.CallComponentMonitoring',
      dm: '调用组件监控',
    }),
  },
  interfaceMonitor: {
    key: 'interfaceMonitor',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.InterfaceMonitoring',
      dm: '接口监控',
    }),
  },
  callLink: {
    key: 'callLink',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.InterfaceMonitoring',
      dm: '接口监控',
    }),
  },
  standAloneMonitor: {
    key: 'standAloneMonitor',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.StandAloneMonitoring',
      dm: '单机监控',
    }),
  },
  appDetail: {
    key: 'appDetail',
    title: $i18n.get({
      id: 'holoinsight.pages.applicationMonitor.const.VirtualApplicationDetails',
      dm: '虚拟应用详情',
    }),
  },
};
export const APP_ROUTER_LIST = Object.keys(APP_ROUTER_TYPE).map((key) => {
  return {
    ...APP_ROUTER_TYPE[key as keyof typeof APP_ROUTER_TYPE],
  };
});
