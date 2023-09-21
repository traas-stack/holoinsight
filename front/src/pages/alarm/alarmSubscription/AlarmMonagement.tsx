import {
  alarmBlockDelete,
  alarmRuleCreate,
  alarmRuleDelete,
  alarmRuleQueryById,
  alarmRuleUpdate,
  getSubScribeAlarmRule,
  pageQueryAlarmRule,
} from '@/services/alarm/api';
import { ExclamationCircleOutlined, ImportOutlined, DownOutlined } from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import {
  Button,
  Checkbox,
  Dropdown,
  message,
  Modal,
  Popconfirm,
  Switch,
  Tag,
  Tooltip,
} from 'antd';
import queryString from 'query-string';
import React, { useRef, useState } from 'react';
import { history } from 'umi';
import SuspendAlarmModal from '../../../components/Alarm/suspendAlarmModal';
import $i18n from '../../../i18n';
import { WEEKS } from './const';
import './index.less';
import QuickAlarmSubscribe from './QuickAlarmSubscribe';
const { confirm } = Modal;

const AlarmMonagement: React.FC<any> = (props) => {
  const initialState = props.initialState;
  const { from, id, type } = props;
  const { app: appName, id: urlId } = queryString.parse(location.search);
  const actionRef = useRef<any>(null);
  const isUser = window.location?.pathname === '/user/alarmsub' || false;
  const isAlarmRule = location.pathname.includes('alarm/rule');
  const [popVisible, setPopVisible] = useState(false);
  const [uniqueId, setUniqueId] = useState('');
  const [quickAlarm, setQuickAlarm] = useState({
    open: false,
    id: '',
    type: '',
  });
  const handleDelete = (id: number) => {
    confirm({
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AreYouSureYouWant',
        dm: '请确认是否删除？',
      }),
      icon: <ExclamationCircleOutlined />,
      okText: $i18n.get({ id: 'holoinsight.pages.alarm.Ok', dm: '确定' }),
      okType: 'danger',
      cancelText: $i18n.get({
        id: 'holoinsight.pages.alarm.Cancel',
        dm: '取消',
      }),
      onOk() {
        alarmRuleDelete(id).then(() => {
          if (actionRef.current) {
            actionRef.current.reloadAndRest();
          }
        });
      },
      onCancel() {},
    });
  };
  function handleOpenPop(record: any) {
    setUniqueId(`rule_${record.id}`);
    setPopVisible(true);
  }
  function handleChangeStatus(value: any, _: any, record: any) {
    let params: any = {
      id: record.id,
      status: value ? 1 : 0,
      tenant:initialState.currentTenant
    };
    alarmRuleUpdate(params).then((res) => {
      actionRef.current.reload();
      if (res) {
        message.success(
          `${
            value
              ? $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.OpenSuccess',
                  dm: '启用成功',
                })
              : $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.OffSuccess',
                  dm: '禁用成功',
                })
          }`,
        );
      }
    });
  }
  function menu(record: any) {
    let menuItems = record.blockId
      ? [
          {
            label: (
              <Popconfirm
                overlayStyle={{ width: 240 }}
                onConfirm={() => {
                  alarmBlockDelete(record.blockId).then((res) => {
                    if (res) {
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.pages.alarm.TheAlertHasBeenRestored',
                          dm: '恢复告警成功！',
                        }),
                      );

                      actionRef.current.reload();
                    }
                  });
                }}
                title={$i18n.get({
                  id: 'holoinsight.pages.alarm.AreYouSureYouWant.1',
                  dm: '确定恢复告警吗?',
                })}
              >
                <Button type="link" size="small">
                  {$i18n.get({
                    id: 'holoinsight.pages.alarm.RecoveryAlert',
                    dm: '恢复告警',
                  })}
                </Button>
              </Popconfirm>
            ),

            key: '0',
          },
        ]
      : [
          {
            label: (
              <Button
                type="link"
                size="small"
                onClick={() => {
                  handleOpenPop(record);
                }}
              >
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.ShieldAlarm',
                  dm: '屏蔽报警',
                })}
              </Button>
            ),

            key: '0',
          },
        ];
    if (props.jihuanghost) {
      menuItems.push({
        label: (
          <Button type="link" size="small">
            <a
              href={`${props.jihuanghost}/console/integration?ruleId=${record.id}&channel=holoinsight`}
              target="_blank"
              rel="noopener noreferrer"
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.PushAurora',
                dm: '推送极光',
              })}
            </a>
          </Button>
        ),
        key: '1',
      });
    }
    if (record.extra?.sourceLink) {
      menuItems.push({
        label: (
          <Button
            type="link"
            size="small"
            onClick={() => {
              window.open(record.extra?.sourceLink)
            }}
          >
            {record.sourceType === 'log'
              ? $i18n.get({
                  id: 'holoinsight.components.HeaderSearch.LogMonitoring',
                  dm: '日志监控',
                })
              : $i18n.get({
                  id: 'holoinsight.src.components.defaultLayout.ApplicationMonitoring',
                  dm: '应用监控',
                })}
          </Button>
        ),
        key: '2',
      });
    }

    menuItems.push({
      label: (<Button
      danger
      type="link"
      size="small"
      onClick={() => {
        handleDelete(record.id);
      }}
    >
      {$i18n.get({ id: 'holoinsight.pages.alarm.Delete', dm: '删除' })}
    </Button>),
    key: '4'
    })
    menuItems.push({
      label: ( <Popconfirm
        overlayStyle={{ width: 240 }}
        onConfirm={() => {
          alarmRuleQueryById(record.id).then((res: any) => {
            res.ruleName =
              res.ruleName +
              $i18n.get({
                id: 'holoinsight.pages.alarm.Clone',
                dm: '_克隆',
              });
            delete res.id;
            alarmRuleCreate(res).then((data) => {
              if (data) {
                message.success(
                  $i18n.get({
                    id: 'holoinsight.pages.alarm.ClonedSuccessfully',
                    dm: '克隆成功！',
                  }),
                );
                actionRef.current.reload();
              }
            });
          });
        }}
        title={$i18n.get({
          id: 'holoinsight.pages.alarm.ConfirmWhetherToClone',
          dm: '请确认是否克隆',
        })}
      >
        <Button type="link" size="small">
          {$i18n.get({
            id: 'holoinsight.pages.alarm.Clone.1',
            dm: '克隆',
          })}
        </Button>
      </Popconfirm>),
      key: '5'
    })
           
    menuItems.push({
      label: (
        <Button
          type="link"
          size="small"
          onClick={() => {
            setQuickAlarm({
              id: record.id,
              open: true,
              type: record.ruleType,
            });
          }}
        >
          {$i18n.get({
            id: 'holoinsight.pages.alarm.Subscription',
            dm: '订阅',
          })}
        </Button>
      ),
      key: '3',
    });
    return { items: menuItems };
  }
  const columnsalarm: any[] = [
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.RuleName',
        dm: '规则名称',
      }),
      dataIndex: 'ruleName',
      key: 'ruleName',
      width: 150,
      fixed: 'left',
      render: (text: any, record: any) => (
        <a
          onClick={() => {
            if (appName) {
              history.push({
                pathname: `/alarm/details/${record.id}`,
                search: queryString.stringify({
                  app: appName,
                  id: urlId,
                }),
              });
            } else {
              history.push({
                pathname: `/alarm/details/${record.id}`,
                search: queryString.stringify({
                  changeBreadcrumb: location.pathname.includes('/user/alarmsub')
                    ? true
                    : false,
                }),
              });
            }
          }}
        >
          {text}
        </a>
      ),
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmLevel',
        dm: '告警等级',
      }),
      dataIndex: 'alarmLevel',
      key: 'alarmLevel',
      filters: true,
      onFilter: true,
      width: 100,
      valueType: 'select',
      valueEnum: {
        1: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.Emergency',
            dm: '紧急',
          }),
        },

        2: {
          text: $i18n.get({
            id: 'holoinsight.pages.alarm.Serious',
            dm: '严重',
          }),
        },

        3: {
          text: $i18n.get({ id: 'holoinsight.pages.alarm.High', dm: '高' }),
        },

        4: {
          text: $i18n.get({ id: 'holoinsight.pages.alarm.Medium', dm: '中' }),
        },

        5: {
          text: $i18n.get({ id: 'holoinsight.pages.alarm.Low', dm: '低' }),
        },
      },
    },
    {
      title: $i18n.get({ id: 'holoinsight.pages.alarm.Status', dm: '状态' }),
      width: 100,
      dataIndex: 'status',
      key: 'status',
      valueEnum: {
        1: {
          text: $i18n.get({ id: 'holoinsight.pages.alarm.Enable', dm: '启用' }),
        },

        0: {
          text: $i18n.get({ id: 'holoinsight.pages.alarm.Pause', dm: '暂停' }),
        },
      },
      render: (id: number, record: any) => (
        <Switch
          checkedChildren={$i18n.get({
            id: 'holoinsight.pages.alarm.Enable',
            dm: '启用',
          })}
          onChange={(value) => handleChangeStatus(value, id, record)}
          unCheckedChildren={$i18n.get({
            id: 'holoinsight.pages.alarm.Pause',
            dm: '暂停',
          })}
          checked={record.status === 1 ? true : false}
        />
      ),
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.TriggerCondition',
        dm: '触发条件',
      }),
      dataIndex: 'alarmContent',
      key: 'rule',
      width: 800,
      search: false,
      render: (record: any) => {
        if (Array.isArray(record) && record.length > 1) {
          return (
            <Tooltip
              overlayClassName="alram-tooltip-card"
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
        id: 'holoinsight.pages.alarm.EffectiveTime',
        dm: '生效时间',
      }),
      width: 200,
      dataIndex: 'timeFilter',
      key: 'timeFilter',
      search: false,
      render: (record: any) => {
        const time = record;
        const timeRange = `${time.from} - ${time.to}`;
        if (time.model === 'weeks') {
          return (
            <div>
              {time.weeks.map((item: any) => {
                return (
                  <Tag style={{ marginBottom: 5 }} key={item}>
                    {WEEKS[item]}
                  </Tag>
                );
              })}
              <br />
              {timeRange}
            </div>
          );
        } else {
          return timeRange;
        }
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.CreationTime',
        dm: '创建时间',
      }),
      width: 200,
      dataIndex: 'gmtCreate',
      valueType: 'dateTime',
      key: 'gmtCreate',
      search: isUser ? false : true,
    },
    {
      title: '',
      dataIndex: 'creator',
      key: 'creator',
      hideInTable: true,
      renderFormItem: (item: any, { fieldProps }: { fieldProps: any }) => {
        const optionName = location.pathname.includes('user/alarmsub')
          ? '我创建/修改的'
          : $i18n.get({
              id: 'holoinsight.pages.alarm.ICreated',
              dm: '我创建的',
            });
        return (
          <Checkbox.Group
            {...fieldProps}
            options={[
              {
                label: optionName || '',
                value: initialState?.user?.email || '',
              },
            ]}
            style={{ marginLeft: 10 }}
          />
        );
      },
    },
    {
      title: $i18n.get({ id: 'holoinsight.pages.alarm.Operation', dm: '操作' }),
      dataIndex: 'operation',
      key: 'operation',
      width: 260,
      fixed: 'right',
      search: false,
      render: (_: any, record: any) => {
        return (
          <>
            <Button
              type="link"
              onClick={() => {
                if (appName) {
                  history.push({
                    pathname: `/alarm/view/${record.id}`,
                    search: queryString.stringify({
                      app: appName,
                      id: urlId,
                    }),
                  });
                } else {
                  history.push({
                    pathname: `/alarm/view/${record.id}`,
                    search: queryString.stringify({
                      from: 'alarm',
                      changeBreadcrumb: location.pathname.includes(
                        '/user/alarmsub',
                      )
                        ? true
                        : false,
                    }),
                  });
                }
              }}
            >
              {$i18n.get({ id: 'holoinsight.pages.alarm.Edit', dm: '编辑' })}
            </Button>

            

            <Dropdown menu={menu(record)} trigger={['click']}>
              <Button type="link">
                {$i18n.get({ id: 'holoinsight.pages.alarm.More', dm: '更多' })}
              </Button>
            </Dropdown>
            {/* <Button type="link">展开</Button> */}
          </>
        );
      },
    },
  ];

  function handleClose() {
    setPopVisible(false);
  }

  function refesh() {
    actionRef.current.reload();
  }
  const handleImportFun = () => {
    navigator.clipboard
      .readText()
      .then((data) => {
        try {
          let res = JSON.parse(data);
          if (res.alarmContent || res.ruleName) {
            alarmRuleCreate(res).then((data) => {
              if (data) {
                message.success('导入成功');
                actionRef.current.reload();
              }
            });
          } else {
            message.error('导入失败，请检查剪切板中的导入配置');
          }
        } catch (error) {
          message.error('导入失败，请检查剪切板中的导入配置');
        }
      })
      .catch(() => {
        message.error('导入失败，请检查剪切板中的导入配置');
      });
  };
  let timer: any = null;
  const debounce = (fn: any, wait: number) => {
    return function () {
      if (timer) clearTimeout(timer);
      timer = setTimeout(() => {
        fn.apply(this, arguments);
      }, wait);
    };
  };
  const importJson = debounce(handleImportFun, 1000);
  const items: MenuProps['items'] = [
    {
      key: '1',
      label: (
        <Button
          key="import"
          // type="primary"
          onClick={() => {
            Modal.confirm({
              title: '请确认是否导入配置（导入剪切板中的配置）？',
              onOk: () => {
                importJson();
              },
            });
          }}
          style={{ marginRight: '10px' }}
        >
          <ImportOutlined />
          导入配置
        </Button>
      ),
    },
  ];
  return (
    <>
      <ProTable
        bordered
        actionRef={actionRef}
        scroll={{ x: 2000 }}
        columns={columnsalarm}
        request={async (params) => {
          const pageRequest = {
            pageNum: params.current,
            pageSize: params.pageSize,
            target: {
              ...params,
              sourceId: id,
              sourceType: appName ? `apm_${appName}` : type,
              creator:
                window.location?.pathname === '/user/alarmsub'
                  ? initialState?.user?.loginName
                  : null,
            },
          };
          if (params.creator && params.creator.length > 0) {
            pageRequest.target.creator = params.creator[0];
          }
          if (isUser) {
            let isMyself = pageRequest.target.creator ? true : false;
            let alarmRule: any = await getSubScribeAlarmRule(isMyself);
            if (alarmRule?.length > 0) {
              alarmRule.forEach((rule: any) => {
                rule.alarmContent = [];
                rule?.rule?.triggers?.forEach((t: any) => {
                  rule.alarmContent.push(t.triggerContent);
                });
                rule['key'] = rule.id;
              });
            }

            return {
              data: alarmRule,
              success: true,
              total: alarmRule?.length,
            };
          } else {
            const alarmRule: any = await pageQueryAlarmRule(pageRequest);
            if (Array.isArray(alarmRule)) {
              alarmRule.forEach((item: any) => {
                item['key'] = item.id;
              });
            }

            return {
              data: alarmRule.items,
              success: true,
              total: alarmRule.totalCount,
            };
          }
        }}
        search={true}
        dateFormatter="string"
        headerTitle={$i18n.get({
          id: 'holoinsight.pages.alarm.RuleManagement',
          dm: '规则管理',
        })}
        options={false}
        toolBarRender={() => [
          <>
            {/* <div className='default-sort'>
            <span>默认排序</span>
            <i className='iconfont icon-downArrow' />
            </div> */}

            {/* <div>
             <Search
               placeholder={$i18n.get({
                 id: 'holoinsight.pages.alarm.SearchRuleName',
                 dm: '搜索规则名称',
               })}
               onSearch={handleSearch}
             />
            </div> */}

            <Button
              type="primary"
              onClick={() => {
                let url =
                  from === 'comp' ? `/alarms/new/${type}/${id}` : '/alarms/new';
                if (appName) {
                  history.push({
                    pathname: url,
                    search: queryString.stringify({
                      app: appName,
                      id: urlId,
                    }),
                  });
                } else {
                  if (location.pathname === '/user/alarmsub') {
                    url = url + '?changeBreadcrumb=true';
                  }
                  history.push(url);
                }
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.CreateARule',
                dm: '新建规则',
              })}
            </Button>
            {isAlarmRule && (
              <Dropdown menu={{ items }} placement="bottomLeft" arrow>
              <Button>更多<DownOutlined /></Button>
            </Dropdown>
            )}
          </>,
        ]}
      />

      <SuspendAlarmModal
        uniqueId={uniqueId}
        refesh={refesh}
        popVisible={popVisible}
        handleClose={handleClose}
      />

      <QuickAlarmSubscribe
        {...quickAlarm}
        onClose={() => {
          setQuickAlarm({
            open: false,
            id: '',
            type: '',
          });
        }}
      />
    </>
  );
};

export default AlarmMonagement;
