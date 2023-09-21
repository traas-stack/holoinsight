import { metricDataQuery } from '@/services/infra/api';
import _ from 'lodash';


export const metricDataQueryFunc = async (dataRange: any,app:any, workspace:string | undefined, cloudRun:any) => {
  let metricList = cloudRun.isCloudRun ? [
    'k8s_pod_cpu_util',
    'k8s_pod_mem_util',
  ]:[
    'system_cpu_util',
    'system_mem_util',
    'k8s_pod_cpu_util',
    'k8s_pod_mem_util',
  ];
  let metricIpMap: Record<
    string,
    | {
        cpu_util: [number, number][];
        mem_util: [number, number][];
      }
    | {}
  > = {};

  try {
    const res = await metricDataQuery({
      datasources: metricList.map((item) => {
        const backItem = {
          metric: item,
          aggregator: 'avg',
          groupBy: [
            "app"
          ],
          ...dataRange,
        };
        backItem.filters = workspace ? [
          {
            name: "app",
            type: "literal_or",
            value: app
          },
          {
            name: "workspace",
            type: "literal_or",
            value: workspace
          }
        ]:[
          {
            name: "app",
            type: "literal_or",
            value: app
          }
        ] 
        if(cloudRun.isCloudRun){
          backItem.filters = backItem.filters.concat([
            {
              name: "appId",
              type: "literal_or",
              value: cloudRun.appId
            },
            {
              name: "envId",
              type: "literal_or",
              value: cloudRun.envId
            }
          ]) 
        }
        return backItem;
      }),
    });
    if (res && res.results && res.results.length > 0) {
      _.forEach(res.results, (re: any) => {
        const { metric, tags, values } = re;
        if (!metricIpMap[tags?.app]) {
          metricIpMap[tags?.app] = {};
        }

        let newMetric = metric;
        if (
          metric === 'system_cpu_util' ||
          metric === 'k8s_pod_cpu_util' ||
          metric === 'k8s_node_cpu_util'
        ) {
          newMetric = 'cpu_util';
        } else if (
          metric === 'system_mem_util' ||
          metric === 'k8s_pod_mem_util' ||
          metric === 'k8s_node_mem_util'
        ) {
          newMetric = 'mem_util';
        }
        metricIpMap[tags?.app][newMetric] = values;
      });
    }
    return metricIpMap;
  } catch (e) {
    return {
      cpu_util: [],
      mem_util: [],
    };
  }
};
