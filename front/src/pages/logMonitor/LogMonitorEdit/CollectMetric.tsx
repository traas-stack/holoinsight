import { useState } from 'react';
import {
  Card,
  Form,
  Divider,
  Table,
  Tag,
  Popconfirm,
} from 'antd';
import DrawerIndicators from './DrawerIndicators';
import _ from 'lodash';
import $i18n from '../../../i18n';

interface IProps {
  form: any;
  id: any;
  tenant?: string;
}

const CollectMetric = (props: IProps) => {
  const { form, id, tenant } = props;
  const [counter, setCounter] = useState<number>(0);

  // 切分表格 删除
  const handleDelete = (index: number) => {
    const collectMetrics = form.getFieldValue('collectMetrics') || [];
    collectMetrics.splice(index, 1);
    form.setFieldsValue({ collectMetrics });
    setCounter(counter + 1);
  };

  const handleAdd = (record: any) => {
    let collectMetrics = _.cloneDeep(
      form.getFieldValue('collectMetrics') || [],
    );
    let existed = false;
    for (let i = 0; i < collectMetrics.length; i++) {
      if (collectMetrics[i].tableName === record.tableName) {
        collectMetrics[i] = record;
        existed = true;
      }
    }

    if (!existed) {
      collectMetrics.push(record);
    }
    form.setFieldsValue({ collectMetrics });
    setCounter(counter + 1);
  };
  const columnindicators: any = [
    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.MonitorName',
        dm: '监控项名称',
      }),
      dataIndex: 'tableName',
      key: 'tableName',
      render: (v: any, record: any) => {
        if (record.name) {
          return v;
        } else {
          if (id) {
            return v + '_' + `${id}`
          }
          return v;
        }
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.IndicatorSelection',
        dm: '指标选择',
      }),
      dataIndex: 'metricType',
      key: 'metricType',
      render: (v: any) => {
        switch (v) {
          case 'select':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.ValueSelection',
                  dm: '数值选择',
                })}
              </Tag>
            );
          case 'count':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.TrafficStatistics',
                  dm: '流量统计',
                })}
              </Tag>
            );
          case 'contains':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.KeywordStatistics',
                  dm: '关键字统计',
                })}
              </Tag>
            );
        }

        return <></>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.TagDimension',
        dm: 'TAG维度',
      }),
      dataIndex: 'tags',
      key: 'tags',
      render: (v: any) => v?.map((item: any) => <Tag key={item}> {item}</Tag>),
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.CalculationMethod',
        dm: '计算方式',
      }),
      dataIndex: 'func',
      key: 'func',
      render: (v: any) => {
        switch (v) {
          case 'sum':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.Summation',
                  dm: '求和',
                })}
              </Tag>
            );
          case 'count':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.NumberOfRows',
                  dm: '求行数',
                })}
              </Tag>
            );
          case 'max':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.FindTheMaximumValue',
                  dm: '求最大值',
                })}
              </Tag>
            );
          case 'min':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.FindTheMinimumValue',
                  dm: '求最小值',
                })}
              </Tag>
            );
          case 'avg':
            return (
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.Average',
                  dm: '求平均值',
                })}
              </Tag>
            );
        }

        return <></>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.Operation',
        dm: '操作',
      }),
      dataIndex: 'hand',
      key: 'hand',
      render: (field: any, record: any, index: number) => {
        return (
          <>
            <DrawerIndicators
              id={id}
              tenant={tenant}
              edit={true}
              splitForm={form}
              tableInfo={record}
              title={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.Edit',
                dm: '编辑',
              })}
              onChange={(val) => handleAdd(val)}
            />

            <Divider type="vertical" />

            <Popconfirm
              title={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.AreYouSureYouWant',
                dm: '确定删除？',
              })}
              onConfirm={() => {
                handleDelete(index, record);
              }}
            >
              <a>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.Delete',
                  dm: '删除',
                })}
              </a>
            </Popconfirm>
          </>
        );
      },
    },
  ];

  return (
    <>
      <Card
        title={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.IndicatorDefinition',
          dm: '指标定义',
        })}
        style={{ marginTop: '20px' }}
      >
        <Form.Item noStyle shouldUpdate={(pre, next) => pre.logPattern !== next.logPattern}>
          {
            ({ getFieldValue }) => {
              const dataSource = getFieldValue('collectMetrics');
              return (
                <>
                  <Form.Item wrapperCol={{ span: 24 }}>
                    <DrawerIndicators
                      edit={false}
                      splitForm={form}
                      title={$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.AddMonitoringMetrics',
                        dm: '新增监控指标',
                      })}
                      tableInfo={{}}
                      onChange={(val) => handleAdd(val)}
                    />
                  </Form.Item>

                  <Form.Item
                    name="collectMetrics"
                    rules={[
                      {
                        required: true,
                        message: $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.CollectMetric.PleaseConfigureTheCollectionIndicator',
                          dm: '请配置采集指标表',
                        }),
                      },
                    ]}
                    wrapperCol={{ span: 24 }}
                  >
                    <Table
                      bordered={false}
                      columns={columnindicators}
                      dataSource={dataSource}
                      pagination={false}
                    />
                  </Form.Item>
                </>
              )
            }
          }

        </Form.Item>
      </Card>
    </>
  );
};
export default CollectMetric;
