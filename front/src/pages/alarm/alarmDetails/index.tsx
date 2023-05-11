import React, { useState, useEffect } from 'react';
import {
  PageHeader,
  Button,
  Descriptions,
  Tabs,
  Radio,
  Table,
  message,
} from 'antd';
import { history } from 'umi';
import styles from './index.less';
import {
  alarmRuleQueryById,
} from '@/services/alarm/api';
import type { ProColumns } from '@ant-design/pro-table';
import qs from 'query-string';
import AlarmHistory from '../alarmHistory';
import CommonBreadcrumb from '@/components/CommonBreadcrumb';
import $i18n from '../../../i18n';
import PQlEditor from '@/components/PQLEditor';
import {
  UnorderedListOutlined,
} from '@ant-design/icons';
import { useSearchParams, useParams } from '@umijs/max';
const { TabPane } = Tabs;
export type TableListItem = {
  id: number;
  alarmContent: string;
  noticeType: string;
  alarmLevel: string;
  alarmTime: any;
  recoverTime: any;
  duration: any;
  alarmUniqueId: string;
};

interface alarmDetailData {
  alarmLevel: string;
  status: number;
  creator: string;
  modifier: string;
  gmtCreate: string;
  gmtModified: string;
  isMerge: number;
  ruleName: string;
}

const alramLevel = {
  '1': $i18n.get({
    id: 'holoinsight.alarm.DetailsAlarm.Emergency',
    dm: '紧急',
  }),
  '2': $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Serious', dm: '严重' }),
  '3': $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.High', dm: '高' }),
  '4': $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Medium', dm: '中' }),
  '5': $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Low', dm: '低' }),
};

const alarmStatus = {
  0: $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Disable', dm: '禁用' }),
  1: $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Enable', dm: '启用' }),
};

const isMerge = {
  0: $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.No', dm: '否' }),
  1: $i18n.get({ id: 'holoinsight.alarm.DetailsAlarm.Yes', dm: '是' }),
};

