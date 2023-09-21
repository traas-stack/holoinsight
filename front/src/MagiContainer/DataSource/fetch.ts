import { HOUR, MINUTE } from '@/constants';
import { context, request } from '@/Magi';
import { timeFloor } from '@/Magi';
import { keyBy, pick } from 'lodash';
import moment from 'moment';
import $i18n from '../../i18n';
import { getTime, PLUGIN_MAP } from './process-query';

function formatFilter(str: any, rule: any) {
  const regex = /[${}]/g;
  if (regex.test(str[0])) {
    if (typeof rule[str[0].replace(regex, '')] === 'string') {
      return rule[str[0].replace(regex, '')];
    } else if (rule[str[0].replace(regex, '')] instanceof Array) {
      return rule[str[0].replace(regex, '')].join('|');
    }
  } else {
    return str.join('|');
  }
}
function handleFilters(filters: any[], variablesValue: any) {
  const res: any[] = [];

  filters.forEach(({ values, dimKey, filterType }) => {
    if (!values || values.length === 0 || !formatFilter(values, variablesValue))
      return;
    res.push({
      type: filterType,
      name: dimKey,
      value:
        filterType === 'literal_or' || filterType === 'not_literal_or'
          ? formatFilter(values, variablesValue)
          : values[0],
    });
  });
  return res;
}
function compose(date: string, time: number) {
  const d = date;
  const t = moment(time, 'x').format('HH:mm:ss.SSS');
  return moment(`${d} ${t}`, 'YYYY-MM-DD HH:mm:ss.SSS').valueOf();
}

function compareTime(data: any, time: number) {
  if (data.type === 'day') {
    return data.value;
  } else {
    return time - data.value * 60 * 60 * 1000;
  }
}

export function buildFetchParams(
  targets: any[],
  vars: any,
  ctx: any,
  metricList: any[],
  variablesValue: any,
  timeAggregat: any,
) {
  const { starttime: start, endtime: end, compare } = ctx;
  const query: string[] = [],
    queryC: string[] = [],
    idMap = new Map(),
    datasources: any[] = [],
    datasourcesC: any[] = [],
    compareId: string[] = [],
    compareMetric: any[] = [],
    refIdCompare: any[] = [];
  metricList.forEach((mt) => {
    let str = '';
    if (mt.type === 'ds') {
      idMap.set(mt.targetUuid, mt.refId);
      if (!mt.hidden) {
        query.push(mt.refId);
        if (mt.compare) {
          str = mt.refId + 1;
          query.push(mt.refId + 1);
        }
        queryC.push(mt.refId);
      }
    } else if (!mt.hidden) {
      query.push(mt.expr! as string);
      if (mt.compare) {
        for (let i = 0; i < (mt.expr as string).length; i++) {
          if (/[\+|\-|\*|\/}]/g.test((mt.expr as string)[i])) {
            str = str + (mt.expr as string)[i];
          } else {
            str = str + (mt.expr as string)[i] + 1;
          }
        }
        query.push(str);
      }
      queryC.push(mt.expr! as string);
    }
    if (mt.compare) {
      compareId.push(mt.targetUuid || '');
      compareMetric[mt.targetUuid as any] = mt.compare;
      refIdCompare[str as any] = mt.compare;
    }
  });
  let ownDownsample: string | undefined;
  if (timeAggregat?.aggregationType === 'custom') {
    if (timeAggregat.granularityType === 'FIFTEEN_MINUTE') {
      ownDownsample = '15m';
    } else if (timeAggregat.granularityType === 'MINUTE') {
      ownDownsample = '1m';
    } else if (timeAggregat.granularityType === 'THIRTY_MINUTE') {
      ownDownsample = '30m';
    } else {
      ownDownsample = '1h';
    }
  }
  targets.forEach((t) => {
    const name = idMap.get(t.uuid);
    if (!name) return;
    const {
      metrics,
      aggregator,
      groupbyName: groupBy,
      filters = [],
      downsample,
      fillPolicy,
    } = t;
    datasources.push({
      start,
      end,
      name,
      metric: metrics[0],
      aggregator,
      groupBy,
      downsample:
        timeAggregat?.aggregationType === 'custom' ? ownDownsample : downsample,
      fillPolicy: fillPolicy,
      filters: handleFilters(filters, variablesValue),
    });

    if (compareId.includes(t.uuid)) {
      const cs = compareTime(compareMetric[t.uuid as any], start);
      datasources.push({
        start: cs,
        end: cs + (end - start),
        name: name + 1,
        metric: metrics[0],
        aggregator,
        groupBy,
        downsample:
          timeAggregat?.aggregationType === 'custom'
            ? ownDownsample
            : downsample,
        fillPolicy: fillPolicy,
        filters: handleFilters(filters, variablesValue),
      });
    }
    if (compare) {
      const cs = compose(compare, start);
      datasourcesC.push({
        start: cs,
        end: cs + (end - start),
        name: name,
        metric: metrics[0],
        aggregator,
        groupBy,
        downsample:
          timeAggregat?.aggregationType === 'custom'
            ? ownDownsample
            : downsample,
        fillPolicy: fillPolicy,
        filters: handleFilters(filters, variablesValue),
      });
    }
  });

  return {
    refIdCompare,
    datasources,
    query,
    datasourcesC,
    queryC,
  };
}

