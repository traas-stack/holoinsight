import { metricDataQueryFunc } from '@/pages/applicationMonitor/helper';
import { ProCard } from '@ant-design/pro-card';
import { Col, Row } from 'antd';
import React, { memo, useEffect, useState } from 'react';
import $i18n from '../../../../i18n';
import style from './index.less';
import PieBox from './Pie';
export interface Props {
  app: string;
  rangeTime: any;
  workspace: string;
  cloudRun: any;
}
const DataSources: React.FC<Props> = ({
  app,
  rangeTime,
  workspace,
  cloudRun,
}) => {
  const [metric, setMetric] = useState({
    cpu: 0,
    mem: 0,
  });
  useEffect(() => {
    if (Object.keys(rangeTime).length) {
      handleMetric({
        start: rangeTime.starttime,
        end: rangeTime.endtime,
      });
    }
  }, [JSON.stringify(rangeTime), workspace, cloudRun]);
  async function handleMetric(time: any) {
    const data = await metricDataQueryFunc(time, app, workspace, cloudRun);
    const { cpu_util = [], mem_util = [] } = (data[app] as {
      cpu_util: [number, number][];
      mem_util: [number, number][];
    }) || { cpu_util: [], mem_util: [] };
    const cpu = cpu_util?.[0]?.[1] || 0;
    const mem = mem_util?.[0]?.[1] || 0;
    setMetric({
      cpu,
      mem,
    });
  }

  return (
    <ProCard
      title={$i18n.get({
        id: 'holoinsight.overview.DataSources.ResourceMonitoring',
        dm: '资源监控',
      })}
      headerBordered
      bodyStyle={{ padding: '0' }}
    >
      <Row gutter={0}>
        <Col span={12} className={style.col}>
          <div className={style.card}>
            <h5>
              {$i18n.get({
                id: 'holoinsight.overview.DataSources.CpuUsage',
                dm: 'CPU使用率',
              })}
            </h5>
            <div>
              <PieBox percent={metric.cpu} />
            </div>
          </div>
        </Col>
        <Col span={12} className={style.col}>
          <div className={style.card}>
            <h5>
              {$i18n.get({
                id: 'holoinsight.overview.DataSources.MemoryUsage',
                dm: '内存使用率',
              })}
            </h5>
            <div>
              <PieBox percent={metric.mem} />
            </div>
          </div>
        </Col>
      </Row>
    </ProCard>
  );
};
export default memo(DataSources);
