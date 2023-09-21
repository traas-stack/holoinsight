import $i18n from '../../../../i18n';
export const FILTER_DIMENSION = {
  totalCount: {
    key: 'totalCount',
    label: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.SortRequests',
      dm: '请求数排序',
    }),
  },
  errorCount: {
    key: 'errorCount',
    label: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.ErrorNumberSorting',
      dm: '错误数排序',
    }),
  },
  avgLatency: {
    key: 'avgLatency',
    label: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.AverageDelaySorting',
      dm: '平均延迟排序',
    }),
  },
};

export type END_POINT_KEY =
  | 'apm_endpoint_cpm'
  | 'apm_endpoint_cpm_fail'
  | 'apm_endpoint_percentile'
  | 'apm_endpoint_resp_time'
  | 'apm_endpoint_cpm_fail-apm_endpoint_cpm';

export const APM_ENDPOINT: {
  key: END_POINT_KEY;
  title: string;
  query?: string;
  downsample?: string;
  fillPolicy?: string;
}[] = [
  {
    key: 'apm_endpoint_cpm',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.TotalRequests',
      dm: '请求总量',
    }),
  },
  {
    key: 'apm_endpoint_cpm_fail-apm_endpoint_cpm',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.SuccessRate',
      dm: '成功率',
    }),
    query: '(b-a)/b*100',
    downsample: '1m', // 缺省填充100
    fillPolicy: 'percent', // 缺省填充100
  },
  {
    key: 'apm_endpoint_cpm_fail',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.RequestFailures',
      dm: '请求失败量',
    }),
  },
  {
    key: 'apm_endpoint_percentile',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.TimeConsumingQuantile',
      dm: '耗时分位数',
    }),
  },
  {
    key: 'apm_endpoint_resp_time',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.AverageTimeConsumption',
      dm: '平均耗时',
    }),
  },
];

export const APP_DETAIL_METRIC = [
  {
    key: 'apm_service_instance_cpm',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.ApmServicePerformanceMonitoringNumber',
      dm: 'APM服务性能监控-服务调用次数',
    }),
  },
  {
    key: 'apm_service_instance_cpm-fail-apm_service_instance_cpm',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.SuccessRate',
      dm: '成功率',
    }),
    query: '(b-a)/b*100',
    downsample: '1m', // 缺省填充100
    fillPolicy: 'percent', // 缺省填充100
  },
  {
    key: 'apm_service_instance_cpm_fail',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.ApmServicePerformanceMonitoringNumber.1',
      dm: 'APM服务性能监控-服务调用失败次数',
    }),
  },
  {
    key: 'apm_service_instance_percentile',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.ApmServicePerformanceMonitoringService',
      dm: 'APM服务性能监控-服务耗时分位数',
    }),
  },
  {
    key: 'apm_service_instance_resp_time',
    title: $i18n.get({
      id: 'holoinsight.APIMonitor.APIList.const.ApmServicePerformanceMonitoringService.1',
      dm: 'APM服务性能监控-服务耗时',
    }),
  },
];
