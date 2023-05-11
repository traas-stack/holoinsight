import { omit, uniq, pick } from 'lodash';
import { WEEK, MINUTE, DAY, HOUR, SECOND } from '@/constants';

export function getTime(time: number, interval = MINUTE) {
  // 特殊处理时区问题
  return (
    Math.floor(time / interval) * interval -
    (interval % DAY === 0 ? 8 * HOUR : 0)
  );
}

export const PLUGIN_MAP: Record<string, string> = {
  second: 'SEC',
  multiSecond: 'MSEC',
  singleMinute: 'SM',
  multiMinute: 'MM',
  spm: 'SPM',
  metrics: 'METRICS',
  statisTopn: 'STN',
  topn: 'TN',
  rrd: 'RRD',
  patternMatch: 'PM',
  purgeGroupby: 'PUG',
  multiGroupbyMultiValueMinute: 'MMM',
  multiGroupbyMultiValueSecond: 'MMS',
  dataset: 'DATASET',

  stack: 'STACK',
  STACK: 'STACK',
  SOFA_STACK: 'STACK',
  biz: 'BIZ',
  BIZ: 'BIZ',
};

export type DataSourceTargetWithContetxt = DataSourceTarget & Context;

export function merge(
  targets: DataSourceTarget[],
  ctx: Context,
): DataSourceTargetWithContetxt[] {
  return targets.map((item) => {
    return {
      ...item,
      ...pick(ctx, ['starttime', 'endtime', 'env', 'granularityType', 'limit']),
    };
  });
}

export function getDataTable(target?: CustomTarget): DataTable | undefined {
  if (!target || !target.source) return undefined;
  const {
    source: { tables },
    groupbyName = '__DEFAULT__',
  } = target;
  return Array.isArray(tables)
    ? tables.find((item) => item.table === groupbyName)
    : tables;
}

/**
 * 处理filters
 * @param filters 用户填的过滤器
 * @param labels 维度列表
 * @returns
 */
export function processFilters(
  filters?: Filter[],
  labels?: LabelColumn[],
): { filters: Filter[]; useTsdbApi: true } | undefined {
  if (filters && filters.length > 0 && labels && labels.length) {
    const availables = filters.filter(
      (item) =>
        item &&
        item.dimKey &&
        item.filterType &&
        item.values &&
        item.values.length > 0,
    );
    if (availables.length > 0) {
      const keys = availables.map((item) => item.dimKey);
      const data = [...availables];
      labels
        .filter((item) => !item.isTopLabel)
        .forEach(({ name }) => {
          if (!keys.includes(name)) {
            data.push({ dimKey: name, filterType: 'Regexp', values: ['.*'] });
          }
        });
      data.forEach((item) => {
        item.groupby = true;
      });
      return { filters: data, useTsdbApi: true };
    }
  }
  return undefined;
}

export function processWhere(filters?: Filter[]) {
  let where: any[] = [];

  filters?.forEach((filter) => {
    const { dimKey, values } = filter;
    if (values && values.length > 0) {
      where = where.concat(values.map((value) => ({ [dimKey]: value })));
    }
  });

  return {
    where,
  };
}

export function CF2PF(filters: Filter[]): PFilter[] | undefined {
  const availables: Filter[] = (filters || []).filter(
    (item: any) =>
      item &&
      item.dimKey &&
      item.filterType &&
      item.values &&
      item.values.length > 0,
  );
  if (availables.length > 0) {
    return availables.map((item) => {
      const { dimKey, filterType, values } = item;
      if (filterType === 'LiteralOr') {
        if (values.length === 1) {
          return { name: dimKey, operator: 'eq', value: values[0] };
        }
        return { name: dimKey, operator: 're', value: values.join('|') };
      }
      if (filterType === 'NotLiteralOr') {
        if (values.length === 1) {
          return { name: dimKey, operator: 'neq', value: values[0] };
        }
        return { name: dimKey, operator: 'nre', value: values.join('|') };
      }
      return { name: dimKey, operator: 're', value: `.*(${values[0]}).*` };
    });
  }
  return undefined;
}

/**
 * PROMQL的filter  转换到 数据源的Filter
 * @param filters
 * @returns
 */
export function PF2CF(filters: PFilter[]): Filter[] | undefined {
  const availables: PFilter[] = (filters || []).filter(
    (item: any) => item && item.name && item.operator && item.value,
  );
  if (availables.length > 0) {
    return availables.map((item) => {
      const { name, operator, value } = item;
      if (operator === 'eq') {
        return { dimKey: name, filterType: 'LiteralOr', values: [value] };
      }
      if (operator === 'neq') {
        return { dimKey: name, filterType: 'NotLiteralOr', values: [value] };
      }
      if (operator === 're') {
        return { dimKey: name, filterType: 'Regexp', values: [`^(${value})$`] };
      }
      return {
        dimKey: name,
        filterType: 'NotRegexpMatch',
        values: [`^(${value})$`],
      };
    });
  }
  return undefined;
}

