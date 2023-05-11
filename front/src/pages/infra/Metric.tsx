import CommonBreadcrumb from '@/components/CommonBreadcrumb/index';
import MagiChart from '@/pages/dashboardMagi/commonDashboard';
import {
  getInfraTemplate,
  getTenantServerByCondition,
} from '@/services/infra/api';
import React, { useEffect, useState } from 'react';
import { Link } from 'umi';
import $i18n from '../../i18n';
const Dashboard: React.FC = (props:any) => {
  const { type } = props.match.params;
  const [template, setTemplate] = useState<any>({});
  const [meta, setMeta] = useState<any>({});

  const urlList = [
    {
      name: $i18n.get({
        id: 'holoinsight.pages.infra.Metric.ListOfBasicResources',
        dm: '基础资源列表',
      }),
      url: '/infra/server',
    },

    {
      name: $i18n.get({
        id: 'holoinsight.pages.infra.Metric.StandAloneMonitoring',
        dm: '单机监控',
      }),
    },
  ];

  useEffect(() => {
    getServerMeta();
    getTemplate();
  }, []);

  async function getServerMeta() {
    let conditions = {
      hostname: [props.match.params.hostname],
    };

    if (
      props?.match?.params?.namespace &&
      props?.match?.params?.namespace != '-'
    ) {
      conditions['namespace'] = props.match.params.namespace;
    }

    const dataResult = await getTenantServerByCondition(
      conditions,
    );
    if (dataResult && dataResult.length > 0) {
      setMeta(dataResult[0]);
    }
  }

  async function getTemplate() {
    let tempalteType = '';
    if (type === 'vm') {
      tempalteType = 'vm_server_metric';
    } else if (type === 'pod') {
      tempalteType = 'pod_server_metric';
    } else if (type === 'node') {
      tempalteType = 'node_server_metric';
    }

    const res = await getInfraTemplate(tempalteType, '-1');
    if (res && res?.config) {
      let config = JSON.stringify(res.config).replace(
        /\$\{hostname\}/g,
        props.match.params.hostname,
      );
      if (
        props?.match?.params?.namespace &&
        props?.match?.params?.namespace !== '-'
      ) {
        config = config.replace(
          /\$\{namespace\}/g,
          props.match.params.namespace,
        );
      }
      setTemplate(JSON.parse(config));
    }
  }

  const getExtra = () => {
    if (props?.match?.params?.type !== 'node' && meta?.app) {
      return (
        <Link to={`/app/dashboard?app=${meta?.app}`}>
          {$i18n.get({
            id: 'holoinsight.pages.infra.Metric.AssociatedApplications',
            dm: '关联应用',
          })}
          {meta?.app}
        </Link>
      );
    } else {
      return <></>;
    }
  };
  return (
    <>
      <CommonBreadcrumb urlList={urlList} to="chart" />

      {template && Object.keys(template).length && (
        <MagiChart
          hasTimePicker={true} //是否有时间选择器
          allowEdit={false} //是否允许编辑
          mode={'preview'} //大盘模式
          onChangeMode={() => {}} //切换模式
          dashboardConfig={template} //大盘配置
          getTitle={() => <div>{props?.match?.params?.hostname}</div>} //大盘标题
          getExtra={getExtra} //大盘标题
          getChangeDashboardConfig={() => {}} //拿到大盘的数据
          hasFavirate={{
            url: `/infra/metric/${props.match.params.type}/${props.match.params.namespace}/${props.match.params.hostname}`,
            type: 'infra',
            relateId: props.match.params.hostname,
            name: props?.match?.params?.hostname,
          }}
        />
      )}
    </>
  );
};

export default Dashboard;
