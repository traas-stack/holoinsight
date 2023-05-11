import { each } from 'lodash';
import $i18n from '../i18n';

export const SECOND = 1e3;
export const MINUTE = 60 * SECOND;
export const HOUR = 60 * MINUTE;
export const DAY = 24 * HOUR;
export const WEEK = 7 * DAY;

export const PLUGIN_MAP = {
  SM: 'singleMinute',
  MM: 'multiMinute',
  SEC: 'second',
  MSEC: 'multiSecond',
  STN: 'statisTopn',
  TN: 'topn',
  SPM: 'spm',
  GREP: 'grepEvent',
  PUG: 'purgeGroupby',
  PUSTN: 'purgeStatisTopn',
  PUGSEC: 'purgeSecGroupby',
  PUSECSTN: 'purgeSecStatisTopn',
  PM: 'patternMatch',
  PMSEC: 'secondPatternMatch',
  DEVICE: 'device',
  MW: 'middleware',
  AM: 'appMonitor',
  BM: 'bizModel',
  cms: 'cms',
  controlPlan: 'controlPlan',
};

const PLUGIN_REVERSE_MAP = {};

each(PLUGIN_MAP, (v, k) => {
  PLUGIN_REVERSE_MAP[v] = k;
});

export const getPlugionTypeName = (pluginType: string): string => {
  if (!pluginType) {
    return pluginType;
  }
  return PLUGIN_REVERSE_MAP[pluginType] || pluginType.toUpperCase();
};

export const PLUGIN_NAME_MAP = {
  multiMinute: $i18n.get({
    id: 'holoinsight.src.constants.MinuteMultiKey',
    dm: '分钟多Key',
  }),
  spm: $i18n.get({
    id: 'holoinsight.src.constants.CommonServiceMetrics',
    dm: '常用服务指标',
  }),
  singleMinute: $i18n.get({
    id: 'holoinsight.src.constants.MinutesWithoutKey',
    dm: '分钟无Key',
  }),
  rrd: $i18n.get({
    id: 'holoinsight.src.constants.ArchiveStatistics',
    dm: '归档统计',
  }),
  second: $i18n.get({
    id: 'holoinsight.src.constants.NoKeyInSeconds',
    dm: '秒级无Key',
  }),
  multiSecond: $i18n.get({
    id: 'holoinsight.src.constants.MultipleKeysInSeconds',
    dm: '秒级多Key',
  }),
  statisTopn: $i18n.get({
    id: 'holoinsight.src.constants.MinuteStatisticsTop',
    dm: '分钟统计Top',
  }),
  patternMatch: $i18n.get({
    id: 'holoinsight.src.constants.PatternMatching',
    dm: '模式匹配',
  }),
  topn: $i18n.get({
    id: 'holoinsight.src.constants.SingleDataTop',
    dm: '单笔数据Top',
  }),
  dataset: $i18n.get({ id: 'holoinsight.src.constants.Dataset', dm: '数据集' }),
  multiGroupbyMultiValueMinute: $i18n.get({
    id: 'holoinsight.src.constants.MultiValueMultiGroupMinute',
    dm: '多值多分组/分钟统计',
  }),
  multiGroupbyMultiValueSecond: $i18n.get({
    id: 'holoinsight.src.constants.MultiValueMultiGroupSecond',
    dm: '多值多分组/秒级统计',
  }),
};

export enum TYPES {
  custom = 'CUSTOM',
  market = 'INDICATORS_AGGREGATE',
  stack = 'STACK',
  sofa = 'SOFA_STACK',
  biz = 'BIZ',
}