/**
 * 获取真正获取的field
 * @param metrics 用户选择的指标
 * @returns
 */
export function getFields(metrics: string[]): string[] {
  return uniq(
    metrics.map((item) => item.replace(/_(non|wow)_(value|percent)$/, '')),
  );
}

export function process(info: CustomTarget & Context): Condition[] {
  const {
    source: { dsId, pluginType },
    where,
    filters,
    groupbyName,
    groupbyType,
    granularityType,
    metrics,
    needWOW,
    needNON,
    starttime,
    endtime,
    env,
    type,
    limit,
  } = info;

  const table: DataTable = getDataTable(info)!;
  const { interval, labels, isTop } = table;
  const start = getTime(starttime, interval);
  const end = getTime(endtime, interval);

  const params: Condition = {
    start,
    end,
    dsId,
    groupbyName,
    plugin: PLUGIN_MAP[pluginType as keyof typeof PLUGIN_MAP],
    useNew: true,
    granularityType,
    limit,
  };

  if (!isTop) {
    params.fields = getFields(metrics);
  } else {
    params.fields = ['topResult'];
  }

  if (env !== 'PROD') {
    params.tag = env;
  }
  if (where && where.length > 0) {
    params.where = where;
  } else {
    const tags = omit(where, ['__ds__', '__name__']);
    if (Object.keys(tags).length > 0) {
      params.where = tags;
    }
  }

  if (!params.where) {
    Object.assign(params, processFilters(filters, labels));
  }
  if (pluginType === 'patternMatch') {
    if (table.interval === SECOND) {
      params.plugin = 'PMSEC';
      if (params.groupbyName)
        params.groupbyName = params.groupbyName.split('@@')[0];
    }
  }

  if (params.groupbyName === '__DEFAULT__') {
    delete params.groupbyName;
  }

  if (needNON) {
    params.start -= interval;
  }

  if (needWOW) {
    return [params, { ...params, start: start - WEEK, end: end - WEEK }];
  }

  if (type === 'STACK' && typeof dsId === 'string' && groupbyType === 'pre') {
    const dsIdArr = dsId.split('@@');

    params.dsId = dsIdArr[1] || dsId;
    params.groupbyName = dsIdArr[2];
    params.plugin = 'DATASET';
  }

  return [params];
}

function isSecondSource(type: string) {
  return ['SEC', 'MSEC', 'PMSEC', 'MMS'].includes(type);
}

export function foldPROMQL(targets: DataSourceTarget[]): any[] {
  const sources = {};
  targets.forEach((item) => {
    if (item.type === 'PROMQL' || item.type === 'PQL') return;
    sources[item.uuid] = item;
  });

  return targets.map((item) => {
    if (item.type === 'PROMQL' && !item.folded) {
      const { pql, refs, type, uuid, ...rest } = item;
      const result = {
        ...rest,
        pql,
        type,
        uuid,
        refs: (refs || [])
          .filter((ref: any) => {
            if (ref.source in sources) {
              const source = sources[ref.source];
              return source.metrics.includes(ref.field);
            }
            return false;
          })
          .map((ref: any) => {
            const { name, field } = ref;
            const { source, filters, type, groupbyName } = sources[ref.source];
            if (type === 'STACK' || type === 'SOFA_STACK') {
              const { dsId } = source;
              const [stack, metric, groupby] = dsId.split('@@');
              return {
                name,
                metricType: 'stack',
                field,
                stack,
                metric,
                groupby,
                filters: CF2PF(filters),
              };
            }
            if (type === 'CUSTOM') {
              const {
                bizDomainId,
                outsideDomainId,
                workspaceId,
                tenantId,
                dsId,
                pluginType,
              } = source;
              const params = {
                name,
                groupBy: groupbyName,
                metricType: 'custom',
                field,
                bizDomainId,
                outsideDomainId,
                workspaceId,
                tenantId,
                pluginId: dsId,
                filters: CF2PF(filters),
                pluginType: PLUGIN_MAP[pluginType],
              } as any;
              const table: DataTable = getDataTable(sources[ref.source])!;
              if (pluginType === 'patternMatch') {
                if (table.interval === SECOND) {
                  params.pluginType = 'PMSEC';
                  if (params.groupbyName)
                    params.groupbyName = params.groupbyName.split('@@')[0];
                }
              }

              if (params.groupbyName === '__DEFAULT__') {
                delete params.groupbyName;
              }
              return params;
            }
            if (type === 'BIZ') {
              const { dsId } = source;
              const [bizId, groupby] = dsId.split('@@');
              return {
                name,
                metricType: 'biz',
                field,
                bizId,
                groupby,
                filters: CF2PF(filters),
              };
            }
            return undefined;
          }),
      };

      result.step = result.refs.every((item: any) =>
        isSecondSource(item.pluginType),
      )
        ? 1000
        : 60000;
      return result;
    }
    return item;
  });
}
