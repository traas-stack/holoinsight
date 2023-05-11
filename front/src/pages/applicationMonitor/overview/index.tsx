import { ProCard } from '@ant-design/pro-card';
import { Select } from 'antd';
import React, { memo, useEffect, useState } from 'react';
import DataSources from './DataSources';
import AccessDocument from '@/components/AccessDocument';
import AppTopo from '@/components/AppTopo';
import MagiRefeshContainer from '@/MagiContainer/MagiRefeshContainer';
import { getTopoDataDetail } from '@/services/application/api';
import { TOPO_DEEP } from '@/utils/const';
import {getQueryString } from '@/utils/help';

import $i18n from '../../../i18n';
import style from './index.less';
export interface Props {
  title: string;
  type: string;
}
const Overview: React.FC<Props> = (props) => {
  const [deep, setDeep] = useState<Number>(2);
  const app = getQueryString('app') || '';
  const [rangeTime, setRangeTime] = useState<any>({});
  const [topoData, setTopoData] = useState();
  const [hasTopo, sethasTop] = useState(false);
  const range = decodeURIComponent(decodeURI(getQueryString('range') || ''));

  useEffect(() => {
    getTopoDataDetail({
      serviceName: app,
      depth: deep,
      category: 'service',
      time: rangeTime,
    }).then((res: any) => {
      if (Object.prototype.toString.call(res) === '[object Object]') {
        if ((res?.nodes || []).length || (res?.calls || []).length) {
          sethasTop(true);
          setTopoData(res);
        } else {
          sethasTop(false);
        }
      }
    });
  }, [deep, JSON.stringify(rangeTime)]);
  function onContextChange(data: any) {
    setRangeTime(data);
  }

  function getNewWorkSpace(value: string) {
  }

  return (
    <div className={style.overviewPage}>
      <MagiRefeshContainer
        hasWorkSpace={false}
        context={{ range, env: 'prod' }}
        getNewWorkSpace={getNewWorkSpace}
        workspace={''}
        onChange={onContextChange}
      />
      <div className={style.container}>
        <DataSources
          workspace={''}
          cloudRun={false}
          rangeTime={rangeTime}
          app={app}
        />
        <ProCard
          title={$i18n.get({
            id: 'holoinsight.applicationMonitor.overview.ApplicationTopology',
            dm: '应用拓扑图',
          })}
          headerBordered
          extra={
            hasTopo ? (
              <div>
                <span className={style.depth}>
                  {$i18n.get({
                    id: 'holoinsight.applicationMonitor.overview.TopologyDepth',
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
            ) : null
          }
        >
          {hasTopo ? (
            <AppTopo topoData={topoData} rangeTime={rangeTime} deep={deep} />
          ) : (
            <AccessDocument />
          )}
        </ProCard>
      </div>
    </div>
  );
};
export default memo(Overview);
