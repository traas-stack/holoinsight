import { Divider, Dropdown,Radio, Space, Typography } from 'antd';
import React, { memo, useEffect, useMemo, useState } from 'react';

import Icon from '@/components/Icon/IconFont';
import { FILTER_DIMENSION } from '@/pages/applicationMonitor/APIMonitor/APIList/const';
import { getEndpointList, getStandAloneList } from '@/services/infra/api';
import { useRequest } from 'ahooks';
import clsx from 'clsx';
import _, { isNumber } from 'lodash';
import $i18n from '../../../../../i18n';
import style from './index.less';
import './index.less';
export interface Props {
  title: string;
  type: string
  compType: string;
  setEndPoint: (endPoint: string) => void;
  changePointerName: (value: string) => void;
  endPoint: string;
  time?: any;
}
const { Paragraph } = Typography;
//   <Menu
//     items={[
//       {
//         key: '1',
//         label: '接口维度',
//       },
//       {
//         key: '2',
//         label: '方法维度',
//       },
//     ]}
//   />
// );
export type FilterKey = keyof typeof FILTER_DIMENSION;
function MetricDes(props: {
  t1: string | number;
  t2: string | number;
  t3: string | number;
  active: string | null | undefined;
}) {
  return (
    <p className={style.metric}>
      <span
        className={clsx({
          [style.filterKeyActive]:
            props.active === FILTER_DIMENSION.totalCount.key,
        })}
      >
        {props.t1}
      </span>{' '}
      <Divider type={'vertical'} />
      <span
        className={clsx({
          [style.filterKeyActive]:
            props.active === FILTER_DIMENSION.errorCount.key,
        })}
      >
        {props.t2}
      </span>{' '}
      <Divider type={'vertical'} />
      <span
        className={clsx({
          [style.filterKeyActive]:
            props.active === FILTER_DIMENSION.avgLatency.key,
        })}
      >
        {props.t3}
      </span>
    </p>
  );
}
const Sider: React.FC<Props> = ({
  title,
  setEndPoint,
  endPoint,
  changePointerName,
  compType,
  time,
}) => {
  const listtime = {
    start: time.starttime,
    end: time.endtime,
  };

  const [filterKey, setFilterKey] = useState<FilterKey>(
    FILTER_DIMENSION.errorCount.key as FilterKey,
  );

  const { run, data = [] } = useRequest(
    compType === 'standAlone' ? getStandAloneList : getEndpointList,
    {
      manual: true,
      onSuccess: (data) => {
        if (data.length) {
          setEndPoint(data[0].name);
        }
      },
    },
  );

  const filteredData = useMemo(() => {
    const name = data?.[0]?.name || '';
    if (name) {
      setEndPoint(name);
      changePointerName && changePointerName(name);
    }
    if (FILTER_DIMENSION.totalCount.key === filterKey) {
      return _.orderBy(
        data,
        (e) => {
          return e.metric.totalCount;
        },
        'desc',
      );
    }
    if (FILTER_DIMENSION.errorCount.key === filterKey) {
      return _.orderBy(
        data,
        (e) => {
          return e.metric.errorCount;
        },
        'desc',
      );
    }
    if (FILTER_DIMENSION.avgLatency.key === filterKey) {
      return _.orderBy(
        data,
        (e) => {
          return e.metric.avgLatency;
        },
        'desc',
      );
    }
    return data;
  }, [data, filterKey]);

  const allData = useMemo(() => {
    const all = {
      totalCount: 0,
      errorCount: 0,
      avgLatency: 0,
    };
    (Array.isArray(data) ? data : []).forEach((item) => {
      if (isNumber(item.metric.totalCount)) {
        all.totalCount += item.metric.totalCount;
      }
      if (isNumber(item.metric.errorCount)) {
        all.errorCount += item.metric.errorCount;
      }
      if (isNumber(item.metric.avgLatency)) {
        all.avgLatency += item.metric.avgLatency;
      }
    });

    return all;
  }, [data]);

  useEffect(() => {
    run({
      ...listtime,
      serviceName: title,
    });
  }, [JSON.stringify(listtime)]);

  let filterObj = [
    { ...FILTER_DIMENSION.totalCount },
    { ...FILTER_DIMENSION.errorCount },
    { ...FILTER_DIMENSION.avgLatency },
  ]
  const items = filterObj.map((item) => {
    return {
      key: item.key,
      label: (
      <div
        onClick={() => {
          setFilterKey(item.key as FilterKey);
        }}
      >
        {item.label}
      </div>
      ),
    }
  })

  return (
    <div className={style.sider}>
      <div className={style.nav}>
        <Space className={style.Dropdown}>
          {$i18n.get({
            id: 'holoinsight.APIList.Sider.InterfaceDimension',
            dm: '接口维度',
          })}
        </Space>
        <br />
        <Dropdown
          menu={{
            items, selectable: true,
            defaultSelectedKeys: [filterKey],
          }}
          trigger={['click']}
        >
          <Space className={style.filterMenu}>
            <MetricDes
              t1={$i18n.get({
                id: 'holoinsight.APIList.Sider.NumberOfRequests',
                dm: '请求数',
              })}
              t2={$i18n.get({
                id: 'holoinsight.APIList.Sider.NumberOfErrors',
                dm: '错误数',
              })}
              t3={$i18n.get({
                id: 'holoinsight.APIList.Sider.AverageLatency',
                dm: '平均延迟',
              })}
              active={filterKey}
            />
            <Icon type="icon-paixu" />
          </Space>
        </Dropdown>
      </div>
      <div className={style.content}>
        <Radio.Group
          value={endPoint}
          onChange={(e) => {
            setEndPoint(e.target.value);
          }}
        >
          <ul>
            {filteredData.map((item) => {
              return (
                <li
                  key={item.name}
                  className={clsx(style.listItem, {
                    [style.active]: item.name === endPoint,
                  })}
                >
                  <Radio value={item.name}>
                    <div>
                      <Paragraph ellipsis={true} copyable={true}>
                        {item.name}
                      </Paragraph>
                      <p className={style.metric}>
                        <MetricDes
                          t1={item.metric.totalCount}
                          t2={item.metric.errorCount}
                          t3={`${_.ceil(item.metric.avgLatency || 0, 2)}ms`}
                          active={filterKey}
                        />
                      </p>
                    </div>
                  </Radio>
                </li>
              );
            })}
          </ul>
        </Radio.Group>
      </div>
    </div>
  );
};
export default memo(Sider);
