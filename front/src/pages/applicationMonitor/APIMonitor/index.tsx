import LineTpChart from '@/components/Chart/linkTpChart';
import MagiRefeshContainer from '@/MagiContainer/MagiRefeshContainer';
import { getTopoDataDetail } from '@/services/application/api';
import { TOPO_DEEP } from '@/utils/const';
import { getQueryString, getTreeData } from '@/utils/help';
import { Select } from 'antd';
import { ProCard } from '@ant-design/pro-card';
import React, { memo, useEffect, useState } from 'react';
import $i18n from '../../../i18n';
import APIList from './APIList';
import style from './index.less';

export interface Props {
  title: string;
  type: string;
}
const APIMonitor: React.FC<Props> = (props) => {
  let treeNode: any = {};
  const app = getQueryString('app');
  const range = decodeURIComponent(decodeURI(getQueryString('range') || ''));
  const envWorkSapce =
    getQueryString('workspace') || '';
  const [chartData, setChartData] = useState(null);
  const [pointName, setPointName] = useState('');
  const [deep, setDeep] = useState(2);
  const [time, setTime] = useState({});
  const [workspace, setWorkSpace] = useState(envWorkSapce);

  useEffect(() => {
    if (pointName) {
      getTopoData();
    }
  }, [pointName, deep, JSON.stringify(time)]);

  async function getTopoData() {
    getTopoDataDetail({
      serviceName: app,
      depth: deep,
      endpointName: pointName,
      category: 'endpoint',
      time: time,
    }).then((data) => {
      if (Object.prototype.toString.call(data) === '[object Object]') {
        getTreeData(data, treeNode, pointName);
        setChartData(treeNode);
      }
    });
  }
  function changePointerName(value: string) {
    setPointName(value);
  }
  function onContextChange(value: any) {
    setTime(value);
  }
  function getNewWorkSpace(value: string) {
    setWorkSpace(value);
  }
  return (
    <div className={style.APIMonitor}>
      <MagiRefeshContainer
        hasWorkSpace={true}
        context={{ range, env: 'prod' }}
        getNewWorkSpace={getNewWorkSpace}
        workspace={workspace}
        // delay={90}
        onChange={onContextChange}
      />

      <ProCard
        title={$i18n.get({
          id: 'holoinsight.applicationMonitor.APIMonitor.InterfaceTopology',
          dm: '接口拓扑',
        })}
        headerBordered
        extra={
          <div>
            <span style={{ color: '#FFF' }}>
              {$i18n.get({
                id: 'holoinsight.applicationMonitor.APIMonitor.TopologyDepth',
                dm: '拓扑图深度：',
              })}
            </span>
            <Select
              value={deep}
              onChange={(value) => setDeep(value)}
              style={{ width: 120 }}
              options={TOPO_DEEP}
            />
          </div>
        }
      >
        {chartData ? <LineTpChart treeNode={chartData} fromType="api" /> : null}
      </ProCard>
      <APIList
        time={time}
        type={app || ''}
        title={app || ''}
        changePointerName={changePointerName}
        compType="api"
      />
    </div>
  );
};
export default memo(APIMonitor);
