import {
  Button,
  Card,
  Select,
  Table,
  Tabs,
  Tooltip,
} from 'antd';
import React, { useEffect, useState } from 'react';
import type { ColumnsType } from 'antd/lib/table';
import LineTpChart from '@/components/Chart/linkTpChart';
import CallLinkTable from '@/pages/applicationMonitor/callLink/CallLinkTable';
import {
  getComponentMonitor,
  getTopoDataDetail,
} from '@/services/application/api';
import { getQueryString, getTreeData } from '@/utils/help';
import $i18n from '../../../i18n';
import CallAnalyzer from './CallAnalyzer';
import styles from './index.less';

export interface TableListItem {
  id: string;
  componentId: string | null;
  destId: string;
  destName: string;
  sourceId: string;
  sourceName: string;
  metric: Record<string, any>;
  [key: string]: any;
}

const { TabPane } = Tabs;

const tabsName = [
  {
    name: $i18n.get({
      id: 'holoinsight.App.componentMonitor.DatabaseMonitoring',
      dm: '数据库监控',
    }),
    key: '1',
  },
  {
    name: $i18n.get({
      id: 'holoinsight.App.componentMonitor.CacheMonitoring',
      dm: '缓存监控',
    }),
    key: '2',
  },
  {
    name: $i18n.get({
      id: 'holoinsight.App.componentMonitor.MessageQueue',
      dm: '消息队列',
    }),
    key: '3',
  },
];

