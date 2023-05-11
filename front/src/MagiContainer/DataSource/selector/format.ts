import { TYPES } from '@/MagiContainer/constants';
import { querySchemaV2 } from '@/services/tenant/api';
import $i18n from '../../../i18n';

async function formatCloudTarget(obj: any) {
  
  const metric = obj.metrics;
  const labels = await querySchemaV2(metric).then((res: any) => {
    const tags = res?.tags || [];
    return tags.map((t: string) => ({ label: t, value: t }));
  });
  return {
    text:
      $i18n.get({
        id: 'holoinsight.DataSource.selector.format.CloudMonitoring',
        dm: '云监控-',
      }) + metric,
    dsId: metric,
    source: {
      pluginType: TYPES.cloud,
      dsId: metric,
      tables: {
        labels, // 可选维度列表
        metrics: [{ label: metric, value: metric }], // 可选指标列表
        multi: true,
      },
    },
  };
}

async function formatPQLTarget(obj: any) {
  const metric = obj.metrics;
  return {
    source: {
      dsId: metric,
      type: TYPES.prometheus,
      legend: '{{__name__}}',
      dims: [], // 已选择的维度列表
      groupbyName: [], // 聚合维度
      metrics: [metric],
      multi: false,
      tables: {
        labels: false, // 可选维度列表
        metrics: [{ label: metric, value: metric }], // 可选指标列表
        multi: false,
      },
    },
    text: 'PQL-' + metric,
  };
}

async function formatLogTarget(obj: any) {
  const { type, treeNode } = obj;
  const { position, logId, logName } = treeNode;
  const metric = treeNode.metrics.title;
  const labels = await querySchemaV2(metric).then((res: any) => {
    const tags = res?.tags || [];
    return tags.map((t: string) => ({ label: t, value: t }));
  });
  return {
    source: {
      pluginType: type,
      dsId: logId + metric,
      position: position,
      tables: {
        labels, // 可选维度列表
        metrics: [
          {
            label: metric,
            value: metric,
          },
        ],
        // 可选指标列表
        multi: true,
      },
    },
    text:
      $i18n.get({
        id: 'holoinsight.DataSource.selector.format.LogMonitoring',
        dm: '日志监控-',
      }) +
      logName +
      metric,
  };
}
export async function formatTarget(obj: any) {
  try {
    let target: any = {};
    switch (obj.type) {
      case TYPES.cloud:
        target = await formatCloudTarget(obj);
        break;
      case TYPES.log:
        target = await formatLogTarget(obj);
        break;
      case TYPES.prometheus:
        target = await formatPQLTarget(obj);
        break;
      default:
        break;
    }
    
    const groupbyName = Array.isArray(target.source?.tables?.labels) ? target.source.tables.labels.map((p: any) => {
      return p.value
    }) : [];
    let  legend = '{{__name__}}'; 
    groupbyName.forEach((m:string,n:number)=>{
        n === groupbyName.length -1 ? legend += `{{${m}}}` : legend += `{{${m}}}/` 
    })
    if(obj.type === 'PROMETHEUS'){
      return {
        ...target,
        type: obj.type,
        fillPolicy: true,
        downsample: '1m',
        legend: legend,
        dims: [], // 已选择的维度列表
        groupbyName: groupbyName, // 默认选择的聚合维度值
        metrics: [target.source.tables?.metrics?.[0].value],
  
      };
    }else{
      return {
        ...target,
        type: obj.type,
        legend: legend,
        dims: [], // 已选择的维度列表
        groupbyName: groupbyName, // 默认选择的聚合维度值
        metrics: [target.source.tables?.metrics?.[0].value],
        
      };
    }
  } catch (e) { }
}