const DetailsAlarm = (props: any) => {
  const { id } = props.match.params;
  const [backData, setBackData] = useState<alarmDetailData>({
    ruleName: '',
    alarmLevel: '',
    status: 0,
    creator: '',
    modifier: '',
    gmtCreate: '',
    gmtModified: '',
    isMerge: 0,
  });

  const [boolType, setBoolType] = useState<String>('');
  const [isPql, setIsPql] = useState(false);
  const [tableData, setTableData] = useState([]); //规则表格数据列表
  const [searchParams] = useSearchParams();
  const { app: appName, id: urlId } = qs.parse(location.search)
  const changeBreadcrumb = searchParams.get('changeBreadcrumb') === 'true'
  const paramsId = useParams().id
  const breadcrumbObj = () => {
    if (appName) {
      return {
        name: appName,
        url: `/app/overview?app=${appName}&id=${urlId}`
      }
    } else {
      return changeBreadcrumb ? {
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.UserAlarmSub',
          dm: '告警订阅'
        }),
        url: '/user/alarmsub'
      }
        : {
          name: $i18n.get({
            id: 'holoinsight.pages.alarm.Detail.RuleManagement',
            dm: '规则管理'
          }),
          url: '/alarm/rule'
        }
    }
  }
  const listcolumns: any = [
    {
      title: $i18n.get({
        id: 'holoinsight.alarm.DetailsAlarm.MonitoringItems',
        dm: '监控项',
      }),
      dataIndex: 'metric',
      key: 'metric',
    },

    {
      title: $i18n.get({
        id: 'holoinsight.alarm.DetailsAlarm.TriggerCondition',
        dm: '触发条件',
      }),
      dataIndex: 'triggerContent',
      key: 'triggerContent',
    },
  ];

  const urlList = [
    breadcrumbObj(),
    {
      name: $i18n.get({
        id: 'holoinsight.alarm.DetailsAlarm.AlertDetails',
        dm: '告警详情',
      }),
      url: '',
    },
  ];


  useEffect(() => {
    alarmRuleQueryById(id).then((res: any) => {
      setBackData(res);
      const bool = res?.rule?.boolOperation;
      const isPql = res?.ruleType === 'pql';
      setIsPql(isPql);
      if (isPql) {
        setBoolType('AND');
      } else {
        setBoolType(bool);
      }

      let arr: any = [];
      (res?.rule?.triggers || []).forEach((item: any) => {

        let newItem: any = {};
        if (item.compareConfigs) {
          let str = "";
          if (item.compareConfigs.length > 1) {
            item.compareConfigs.forEach((ele: any, n: number) => {
              str = str + ` 条件${n + 1}: ${ele.triggerContent}.`;
            })
          } else {
            str = item.compareConfigs[0].triggerContent
          }
          newItem.triggerContent = str;
        } else {
          newItem.triggerContent = item.triggerContent;
        }
        newItem.metric = item?.datasources[0]?.metric;
        arr.push(newItem);
      });
      setTableData(arr);
    });
  }, [id]);
  function copyText(res) {
    navigator.clipboard.writeText(res).then(() => {
      message.success(
        '导出成功，JSON配置已复制到剪切板'
      );
    }).catch(() => {
      message.error(
        '导出失败'
      );
    })
  }
  const copyJson = async () => {
    const res = await alarmRuleQueryById(paramsId)
    delete res.id
    copyText(JSON.stringify(res))
  }
  return (
    <>
      <CommonBreadcrumb urlList={urlList} />

      <PageHeader
        title={$i18n.get(
          {
            id: 'holoinsight.alarm.DetailsAlarm.AlarmConfigurationNameBackdatarulename',
            dm: '报警配置名称: {backDataRuleName}',
          },
          { backDataRuleName: backData.ruleName },
        )}
        extra={
          <div>
            <Button
              className={styles.mgr10}
              onClick={() => {
                history.push({
                  pathname: `/alarm/view/${id}`,
                  search: qs.stringify({
                    from: 'alarm'
                  }),
                });
              }}
              icon={<UnorderedListOutlined />}
            >
              {$i18n.get({ id: 'holoinsight.pages.logMonitor.Metric.AlertRules', dm: '告警规则' })}
            </Button>
            <Button style={{ marginLeft: 10 }} onClick={copyJson} type="primary">导出配置</Button>
          </div>
        }
      >
        <Descriptions size="middle" column={3}>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.AlarmLevel.1',
              dm: '报警级别',
            })}
          >
            {alramLevel?.[backData.alarmLevel]}
          </Descriptions.Item>

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.AlarmStatus',
              dm: '报警状态',
            })}
          >
            {alarmStatus?.[backData.status]}
          </Descriptions.Item>

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.Founder',
              dm: '创建人',
            })}
          >
            {backData.creator}
          </Descriptions.Item>

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.Modifier',
              dm: '修改人',
            })}
          >
            {backData.modifier}
          </Descriptions.Item>

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.CreationTime',
              dm: '创建时间',
            })}
          >
            {backData.gmtCreate}
          </Descriptions.Item>

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.ModificationTime',
              dm: '修改时间',
            })}
          >
            {backData.gmtModified}
          </Descriptions.Item>

          {/* <Descriptions.Item label="生效时间">{ }</Descriptions.Item> */}

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.MergeOrNot',
              dm: '是否合并',
            })}
          >
            {isMerge?.[backData.isMerge]}
          </Descriptions.Item>
        </Descriptions>

        <Tabs defaultValue={'1'}>
          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.AlarmRules',
              dm: '报警规则',
            })}
            key="1"
          >
            <div className={styles['alarmRulerDetail-condition']}>
              <Radio.Group value={boolType} disabled>
                <Radio value="AND">
                  {$i18n.get({
                    id: 'holoinsight.alarm.DetailsAlarm.MeetAtTheSameTime',
                    dm: '同时满足',
                  })}
                </Radio>
                <Radio value="OR">
                  {$i18n.get({
                    id: 'holoinsight.alarm.DetailsAlarm.OnlyOneCanBeSatisfied',
                    dm: '满足一条即可',
                  })}
                </Radio>
              </Radio.Group>
            </div>
            {isPql ? (
              <PQlEditor
                width={'100%'}
                height={200}
                readOnly
                value={backData?.pql || ''}
              />
            ) : (
              <Table
                columns={listcolumns}
                dataSource={tableData}
                rowClassName={styles.tableColorDust}
                bordered
                pagination={false}
              />
            )}
          </TabPane>

          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.alarm.DetailsAlarm.AlarmHistory',
              dm: '报警历史',
            })}
            key="2"
          >
            {/* <ProTable<TableListItem>
               columns={columns}
               request={async (params, sorter, filter) => {
                 let gmtObj: any = {};
                 gmtObj.sortBy = Object.keys(sorter)[0];
                 gmtObj.sortRule = sorter[Object.keys(sorter)[0]] === 'ascend' ? 'asc' : 'desc'
                 const pageRequest = {
                   pageNum: params.current,
                   pageSize: params.pageSize,
                   ...gmtObj,
                   target: { ...params, ...{ uniqueId: `rule_${id}` } },
                  };
                 const data = await pageQueryAlarmHistory(pageRequest);
                 return {
                   data: data?.items,
                   success: true,
                   total: data.totalCount
                 };
               }}
               rowKey="key"
               pagination={{
                 showQuickJumper: true,
                 showSizeChanger: true,
               }}
               // toolBarRender={false}
               search={false}
               headerTitle=""
               options={false}
               dateFormatter="string"
               toolBarRender={() => []}
              /> */}

            <AlarmHistory from="Detail" id={id} backData={backData} />
          </TabPane>
        </Tabs>
      </PageHeader>
    </>
  );
};

export default DetailsAlarm;
