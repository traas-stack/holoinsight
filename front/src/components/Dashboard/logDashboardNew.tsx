import MagiChart from '@/pages/dashboardMagi/commonDashboard';
import { CLOUMN_OPTIONS, LINE_OPTIONS } from '@/utils/const';
import {getQueryString, uuid } from '@/utils/help';
import { useEffect } from 'react';
import _ from 'lodash';
import React, { memo, useState } from 'react';

const LogDashboard: React.FC<any> = (props) => {
  const { metricArr, title, type, favoriteConfig, periodNum } = props;
  const envWorkSapce =
    getQueryString('workspace') || '';
  const [template, setTemplate] = useState(null);
  const [workspace, setWorkSpace] = useState(envWorkSapce);
  useEffect(() => {
    if (metricArr && Array.isArray(metricArr) && metricArr.length) {
      setTemplate(getTemplate());
    }
  }, [JSON.stringify(metricArr)]);

  function getNewWorkSpace(value: string) {
    setWorkSpace(value);
  }

  function getTemplate() {
    let newTemplate: any = {
      title: 'title',
      version: '0.0.0',
      variables: [],
      groupType: 'collapse',
      activeTab: -2,
      panels: [],
    };

    _.forEach(metricArr, (item: any, index: number) => {
      const metricName = item.targetTable;

      let labels = [
        {
          label: 'app',
          value: 'app',
        },
        {
          label: 'hostname',
          value: 'hostname',
        },
        {
          label: 'ip',
          value: 'ip',
        },
      ];

      let dims = ['app', 'hostname', 'ip'];
      if (workspace && !_.isUndefined(workspace)) {
        dims.push('workspace');
      }

      let legend: any[] = [];
      if (item.tags && item.tags.length > 0) {
        _.forEach(item.tags, (tag) => {
          labels.push({
            label: tag,
            value: tag,
          });

          // legend += `/{{${tag}}}`
          legend.push(`${tag}={{${tag}}}`);
          dims.push(tag);
        });
      }
      let aggregator = 'sum';
      if (
        item &&
        item.metrics &&
        item.metrics.length > 0 &&
        item?.metrics?.[0]?.func !== null &&
        item?.metrics?.[0]?.func !== 'count' &&
        item?.metrics?.[0]?.func !== 'loganalysis'
      ) {
        aggregator = item?.metrics?.[0]?.func;
      }
      let column_options = _.cloneDeep(CLOUMN_OPTIONS);
      column_options.headers.row = dims;
      new Array(2).fill(0).forEach((m, n: number) => {
        let panel: any = {
          title: metricName,
          type: n === 0 ? 'line_chart_panel' : 'cross_table_panel',
          showTimeInfo: true,
          grid: {
            x: n === 0 ? 0 : 12,
            y: 0 + index * 12,
            w: 12,
            h: 8,
          },
          targets: [
            {
              // "text": "云监控-" + metricName,
              dsId: metricName,
              downsample: periodNum,
              fillPolicy: 'zero',
              legend: legend.join(','),
              // "dims": dims,
              groupbyName: n === 0 ? item.tags : dims,
              metrics: [metricName],
              uuid: 'target-d5af-b260-3e41' + index,
              refId: 'A',
              aggregator: aggregator,
              filters: backFilters(),
            },
          ],
          options: n === 0 ? LINE_OPTIONS : column_options,
          metricList: [
            {
              name: metricName,
              refId: 'a',
              targetUuid: 'target-d5af-b260-3e41' + index,
              type: 'ds',
            },
          ],
          id: 'panel-' + uuid(),
        };
        if (n !== 0) {
          panel['timeShift'] = '1m';
          panel['timeFrom'] = '10m';
        } else if (n === 0) {
          panel['timeShift'] = '1m';
          panel['timeFrom'] = '30m';
        }
        newTemplate.panels.push(panel);
      });
    });
    return newTemplate;
  }

  function backFilters() {
    let filters: any = [];
      filters = _.isUndefined(workspace)
        ? null
        : [
            {
              dimKey: 'workspace',
              filterType: 'literal_or',
              values: ['${workspace}'],
            },
          ];
    return filters;
  }

  return (
    <>
      {template ? (
        <MagiChart
          hasTimePicker={true} //是否有时间选择器
          allowEdit={false} //是否允许编辑
          mode={'preview'} //大盘模式
          onChangeMode={() => {}} //切换模式
          dashboardConfig={template} //大盘配置
          getTitle={() => <div>{title}</div>} //大盘标题
          getChangeDashboardConfig={() => {}} //拿到大盘的数据
          hasFavirate={{
            url: favoriteConfig.url,
            type: favoriteConfig.type,
            relateId: type,
            name: title,
          }}
          hasWorkSpace={true}
          workspace={workspace}
          getNewWorkSpace={getNewWorkSpace}
        />
      ) : null}
    </>
  );
};

export default memo(LogDashboard, (prevProps, nextProps) => {
  if (
    JSON.stringify(prevProps.metricArr) !== JSON.stringify(nextProps.metricArr)
  ) {
    return false;
  } else {
    return true;
  }
});
