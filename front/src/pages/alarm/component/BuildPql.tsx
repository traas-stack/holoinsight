import { METRICS_FILTER_REG, NAMES } from '@/pages/alarm/component/const';
import { message } from 'antd';
import $i18n from '@/i18n';

export function buildQpl(pql: string, pqlMetricList: any[]) {
  const ml: any = {};
  pqlMetricList.forEach((item: any, index: number) => {
    ml[NAMES[index]] = item.metric;
  });
  let result = '';
  if (typeof pql === 'string') {
    try {
      result = pql.replaceAll(METRICS_FILTER_REG, (substring, args) => {
        if (ml[substring]) {
          return ml[substring];
        } else {
          throw new Error();
        }
      });
    } catch (e) {
      message.warn(
        $i18n.get({
          id: 'holoinsight.pages.alarm.utils.CheckWhetherTheVariableUsed',
          dm: '请确认语句中使用的变量是否能与指标对应。',
        }),
      );
    }
  }
  return result;
}
