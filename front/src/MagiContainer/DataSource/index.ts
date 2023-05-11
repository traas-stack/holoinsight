import { isRelativeTime, getGap } from '../utils';
import { injectable } from 'inversify';
import Editor from './editor';
import qs from 'query-string';
import { keyBy, pick } from 'lodash';
import { cloneDeep, compact } from 'lodash';
import { buildFetchParams, formatResData } from './fetch';
import { fetchData } from '@/services/dashbard';
import { queryRange } from '@/services/dashbard';

type DataSourcePlugin = any
function getQuery() {
  return qs.parse(window.location.search);
}

const getValueType = (o: any): any => {
  let typeStr = (Object.prototype.toString.call(o).match(/\[object (.*?)\]/) ||
    [])[1];
  if (typeStr === 'Object') {
    typeStr += `:${o.constructor.name}`;
  } else if (typeStr === 'Number') {
    if (!isFinite(o)) {
      typeStr = isNaN(o) ? 'NaN' : 'Infinity';
    }
  }
  return typeStr;
};



function getVars(source: any) {
  const vars: any = {};
  const keys = Object.keys(source);
  keys.forEach((item) => {
    if (item.startsWith('var-')) {
      vars[item.slice(4)] = source[item];
    }
  });
  return vars;
}

function BackMilliSecond(type: string){
  switch(type){
    case '1s':
     return 1000;
    case '5s':
     return 5000;
    case '1m':
     return 60000
    case '1h':
      return 3600000;
    case '1d':
      return 86400000;
  }
}
@injectable()
export default class HIDataSource implements DataSourcePlugin {
  editor = Editor;
  async query(panel: any, obj: any) {
    const {
      aggregationType,
      granularityType,
      targets = [], // 数据源列表
      metricList = [], // 指标列表
      timeFrom, // 时间跨度
      timeShift, // 时间偏移
    } = { ...panel.model, ...obj } as any;
    const timeAggregat = {
      aggregationType: aggregationType,
      granularityType: granularityType,
    };
    const ctx: any = Object.keys(panel.dashboard.context.value || {}).length
      ? cloneDeep(panel.dashboard.context.value)
      : {
          starttime: new Date().getTime() - 60 * 60 * 1000,
          endtime: new Date().getTime(),
          range: '1hour,now',
        };
    if (isRelativeTime(ctx.range)) {
      if (timeFrom && /^\d+(:?m|h|d|s)$/.test(timeFrom)) {
        ctx.starttime = ctx.endtime - getGap(timeFrom);
      }
    }
    if (timeShift && /^\d+(:?m|h|d|s)$/.test(timeShift)) {
      ctx.starttime -= getGap(timeShift);
      ctx.endtime -= getGap(timeShift);
    }
      if (targets?.[0]?.type === 'PROMETHEUS') {
          let ownDownsample: string | undefined;
      if (timeAggregat?.aggregationType === 'custom') {
        if (timeAggregat.granularityType === 'FIFTEEN_MINUTE') {
          ownDownsample = '900000';
        } else if (timeAggregat.granularityType === 'MINUTE') {
          ownDownsample = '3600000';
        } else if (timeAggregat.granularityType === 'THIRTY_MINUTE') {
          ownDownsample = '1800000';
        } else {
          ownDownsample = '3600000';
        }
      }
      const backParms = {
        query: targets?.[0]?.metrics?.[0],
        start: ctx.starttime,
        end: ctx.endtime,
        timeout: '30s',
        fillZero: targets[0].fillPolicy || false,
        step: timeAggregat?.aggregationType === 'custom' ? ownDownsample : BackMilliSecond(targets[0].downsample) || '1000',
        
      };
      let data = (await queryRange(backParms)) as any;
      if (getValueType(data) !== 'Array') {
        data = [];
      }

      return formatResData(data, targets as any[], metricList, [], ctx, 'PROMETHEUS') as any;
    } else {
      const backParms = buildFetchParams(
        targets as any,
        panel.dashboard.variables,
        ctx,
        metricList,
        getVars(getQuery()) || {},
        timeAggregat,
      );
      if (!ctx.compare) {
        const { datasources, query, refIdCompare } = backParms;
        if (datasources.length === 0) return {};
        const { results } = await fetchData(datasources, query);
        return formatResData(
          results,
          targets as any[],
          metricList,
          refIdCompare,
          ctx,
        ) as any;
      } else {
        function backData(data: any) {
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
          let metricCols: any = [];
          metricCols = data?.map((item) => {
            const { tags, metric } = item;
            const meta = mtNameMap.get(metric);
            const labels = {
              ...tags,
              __name__: meta?.name,
            };
            return {
              type: 'float',
              legend: meta?.legend,
              meta,
              labels,
              values: item.values.map((vs: string[]) => ({
                labels,
                meta,
                time: vs[0],
                value: +vs[1],
              })),
            };
          });
          return metricCols;
        }

        const { datasources, datasourcesC, query, queryC } = backParms;
        const [r1, r2] = await Promise.all([
          fetchData(datasources, query),
          fetchData(datasourcesC, queryC),
        ]);
        let backObj: any = {};
        const data = r1.results;
        const timeIndex = (data || []).reduce(
          (maxIndex: any, item: any, index: Number) => {
            return item.values.length > data[maxIndex].values.length
              ? index
              : maxIndex;
          },
          0,
        );
        const timeCol = {
          name: 'time',
          type: 'date',
          values:
            Array.isArray(data) && data.length
              ? data[timeIndex].values.map((vs: any[]) => ({ value: vs[0] }))
              : [],
        };
        const metricCols = backData(r1.results);
        const metricColsBefore = backData(r2.results);
        backObj[ctx.compare] = {
          data: [timeCol, ...metricColsBefore],
        };
        backObj['default'] = {
          data: [timeCol, ...metricCols],
        };
        return backObj;
      }
    }
  }


  queryTrend(panel: any, colData: any = {}) {
     return this.query(panel, colData);
  }
  getVariables(panel: any) {
    const { targets = [] } = panel.model;
    const variableNames: string[] = [];
    targets.forEach((item: any) => {
      const { type, filters = [] } = item;

      if (type === 'CUSTOM' || type === 'STACK') {
        filters.forEach((filter: any) => {
          const { values } = filter;

          if (values && values.length > 0) {
            values.forEach((v: string) => {
              if (v && /\$\{(.+?)}/g.test(v)) {
                const matchs = v.match(/\$\{(.+?)}/g) || [];
                variableNames.push(
                  ...matchs.map((match: string) =>
                    match.replace(/(^\$\{)|(\}$)/g, ''),
                  ),
                );
              }
            });
          }
        });
      }
    });

    return variableNames;
  }
}
