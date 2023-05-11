import MagiChart from '../../dashboardMagi/commonDashboard';
import moment from 'moment';
import _ from 'lodash';
import React, { memo } from 'react';
import $i18n from '../../../i18n';
import { uuid } from '../../../utils/help';

const NAMES = 'abcdefghijklmnopqrstuvwxyz';

const AlarmPanel: React.FC<any> = (props) => {
  const field =
    typeof props.data === 'string' ? JSON.parse(props.data) : props.data;
  const item = props.item;
  let startTime = new Date().getTime() - 30 * 60000;
  let endTime = new Date().getTime();

  if (item?.alarmTime) {
    startTime = moment(item.alarmTime).toDate().getTime() - 10 * 60000;
    endTime = moment(item.alarmTime).toDate().getTime() + 10 * 60000;
  }
  function mergeData(data: any) {
    let newDataSources = (data?.datasources || []).map(
      (item: any, index: number) => {
        let newItem: any = {};
        newItem.metric = item?.metric;
        newItem.aggregator = item?.aggregator;
        newItem.downsample = /[0-9][a-zA-Z]{1}$/.test(item.downsample)
          ? item.downsample
          : item.downsample + 'm';
        newItem.groupBy = item.groupBy;
        newItem.name = NAMES[index];
        if (item?.filters?.length && Object.keys(item?.filters[0]).length) {
          let newFilters = item.filters.map((ele: any) => {
            let newFilterItem: any = {};
            newFilterItem.type = ele.type;
            if (ele.whites?.length > 0) {
              newFilterItem.name = ele.key;
              newFilterItem.value = (ele.whites || []).join('|');
            } else {
              newFilterItem.name = ele.name;
              newFilterItem.value = ele.value;
            }
            return newFilterItem;
          });
          newItem.filters = newFilters;
        }
        return newItem;
      },
    );
    return newDataSources;
  }
  const panel: any = {
    aliasColors: {},
    bars: false,
    dashLength: 10,
    dashes: false,
    datasource: null,
    fill: 1,
    fillGradient: 0,
    gridPos: {
      h: 9,
      w: 12,
      x: 0,
      y: 0,
    },
    hiddenSeries: false,
    id: 2,
    legend: {
      avg: false,
      current: false,
      max: false,
      min: false,
      show: true,
      total: false,
      values: false,
    },
    lines: true,
    linewidth: 1,
    nullPointMode: 'null',
    options: {
      alertThreshold: true,
    },
    percentage: false,
    pluginVersion: '8.2.3',
    pointradius: 2,
    points: false,
    renderer: 'flot',
    seriesOverrides: [],
    spaceLength: 10,
    stack: false,
    steppedLine: false,
    targets: [
      {
        refId: 'A',
        datasources: [
          {
            metric: 'cpu_user',
            aggregator: 'none',
          },
        ],
      },
    ],

    thresholds: [],
    timeRegions: [],
    title: 'cpu_user',
    tooltip: {
      shared: true,
      sort: 0,
      value_type: 'individual',
    },
    type: 'graph',
    xaxis: {
      buckets: null,
      mode: 'time',
      name: null,
      show: true,
      values: [],
    },
    yaxes: [
      {
        format: 'short',
        label: null,
        logBase: 1,
        max: null,
        min: null,
        show: true,
      },
      {
        format: 'short',
        label: null,
        logBase: 1,
        max: null,
        min: null,
        show: true,
      },
    ],

    yaxis: {
      align: false,
      alignLevel: null,
    },
  };

  let template: any = {
    title: 'title',
    version: '0.0.0',
    variables: [],
    groupType: 'collapse',
    activeTab: -2,
    panels: [],
  };

  if (field) {
    const datasources = mergeData(field);
    panel.targets = [
      {
        refId: 'A',
        datasources: datasources,
        query: field.query,
      },
    ];

    let target: any[] = [];
    let metricList: any[] = [];
    let refIds: any[] = [];

    _.forEach(datasources, (datasource) => {
      let filters: any[] = [];
      if (datasource.filters && datasource.filters.length > 0) {
        _.forEach(datasource.filters, (filter) => {
          filters.push({
            dimKey: filter.name,
            filterType: filter.type,
            values: filter.value?.split('|'),
          });
        });
      }

      const uid = 'target-' + uuid();
      target.push({
        text:
          $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmPanel.CloudMonitoring',
            dm: '云监控-',
          }) + datasource.metric,
        dsId: datasource.metric,
        type: 'CLOUD_MONITOR',
        groupbyName: datasource.groupBy,
        metrics: [datasource.metric],

        uuid: uid,
        refId: datasource.name,
        aggregator: datasource.aggregator,
        filters: filters,
        fillPolicy: 'zero',
      });

      refIds.push(datasource.name);

      metricList.push({
        name: datasource.metric,
        refId: datasource.name,
        targetUuid: uid,
        hidden: datasources.length > 1 ? true : false,
        type: 'ds',
      });
    });

    if (datasources.length > 1) {
      metricList.push({
        refId: 'c',
        expr: field.query,
        type: 'calculated',
        exprType: 'normal',
        depMetrics: refIds,
      });
    }
    template.panels.push({
      type: 'line_chart_panel',
      showTimeInfo: true,
      grid: {
        x: 0,
        y: 0,
        w: 24,
        h: 9,
      },
      targets: target,
      options: {
        xAxis: {
          label: {
            formatter: 'HH:mm',
          },
          visible: true,
        },
        yAxis: {
          label: {
            formatter: {
              op: '/',
              opValue: 1,
              unit: 'default',
            },
          },
          visible: true,
        },
        lineStyle: {
          size: 2,
        },
        line: {
          connectNulls: false,
        },
        point: {
          visible: false,
          size: 4,
        },
        area: {
          visible: false,
          opacity: 0.03,
        },
        tooltip: {
          visible: true,
          mode: 'share',
          order: 'desc',
        },
        legend: {
          visible: true,
          asTable: true,
          position: 'bottom',
          values: {
            min: false,
            max: false,
            avg: false,
            current: true,
            total: false,
          },
        },
        stack: false,
      },
      metricList: metricList,
      id: 'panel-' + uuid(),
    });
  }
  return field?.triggerContent || field.compareConfigs ? (
    <MagiChart
      hasTimePicker={false}
      start={startTime}
      end={endTime}
      allowEdit={false} 
      mode={'preview'} 
      onChangeMode={() => {}}
      dashboardConfig={template} 
      getTitle={() => <div></div>}
      getChangeDashboardConfig={() => {}}
    />
  ) : (
    <div>
      {$i18n.get({
        id: 'holoinsight.pages.alarm.AlarmPanel.FailedToGenerateTheChart',
        dm: '生成图表失败，请编辑补充数据！',
      })}
    </div>
  );
};

const Chart = memo(AlarmPanel, (prevProps: any, nextProps: any) => {
  if (JSON.stringify(prevProps.data) !== JSON.stringify(nextProps.data)) {
    return false;
  } else {
    return true;
  }
});

export default Chart;
