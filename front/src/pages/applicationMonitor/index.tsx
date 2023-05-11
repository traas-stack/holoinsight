import AccessDocument from '@/components/AccessDocument';
import { CUSTEM_TEMPLATE_MAGI } from '@/layout/const';
import { queryMenu } from '@/services/app/api';
import { getTenantAppByCondition, metricDataQuery } from '@/services/infra/api';
import {
  AutoComplete,
  Button,
  Card,
  Drawer,
  Input,
  Progress,
  Radio,
  Tag,
  Tooltip,
} from 'antd';
import _ from 'lodash';
import { ContainerOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { useRef, useState } from 'react';
import { history } from 'umi';
import $i18n from '../../i18n';
import styles from './index.less';
import ApplicationTopo from './overview/applicationTopo';

export type TableListItem = {
  id: number;
  groupName: string;
  groupInfo: string;
  modifier: string;
  gmtModified: any;
  gmtCreate: any;
};

export default () => {
  const actionRef = useRef();
  const [requestName, setRequestName] = useState('');
  const [view, setView] = useState('table');
  const [openDrawer, setOpenDrawer] = useState<boolean>(false);
  const [appList, setAppList] = useState<any>([]);
  async function getMenu(app: string) {
    const menuObj: any = await queryMenu(app);
    const firstUrl = menuObj?.[0]?.url || '';
    const firstPath = {
      url: `${
        CUSTEM_TEMPLATE_MAGI.includes(firstUrl)
          ? '/app/' + firstUrl
          : '/app/dashboard/' + firstUrl
      }`,
      id: menuObj?.[0]?.templateId,
    };
    return firstPath;
  }

  const APM_HEAD:any = [];
  async function changeUrl(record: any) {
    const path = await getMenu(record?.app || '');
    const url =  `${path.url}?app=${record.app}&id=${path.id}`;
    console.log('url',url);
    history.push(url);
  }
  const columns: ProColumns<TableListItem>[] = [
    {
      title: $i18n.get({
        id: 'holoinsight.pages.applicationMonitor.Name',
        dm: '名称',
      }),
      dataIndex: 'app',
      key: 'app',
      width: 300,
      fixed: 'left',
      render: (v, record: any) => {
        return (
          <>
            <Button
              type="link"
              size="small"
              onClick={async () => {
                changeUrl(record);
              }}
            >
              {v}
            </Button>
          </>
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.applicationMonitor.UpdateTime',
        dm: '更新时间',
      }),
      width: 150,
      dataIndex: '_modified',
      key: '_modified',
      valueType: 'dateTime',
    },
    {
      title: 'CPU',
      hideInSearch: true,
      dataIndex: 'cpu_util',
      key: 'cpu_util',
      width: 150,
      render: (text: any) => {
        if (text && text?.[0]?.[1]) {
          return <Progress steps={3} percent={_.ceil(text?.[0]?.[1], 2)} />;
        }

        return (
          <Tag>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.NoDataAvailable',
              dm: '暂无数据',
            })}
          </Tag>
        );
      },
    },

    {
      title: 'MEM',
      dataIndex: 'mem_util',
      hideInSearch: true,
      key: 'mem_util',
      width: 150,
      render: (text: any) => {
        if (text && text?.[0]?.[1]) {
          return <Progress steps={3} percent={_.ceil(text?.[0]?.[1], 2)} />;
        }
        return (
          <Tag>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.NoDataAvailable',
              dm: '暂无数据',
            })}
          </Tag>
        );
      },
    },
    ...APM_HEAD,
    {
      title: $i18n.get({
        id: 'holoinsight.pages.applicationMonitor.Operation',
        dm: '操作',
      }),
      // dataIndex: 'app',
      width: 100,
      fixed: 'right',
      render: (v, record) => {
        return (
          <>
            <Button
              type="link"
              size="small"
              onClick={() => {
                changeUrl(record);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.applicationMonitor.View',
                dm: '查看',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  function handleSearchName(value:any) {
    setRequestName(value);
    actionRef.current.reload();
  }

  const metricDataQueryFunc = async (dataResult: any[]) => {
    let metricList;
    let metricIpMap = {};
    const endTime = new Date().getTime() - 120000;
    metricList =[
          'system_cpu_util',
          'system_mem_util',
          'k8s_pod_cpu_util',
          'k8s_pod_mem_util',
        ];

    const res = await metricDataQuery({
      datasources: metricList.map((item, index) => {
        const filters:any =[];
        return {
          metric: item,
          aggregator: 'avg',
          filters: filters,
          groupBy: ['app'],
          start: endTime - 180000,
          end: endTime,
        };
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
    const ddata = _.orderBy(
      dataResult.map((data: any) => {
        if (data && data['app'] && metricIpMap[data['app']]) {
          data = Object.assign({ ...data }, metricIpMap[data['app']]);
        }
        return data;
      }),
      ['app'],
      ['asc'],
    );

    return ddata;
  };
  return (
    <>
      <div
        style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '10px' }}
      >
        {$i18n.get({
          id: 'holoinsight.pages.applicationMonitor.ApplicationMonitoring',
          dm: '应用监控',
        })}
      </div>
      <Card>
        <div className={styles.appContainer}>
          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.ApplicationList',
              dm: '应用列表',
            })}
          </div>
          <div className={styles.right}>
            <AutoComplete
              dropdownMatchSelectWidth={252}
              style={{ width: 300 }}
              options={appList}
              onSelect={(value) => {
                handleSearchName(value);
              }}
              filterOption={(inputValue, option: any) =>
                option!.value
                  .toUpperCase()
                  .indexOf(inputValue.toUpperCase()) !== -1
              }
              onClear={() => {
                handleSearchName('');
              }}
              allowClear
            >
              <Input
                placeholder={$i18n.get({
                  id: 'holoinsight.pages.applicationMonitor.EnterAnApplicationName',
                  dm: '请输入应用名称',
                })}
              />
            </AutoComplete>
            <div className={styles.changeView}>
              <Radio.Group
                key={'back'}
                defaultValue="table"
                value={view}
                onChange={(e) => {
                  setView(e.target.value);
                }}
                style={{ marginRight: '10px' }}
              >
                <Radio.Button value="table">
                  <Tooltip
                    title={$i18n.get({
                      id: 'holoinsight.pages.applicationMonitor.TileList',
                      dm: '平铺列表',
                    })}
                  >
                    <ContainerOutlined />
                  </Tooltip>
                </Radio.Button>
              </Radio.Group>
            </div>
            <Button onClick={() => setOpenDrawer(true)}>
              {$i18n.get({
                id: 'holoinsight.pages.applicationMonitor.ApplicationAccess',
                dm: '应用接入',
              })}
            </Button>
            <Drawer
              title={$i18n.get({
                id: 'holoinsight.pages.applicationMonitor.ApplicationAccessGuide',
                dm: '应用接入指南',
              })}
              open={openDrawer}
              closable={false}
              size={'large'}
              onClose={() => setOpenDrawer(false)}
            >
              <AccessDocument />
            </Drawer>
          </div>
        </div>
        {view === 'table' ? (
          <ProTable<TableListItem>
            actionRef={actionRef}
            columns={columns}
            scroll={{ x: 1500 }}
            request={async (params, sorter, filter) => {
              let data: any;
              const otherInfo: any =[];
              let condition: any = {};
              if (requestName) {
                condition['app'] = [requestName];
              }

              data = await getTenantAppByCondition(condition);
              data = await metricDataQueryFunc(data);
              if (Array.isArray(otherInfo)) {
                otherInfo.forEach((item: any) => {
                  let flag = false;
                  data.forEach((element: any) => {
                    if (item.name === element.app && !item.new) {
                      element.metric = item.metric;
                      flag = true;
                    }
                  });
                  if (!flag) {
                    item.app = item.name;
                    item.new = true;
                    data.push(item);
                  }
                });
              }
              if (!requestName) {
                setAppList(
                  data.map((item:any) => {
                    return {
                      label: item.app,
                      value: item.app,
                    };
                  }),
                );
              }
              return {
                data: data,
                success: true,
                total: data.length,
              };
            }}
            rowKey="key"
            pagination={{
              showQuickJumper: true,
              showSizeChanger: true,
            }}
            dateFormatter="string"
            search={false}
            options={false}
          />
        ) : (
          <ApplicationTopo />
        )}
      </Card>
    </>
  );
};