const dataType: any = 'TimeSeries';
export function formatResData(
  data: any[],
  targets: any[],
  metricList: any[],
  refIdCompare: any[],
  ctx: any,
  type?: string
): any {
  const targetMap = keyBy(targets, 'uuid');
  const mtNameMap = new Map();
  metricList.forEach((mt) => {
    if (mt.type === 'ds') {
      mtNameMap.set(mt.refId, {
        ...pick(mt, 'type', 'targetUuid', 'name'),
        legend: targetMap[mt.targetUuid!].legend,
      });
    } else {
      mtNameMap.set(
        mt.expr,
        pick(mt, 'type', 'name', 'expr', 'depMetrics', 'legend'),
      );
    }
  });
  const classData: any = getClassData(data,type);
  let metricObj: any = {};
  classData.forEach((item: any, index: number) => {
    let key = index === 0 ? 'default' : '';
    let time = 0;
    if (refIdCompare?.[item[0]?.metric]) {
      if (refIdCompare[item[0]?.metric]?.type === 'day') {
        key = moment(refIdCompare[item[0]?.metric].value).format('YYYY-MM-DD');
        time = ctx.starttime - refIdCompare[item[0]?.metric].value;
      } else {
        key = $i18n.get(
          {
            id: 'holoinsight.MagiContainer.DataSource.fetch.RefidcompareitemmetricHoursAgo',
            dm: '{refIdCompareItemMetric}小时前',
          },
          { refIdCompareItemMetric: refIdCompare[item[0].metric].value },
        );
        time = refIdCompare[item[0].metric].value * 60 * 60 * 1000;
      }
    } 
    if (!metricObj?.[key]) {
      metricObj[key] = {
        data: item?.map((m: any) => {
          const { tags, metric } = m;
          const meta = mtNameMap.get(metric);
          const labels = {
            ...tags,
            __name__: meta?.name,
          };
          return {
            legend: meta?.legend,
            meta,
            labels,
            metric: meta?.name, 
            values: m.values.map((vs: string[]) => ({
              time: key === 'default' ? vs[0] : vs[0] + time,
              value: +vs[1],
            })),
          };
        }),
        type: dataType,
      };
    } else {
      metricObj?.[key]?.data.push(
        item?.map((m: any) => {
          const { tags, metric } = m;
          const meta = mtNameMap.get(metric);
          const labels = {
            ...tags,
            __name__: meta?.name 
          };
          return {
            legend: meta?.legend,
            meta,
            labels,
            metric: meta?.name,
            values: m.values.map((vs: string[]) => ({
              time: key === 'default' ? vs[0] : vs[0] + time,
              value: +vs[1],
            })),
          };
        }),
      );
    }
  });
  return metricObj;
}

function getClassData(data: any = [],type?:string) {
  let metricArr: any
  if(type !== 'PROMETHEUS'){
  metricArr = [0];
  data.forEach((item: any, index: number) => {
      if (/[0-9]/g.test(item.metric) && !/[\+|\-|\*|\/]/g.test(item.metric)) {
        const str = (item.metric || '').slice(1, item.metric.length);
        metricArr.push(str);
      }
  });
  }else{
    metricArr = []
    data.forEach((item: any, index: number) => {
        metricArr.push(item.metric)
    });
  }
  metricArr = Array.from(new Set(metricArr));
  const backData = metricArr.map((item: any, index: number) => {
    let itemData: any = [];
    if(type === 'PROMETHEUS'){
      (data || []).forEach((ele: any, key: number) => {
        itemData.push(ele);
     });
    }else{
      if (item === 0) {
        (data || []).forEach((ele: any, key: number) => {
          if (!/[0-9]/g.test(ele.metric) || !/[a-z|A-Z][0-9]/g.test(ele.metric)) {
            itemData.push(ele);
          }
        });
      } else {
        (data || []).forEach((ele: any, key: number) => {
          if (/[0-9]/g.test(ele.metric) || /[a-z|A-Z][0-9]/g.test(ele.metric)) {
            itemData.push(ele);
          }
        });
      }
    }
    return itemData;
  });
  return backData;
}

export async function getLabelValues(
  source: any,
  groupbyName?: string,
): Promise<string[]> {
  const { pluginType, outsideDomainId, dsId, label } = source;
  const time = getTime(Date.now(), MINUTE);
  const { data, success } = await request(
    `/webapi/u_${outsideDomainId}/universalQuery?tenantId=${
      context.getParams().tenantId
    }`,
    {
      method: 'POST',
      data: [
        {
          condition: {
            plugin: PLUGIN_MAP[pluginType],
            contentType: 'KEYSET',
            dsId,
            groupbyName,
            start: timeFloor(time, MINUTE),
            end: timeFloor(time, MINUTE),
          },
        },
      ],
    },
  );

  if (success && data[0].success) {
    return data[0].data.map((item: any) => item.keyMap[label]);
  }
  return [];
}

export async function getPostLabelValues(source: any): Promise<string[]> {
  const { dim, cubeId } = source;

  const now = Date.now();
  const { data, success } = await request('/webapi/cube/data/queryDimValues', {
    method: 'POST',
    data: {
      cubeId,
      dim,
      pageSize: 20,
      startTime: now - HOUR,
      endTime: now,
    },
  });

  if (success && data) {
    return data.map((item: any) => item.value);
  }

  return [];
}
