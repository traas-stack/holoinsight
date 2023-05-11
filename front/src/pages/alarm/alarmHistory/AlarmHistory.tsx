import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Card, Drawer, Tooltip } from 'antd';
import { useRef, useState } from 'react';
import { history } from 'umi';
import {
  alarmRuleQueryById,
  pageQueryAlarmHistory,
  pageQuerySubAlarmHistory,
} from '../../../services/alarm/api';
import { getQueryString } from '../../../utils/help';
import HistoryDrawer from '../historyDrawer';
import AlarmPanel from './AlarmPanel';
import PQlChart from './PQlChart';

import moment from 'moment';
import $i18n from '../../../i18n';
import './AlarmHistory.less';

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

export default (props: any) => {
  const initialState = props.initialState;
  const { from } = props;
  const historyId = getQueryString('historyId');
  const alarmTime = getQueryString('alarmTime');
  const isUser = window.location?.pathname === '/user/alarmhistory' || false;

  const [popView, setPopView] = useState('detail');
  const [visible, setVisible] = useState(historyId && alarmTime ? true : false);
  const [triggerArr, setTriggerArr] = useState([]);
  const [selectItem, setSelectItem] = useState({});
  const [pql, setPQL] = useState({});

  const actionRef = useRef();

  function handleSeePhoto(record: any) {
    setSelectItem(record);
    const { uniqueId = '', alarmTime, recoverTime } = record;
    const id = uniqueId.replace(/^rule_|pql_|ai_/, '');
    const isPQL = uniqueId.includes('pql');
    alarmRuleQueryById(id).then((res: any) => {
      if (res) {
        if (isPQL) {
          setPopView('pql');
          setPQL({
            pql: res.pql,
            startTime: moment(alarmTime).format('x'),
            endTime: moment(recoverTime).format('x'),
          });
          setVisible(true);
        } else {
          const triggers = res.rule?.triggers || [];
          setTriggerArr(triggers);
          setPopView('chart');
          setVisible(true);
        }
      }
    });
  }
  function handleDetailInfo(record: any) {
    setSelectItem(record);
    setPopView('detail');
    setVisible(true);
  }

  const columns: ProColumns<TableListItem>[] = [
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.RuleName',
        dm: '规则名称',
      }),
      width: 150,
      fixed: 'left',
      dataIndex: 'ruleName',
      key: 'ruleName',
      render: (text: any, record: any) => {
        const id = record?.uniqueId?.replace(/^rule_|pql_|ai_/, '');
        return (
          <a
            onClick={() => {
              history.push({
                pathname: `/alarm/view/${id}`,
              });
            }}
          >
            {text}
          </a>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.TriggerDetails',
        dm: '触发详情',
      }),
      key: 'detail',
      search: false,
      width: 400,
      dataIndex: 'triggerContent',
      render: (record: any) => {
        if (Array.isArray(record) && record.length > 1) {
          return (
            <Tooltip
              placement="topLeft"
              title={record.map((item, index) => (
                <p key={index}>{item}</p>
              ))}
            >
              {record[0] + '...'}
            </Tooltip>
          );
        } else {
          return record[0];
        }
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.AlarmTime',
        dm: '告警时间',
      }),
      width: 200,
      dataIndex: 'alarmTime',
      key: 'alarmTime',
      valueType: 'dateTime',
      onFilter: true,
      sorter: true,
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.Duration',
        dm: '持续时间',
      }),
      width: 100,
      dataIndex: 'duration',
      key: 'duration',
      search: false,
      render: (record: any) => {
        return (
          (record || 0) +
          $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.Minutes',
            dm: '分钟',
          })
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.RecoveryTime',
        dm: '恢复时间',
      }),
      width: 200,
      dataIndex: 'recoverTime',
      key: 'recoverTime',
      valueType: 'dateTime',
      search: false,
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.AlarmLevel',
        dm: '告警等级',
      }),
      width: 100,
      dataIndex: 'alarmLevel',
      key: 'alarmLevel',
      filters: true,
      onFilter: true,
      valueType: 'select',
      valueEnum: {
        1: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.Emergency',
            dm: '紧急',
          }),
        },

        2: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.Serious',
            dm: '严重',
          }),
        },

        3: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.High',
            dm: '高',
          }),
        },

        4: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.Medium',
            dm: '中',
          }),
        },

        5: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.AlarmHistory.Low',
            dm: '低',
          }),
        },
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmHistory.Operation',
        dm: '操作',
      }),
      key: 'option',
      width: 200,
      fixed: 'right',
      search: false,
      render: (record) => {
        return (
          <>
            <Button
              type="link"
              size="small"
              onClick={() => {
                handleSeePhoto(record);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.AlarmHistory.Chatu',
                dm: '查图',
              })}
            </Button>

            <Button
              type="link"
              size="small"
              onClick={() => {
                handleDetailInfo(record);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.AlarmHistory.Details',
                dm: '详情',
              })}
            </Button>

            {/* <Button type="link" size='small'
             onClick={() => { handleSeePhoto(record) }}
          >确认报警</Button> */}

            {/* {
             record.duration ?
                 <Popconfirm
                     overlayStyle={{ width: 240 }}
                     onConfirm={() => handleDeleteBlock(record)}
                     title="确定删除吗?"
                 >
                     <Button type="link" size='small'
                     >恢复告警</Button>
                 </Popconfirm> : <Button type="link" size='small'
                     onClick={() => { handleOpenPop(record) }}
                 >屏蔽报警</Button>
          } */}

            {/* <Button type="link" size='small'
             onClick={() => { handleSeePhoto(record) }}
          >更多</Button> */}
          </>
        );
      },
    },
  ];

  return (
    <>
      <ProTable<TableListItem>
        columns={columns}
        scroll={{ x: 1500 }}
        actionRef={actionRef}
        request={async (params, sorter) => {
          let gmtObj: any = {};
          gmtObj.sortBy = Object.keys(sorter)[0];
          gmtObj.sortRule =
            sorter[Object.keys(sorter)[0]] === 'ascend' ? 'asc' : 'desc';
          let target = {
            ...params,
            tenant: initialState.tenant?.['name'],
            creator:
              window.location?.pathname === '/user/alarmhistory'
                ? initialState?.user?.loginName
                : null,
          };
          const pageRequest = {
            pageNum: params.current,
            pageSize: params.pageSize,
            ...gmtObj,
            target,
          };
          if (props.backData?.ruleType && props.id) {
            Object.assign(target, {
              uniqueId: `${props.backData?.ruleType}_${props.id}`,
            });
          }
          if (isUser) {
            const data: any = await pageQuerySubAlarmHistory(pageRequest);
            data.items.forEach((item: any) => {
              item['key'] = item.id;
            });
            return {
              data: data?.items,
              success: true,
              total: data?.totalCount || 0,
            };
          } else {
            const data: any = await pageQueryAlarmHistory(pageRequest);
            data.items.forEach((item: any) => {
              item['key'] = item.id;
            });
            return {
              data: data?.items,
              success: true,
              total: data?.totalCount || 0,
            };
          }
        }}
        rowKey="key"
        pagination={{
          showQuickJumper: true,
          showSizeChanger: true,
        }}
        headerTitle={
          from
            ? ''
            : $i18n.get({
                id: 'holoinsight.pages.alarm.AlarmHistory.AlarmHistory',
                dm: '告警历史',
              })
        }
        options={false}
        dateFormatter="string"
      />

      <Drawer
        title={$i18n.get({
          id: 'holoinsight.pages.alarm.AlarmHistory.AlertChatu',
          dm: '告警查图',
        })}
        placement="right"
        destroyOnClose
        width={900}
        onClose={() => {
          setVisible(false);
        }}
        open={visible}
        size="large"
      >
        {popView === 'detail' && (
          <HistoryDrawer
            historyId={historyId}
            alarmTime={alarmTime}
            selectItem={selectItem}
          />
        )}
        {popView === 'chart' &&
          triggerArr.map((item: any, index: number) => {
            return (
              <Card
                title={item.triggerContent}
                className="alarm-history-card"
                headStyle={{ textAlign: 'left' }}
                key={index}
              >
                <AlarmPanel
                  data={triggerArr[index]}
                  item={selectItem}
                  key={index}
                />
              </Card>
            );
          })}
        {popView === 'pql' && <PQlChart {...pql} />}
      </Drawer>
    </>
  );
};
