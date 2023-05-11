import MagiChart from '@/pages/dashboardMagi/commonDashboard';
import { uuid } from '@/utils/help';
import React, { useEffect, useState } from 'react';
import $i18n from '@/i18n';

function buildConfig(pql: string) {
  const uid = 'target-' + uuid();
  const id = 'target-' + uuid();
  return {
    title: 'pql',
    version: '0.0.0',
    variables: [],
    groupType: 'collapse',
    panels: [
      {
        title: $i18n.get({
          id: 'holoinsight.pages.alarm.PQlChart.Unnamed',
          dm: '未命名',
        }),
        type: 'line_chart_panel',
        showTimeInfo: true,
        grid: {
          x: 0,
          y: 0,
          w: 24,
          h: 6,
        },
        targets: [
          {
            source: {
              dsId: pql,
              type: 'PROMETHEUS',
              legend: '{{__name__}}',
              dims: [],
              groupbyName: [],
              metrics: [pql],
              multi: false,
              tables: {
                labels: false,
                metrics: [
                  {
                    label: pql,
                    value: pql,
                  },
                ],

                multi: false,
              },
            },
            text: `PQL-${pql}`,
            type: 'PROMETHEUS',
            legend: '{{__name__}}',
            dims: [],
            groupbyName: [],
            metrics: [pql],
            uuid: uid,
            refId: 'A',
          },
        ],

        id: id,
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
            asTable: false,
            position: 'bottom',
            values: {
              min: false,
              max: false,
              avg: false,
              current: false,
              total: false,
            },
          },
          stack: false,
        },
        metricList: [
          {
            name: pql,
            refId: 'a',
            targetUuid: uid,
            type: 'ds',
          },
        ],
      },
    ],

    activeTab: -2,
  };
}
const DemoDashboard: React.FC<any> = ({
  pql,
  startTime,
  endTime,
  refresh,
}: {
  pql: string;
  startTime: number;
  endTime: number;
  refresh: number;
}) => {
  const [dashboardConfig, setDashboardConfig] = useState<any>(null);

  useEffect(() => {
    initDashboardConfig();
  }, [pql, refresh]);
  function initDashboardConfig() {
    const config = buildConfig(pql);
    setDashboardConfig(config);
  }

  return (
    <div>
      {dashboardConfig && (
        <MagiChart
          hasTimePicker={false} //是否有时间选择器
          start={startTime}
          end={endTime}
          allowEdit={false} //是否允许编辑
          mode={'preview'} //大盘模式
          onChangeMode={() => {}} //切换模式
          dashboardConfig={dashboardConfig} //大盘配置
          getTitle={() => <div></div>} //大盘标题
          getChangeDashboardConfig={() => {}} //拿到大盘的数据
        />
      )}
    </div>
  );
};

export default DemoDashboard;
