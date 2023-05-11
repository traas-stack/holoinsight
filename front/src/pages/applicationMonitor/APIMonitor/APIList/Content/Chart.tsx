import React, { useEffect, memo } from 'react';

const { Panel } = window.grafanaComponents;
const NAMES = 'abcdefghijklmnopqrstuvwxyz';
export interface Props {
  datasources: any;
  query?: string;
  downsample?: string;
  fillPolicy?: string

}
const AlarmPanel: React.FC<Props> = (props) => {
  const field = props.datasources;
  const {query,downsample,fillPolicy } = props;
  
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
          field
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
  useEffect(() => {
    if (field) {
      // const BackData = mergeData(field);
      if(!query){
        field.start = new Date().getTime() - 3600000;
        field.end = new Date().getTime();  
      }
      panel.targets = [
        {
          refId: 'A',
          datasources:query ? field : [field],
          query: query,
          downsample:downsample,
          fillPolicy:fillPolicy,
        },
      ];
    }
  }, [field]);


  return field ? <Panel style={{ height: 300 }} panel={panel} /> : null;
};

export default memo(AlarmPanel);
