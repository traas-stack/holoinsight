import $i18n from '@/i18n';
export const NAMES = 'abcdefghijklmnopqrstuvwxyz';
export const WEEKS = [
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Sunday', dm: '周日' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Monday', dm: '周一' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Tuesday', dm: '周二' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Wednesday', dm: '周三' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Thursday', dm: '周四' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Friday', dm: '周五' }),
  $i18n.get({ id: 'holoinsight.pages.alarm.const.Saturday', dm: '周六' }),
];
export const METRICS_FILTER_REG = /(?<![a-z0-9])[a-z]{1}(?![a-z])/g;

export const WEEK_DATE = [
  {
    label: '1分钟',
    value: '1',
  },
  {
    label: '5分钟',
    value: '5',
  },
  {
    label: '10分钟',
    value: '10',
  },
  {
    label: '15分钟',
    value: '15',
  },
  {
    label: '30分钟',
    value: '30',
  },
  {
    label: '1小时',
    value: '60',
  },
  {
    label: '6小时',
    value: '360',
  },
];