const ComponentMonitor: React.FC<any> = () => {
  let treeNode: any = {};
  const [typeObj, setTypeObj] = useState<any>({
    type: [],
    addressList: {},
  });
  const [checkValue, setCheckValue] = useState({
    type: '', //类型
    address: '', //地址
  });
  const [chatData, setChartData] = useState(null); //topo图数据
  const [sqlList, setSqlList] = useState<any>([]); //topo图数据
  const [isCallAnalyzer, setIsCallAnalyzer] = useState<boolean>(false); //traceIds数据
  const [sqlRecord, setSqlRecord] = useState<TableListItem | null>(null); //topo图数据
  const [type, setType] = useState('1');
  const [activeKey, setActiveKey] = useState('sql');
  const app = getQueryString('app');
  useEffect(() => {
    const category = type === '1' ? 'db' : type === '2' ? 'cache' : 'mq';
    getComponentMonitor({
      serviceName: app,
      category: category,
    }).then((res: any) => {
      if (Array.isArray(res)) {
        const newObj = typeObj;
        (res || []).forEach((element: any) => {
          newObj.type.push(element.type);
          const hasAddress = newObj?.addressList?.[element.type];
          if (hasAddress) {
            hasAddress?.push({
              label: element.address,
              value: element.address,
            });
          } else {
            newObj.addressList[element.type] = [];
            newObj.addressList[element.type].push({
              label: element.address,
              value: element.address,
            });
          }
        });
        newObj.type = Array.from(new Set(newObj.type)).map((item) => {
          return {
            label: item,
            value: item,
          };
        });
        setTypeObj(JSON.parse(JSON.stringify(typeObj)));
        if (typeObj.type.length && Object.keys(typeObj.addressList).length) {
          setCheckValue({
            type: typeObj.type[0].value,
            address: typeObj.addressList[typeObj.type[0].value][0].value,
          });
        }
      }
    });
  }, [type]);

  async function getTopoData(type: string, address: string) {
    getTopoDataDetail({
      address: address,
      category: type === '1' ? 'db' : type === '2' ? 'cache' : 'mq',
    }).then((res) => {
      if (Object.prototype.toString.call(res) === '[object Object]') {
        getTreeData(res, treeNode, address);
        setChartData(treeNode);
        setSqlList(res);
      }
    });
  }
  useEffect(() => {
    if (checkValue.address) {
      getTopoData(type, checkValue.address);
    }
  }, [checkValue.address]);


  const sqlColumns: ColumnsType<TableListItem> = [
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.CallInformation',
        dm: '调用信息',
      }),
      dataIndex: 'sourceName',
      key: 'sourceName',
      width: 240,
      render: (text, record) => {
        return (
          <div style={{ textAlign: 'center' }}>
            <Tooltip title={text}>
              <div className={styles.sourceName}>{text}</div>
            </Tooltip>
            <div>↓</div>
            <Tooltip title={record.destName}>
              <div className={styles.sourceName}>{record.destName}</div>
            </Tooltip>
          </div>
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.AverageResponseTime',
        dm: '平均响应时间',
      }),
      dataIndex: 'metric',
      key: 'avgLatency',
      width: 200,
      sorter: (a, b) => a.metric.avgLatency - b.metric.avgLatency,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{text?.avgLatency.toFixed(1)}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.PResponseTime',
        dm: 'P95响应时间',
      }),
      dataIndex: 'metric',
      key: 'p95Latency',
      width: 200,
      sorter: (a, b) => a.metric.p95Latency - b.metric.p95Latency,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{text?.p95Latency.toFixed(1)}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.PResponseTime.1',
        dm: 'P99响应时间',
      }),
      dataIndex: 'metric',
      key: 'p99Latency',
      width: 200,
      sorter: (a, b) => a.metric.p99Latency - b.metric.p99Latency,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{text?.p99Latency.toFixed(1)}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.TotalNumberOfTimes',
        dm: '总次数',
      }),
      dataIndex: 'metric',
      key: 'totalCount',
      width: 200,
      sorter: (a, b) => a.metric.totalCount - b.metric.totalCount,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{text?.totalCount}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.NumberOfErrors',
        dm: '错误数',
      }),
      dataIndex: 'metric',
      key: 'errorCount',
      width: 200,
      sorter: (a, b) => a.metric.errorCount - b.metric.errorCount,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{text?.errorCount}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.SuccessRate',
        dm: '成功率',
      }),
      dataIndex: 'metric',
      key: 'successRate',
      width: 200,
      sorter: (a, b) => a.metric.successRate - b.metric.successRate,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{Math.floor(text?.successRate * 1000) / 1000} </span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.App.componentMonitor.Operation',
        dm: '操作',
      }),
      dataIndex: 'handle',
      key: 'handle',
      render: (text, record) => {
        return (
          <>
            <Button
              type="link"
              onClick={() => {
                setSqlRecord(record);
                setIsCallAnalyzer(true);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.App.componentMonitor.CallStatistics',
                dm: '调用统计',
              })}
            </Button>
            <Button
              type="link"
              onClick={() => {
                setSqlRecord(record);
                setActiveKey('call');
              }}
            >
              {$i18n.get({
                id: 'holoinsight.App.componentMonitor.CallChainQuery',
                dm: '调用链查询',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  return (
    <div>
      {/* <div style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '10px' }}>
                 组件监控
             </div> */}
      <Card>
        <Tabs
          defaultActiveKey="1"
          size="large"
          onChange={(value) => {
            setType(value);
            checkValue.type = '';
            checkValue.address = '';
            setCheckValue({ ...checkValue });
            setTypeObj({
              type: [],
              addressList: {},
            });
            setChartData(null);
            setActiveKey('sql');
            setSqlList([]);
            setSqlRecord(null);
          }}
        >
          {tabsName.map((item: any) => {
            return (
              <TabPane tab={item.name} key={item.key}>
                <Card>
                  {type === '1'
                    ? $i18n.get({
                        id: 'holoinsight.App.componentMonitor.DatabaseType',
                        dm: '数据库类型：',
                      })
                    : type === '2'
                    ? $i18n.get({
                        id: 'holoinsight.App.componentMonitor.CacheType',
                        dm: '缓存类型：',
                      })
                    : $i18n.get({
                        id: 'holoinsight.App.componentMonitor.MessageType',
                        dm: '消息类型：',
                      })}

                  <Select
                    style={{ width: 160 }}
                    value={checkValue.type}
                    onChange={(value) => {
                      checkValue.type = value;
                      checkValue.address =
                        typeObj?.addressList?.[checkValue.type] || [];
                      setCheckValue({ ...checkValue });
                    }}
                    defaultValue={typeObj?.type?.[0]?.value}
                    options={typeObj.type}
                  ></Select>
                  <Select
                    style={{ width: 360 }}
                    value={checkValue.address}
                    onChange={(value) => {
                      checkValue.address = value;
                      setCheckValue({ ...checkValue });
                    }}
                    placeholder={$i18n.get({
                      id: 'holoinsight.App.componentMonitor.PleaseSelect',
                      dm: '请选择',
                    })}
                    options={typeObj?.addressList?.[checkValue.type] || []}
                  ></Select>
                </Card>
                <Card className={styles.mgTop}>
                  <h3>
                    {$i18n.get({
                      id: 'holoinsight.App.componentMonitor.OverviewOfDatabaseCalls',
                      dm: '数据库调用概览',
                    })}
                  </h3>
                  {chatData ? <LineTpChart treeNode={chatData} /> : null}
                </Card>
                <Card className={styles.mgTop}>
                  <Tabs
                    activeKey={activeKey}
                    onChange={(activeKey) => setActiveKey(activeKey)}
                    items={[
                      {
                        label: $i18n.get({
                          id: 'holoinsight.App.componentMonitor.CallAnalysis',
                          dm: '调用分析',
                        }),
                        key: 'sql',
                        children: (
                          <>
                            <div
                              style={{
                                display: isCallAnalyzer ? 'none' : 'block',
                              }}
                            >
                              <Table
                                columns={sqlColumns}
                                dataSource={sqlList?.calls || []}
                              />
                            </div>
                            {isCallAnalyzer && (
                              <CallAnalyzer
                                sqlRecord={sqlRecord}
                                setIsCallAnalyzer={setIsCallAnalyzer}
                              />
                            )}
                          </>
                        ),
                      },
                      {
                        label: $i18n.get({
                          id: 'holoinsight.App.componentMonitor.CallLinkQuery',
                          dm: '调用链路查询',
                        }),
                        key: 'call',
                        children: (
                          <div>
                            <CallLinkTable
                              key={type}
                              type="componentMonitor"
                              sqlRecord={sqlRecord}
                              address={checkValue.address}
                            />
                          </div>
                        ),
                      },
                    ]}
                  />
                </Card>
              </TabPane>
            );
          })}
        </Tabs>
      </Card>
    </div>
  );
};
export default ComponentMonitor;
