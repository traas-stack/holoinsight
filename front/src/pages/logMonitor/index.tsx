import FordView from '@/components/FolderView/index';
import {
  create,
  deleteById,
  pageQuery,
  queryById,
  update,
} from '@/services/customplugin/api';
import {
  userFavoriteCreate,
  userFavoriteDeleteById,
  userFavoriteQueryAll,
} from '@/services/tenant/favoriteApi';
import {
  Button,
  Card,
  Input,
  message,
  Modal,
  Popconfirm,
  Popover,
  Radio,
  Switch,
  Tabs,
  Tooltip,
} from 'antd';
import _ from 'lodash';
import {
  BarChartOutlined,
  ContainerOutlined,
  DeleteOutlined,
  EditOutlined,
  FolderOpenOutlined,
  PlusOutlined,
  RedoOutlined,
  SlidersOutlined,
  StarFilled,
  StarOutlined,
} from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import React, { useRef, useState } from 'react';
import { history } from 'umi';
import IconFont from '../../components/Icon/IconFont';
import $i18n from '../../i18n';
import styles from './index.less';
import { useModel } from 'umi';

const { Search } = Input;
const { TabPane } = Tabs;
const LogMonitoring = (props: any) => {
  const childRef = useRef<any>();
  const childRefs = useRef<any>();
  const id = props.match?.params?.id || -1;
  const [view, setView] = useState('folder');
  const [searchOpen, setSearchOpen] = useState(false);
  const [isMyFavorite, setIsMyFavorite] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const actionRef = useRef<ActionType>();
  const actionRefs = useRef<ActionType>();
  const { initialState } = useModel('@@initialState');

  const handleDel = async (record: any) => {
    const data = await deleteById(record.id);
    if (data) {
      message.success(
        $i18n.get({
          id: 'holoinsight.pages.logMonitor.DeletedSuccessfully',
          dm: '删除成功',
        }),
      );
    }
    actionRef.current?.reload(true);
  };
  const deleteFavorite = (record: any) => {
    userFavoriteDeleteById('logmonitor', record.id).then(() => {
      message.success(
        $i18n.get({
          id: 'holoinsight.FolderView.XfItem.TheCollectionHasBeenCanceled',
          dm: '取消收藏成功',
        }),
      );

      actionRef.current?.reload();
      actionRefs.current?.reload();
    });
  };
  const updateStatus = async (ids: number) => {
    queryById(ids).then((res: any) => {
      const qData = {
        conf: _.cloneDeep(res.conf),
        id: res.id,
        name: res.name,
        parentFolderId: res.parentFolderId,
        periodType: res.periodType,
        pluginType: res.pluginType,
        status: res.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE',
      };
      update(qData).then(() => {
        actionRef.current?.reload();
        actionRefs.current?.reload();
      });
    });
  };
  const popColumns = (record: any) => {
    return (
      <>
        <Button
          type="link"
          key="editable"
          onClick={() => {
            history.push(`/log/metric/${record.id}?logMonitor=edit`);
          }}
          style={{ display: 'block' }}
        >
          <EditOutlined />
          {$i18n.get({ id: 'holoinsight.pages.logMonitor.Edit', dm: '编辑' })}
        </Button>

        <Button
          style={{ display: 'block' }}
          type="link"
          key="add"
          onClick={() =>
            history.push(`/log/metric/${record.id}?logMonitor=alarm`)
          }
        >
          <SlidersOutlined />
          {$i18n.get({
            id: 'holoinsight.pages.logMonitor.CreateAlarm',
            dm: '新建报警',
          })}
        </Button>
        {record.favorited ? (
          <Button
            style={{ display: 'block' }}
            type="link"
            onClick={() => deleteFavorite(record)}
          >
            <StarFilled style={{ color: 'gold' }} />
            {$i18n.get({
              id: 'holoinsight.pages.logMonitor.CancelCollection',
              dm: '取消收藏',
            })}
          </Button>
        ) : (
          <Button
            style={{ display: 'block' }}
            type="link"
            onClick={() => {
              userFavoriteCreate({
                url: `/log/metric/${record.id}`,
                type: 'logmonitor',
                relateId: record.id,
                name: record.name,
              }).then(() => {
                message.success(
                  $i18n.get({
                    id: 'holoinsight.FolderView.XfItem.CollectedSuccessfully',
                    dm: '收藏成功',
                  }),
                );

                actionRef.current?.reload();
                actionRefs.current?.reload();
              });
            }}
          >
            <StarOutlined style={{ color: 'gold' }} />
            {$i18n.get({
              id: 'holoinsight.pages.logMonitor.Collection',
              dm: '收藏',
            })}
          </Button>
        )}

        <Popconfirm
          title={$i18n.get({
            id: 'holoinsight.pages.logMonitor.AreYouSureYouWant',
            dm: '确定要删除吗？',
          })}
          onConfirm={() => handleDel(record)}
        >
          <Button type="link" key="delete" style={{ display: 'block' }}>
            <DeleteOutlined style={{ color: 'red' }} />
            <span style={{ color: 'red' }}>
              {$i18n.get({
                id: 'holoinsight.pages.logMonitor.Delete',
                dm: '删除',
              })}
            </span>
          </Button>
        </Popconfirm>
      </>
    );
  };
  const columns: any = [
    {
      title: 'id',
      width: 80,
      key: 'id',
      dataIndex: 'id',
      render: (text: any, record: any) => [
        <a
          key="editable"
          onClick={() => {
            history.push(`/log/metric/${record.id}?logMonitor=edit`);
          }}
        >
          {text}
        </a>,
      ],
    },

    {
      title: $i18n.get({ id: 'holoinsight.pages.logMonitor.Name', dm: '名称' }),
      width: 120,
      key: 'name',
      dataIndex: 'name',
      render: (text: any, record: any) => {
        return (
          <>
            {text}
            {record.favorited ? (
              <>
                <Tooltip
                  title={$i18n.get({
                    id: 'holoinsight.pages.logMonitor.CancelCollection',
                    dm: '取消收藏',
                  })}
                >
                  <Button
                    type="link"
                    style={{ padding: '0px' }}
                    onClick={() => deleteFavorite(record)}
                  >
                    <StarFilled style={{ color: 'gold' }} />
                    {/* <StarOutlined  /> */}
                  </Button>
                </Tooltip>
              </>
            ) : (
              <></>
            )}
          </>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.logMonitor.Status',
        dm: '状态',
      }),
      width: 80,
      key: 'status',
      search: false,
      dataIndex: 'status',
      // valueEnum: {
      //   OFFLINE: {
      //     // text: $i18n.get({ id: 'holoinsight.pages.logMonitor.Close', dm: '关闭' }),
      //     // status: 'Default',
      //   },
      //   ONLINE: {
      //     // text: $i18n.get({ id: 'holoinsight.pages.logMonitor.Online', dm: '已上线' }),
      //     // status: 'Success',
      //   },
      // },
      render: (text: any, record: any) => {
        return (
          <Switch
            checked={text === 'ONLINE'}
            onChange={() => updateStatus(record.id)}
          />
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.logMonitor.Cycle',
        dm: '周期',
      }),
      width: 80,
      dataIndex: 'periodType',
      search: false,
      key: 'periodType',
      valueEnum: {
        SECOND: { text: '1s' },
        FIVE_SECOND: { text: '5s' },
        MINUTE: { text: '1min' },
      },
    },

    // {
    //   title: $i18n.get({
    //     id: 'holoinsight.pages.logMonitor.Creator',
    //     dm: '创建者',
    //   }),
    //   width: 80,
    //   key: 'creator',
    //   dataIndex: 'creator',
    // },

    // {
    //   title: $i18n.get({
    //     id: 'holoinsight.pages.logMonitor.Modifier',
    //     dm: '修改者',
    //   }),
    //   width: 80,
    //   key: 'modifier',
    //   dataIndex: 'modifier',
    // },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.logMonitor.CreationTime',
        dm: '创建时间',
      }),
      width: 140,
      search: false,
      key: 'gmtCreate',
      dataIndex: 'gmtCreate',
      valueType: 'dateTime',
      sorter: (a: any, b: any) => a.gmtCreate - b.gmtCreate,
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.logMonitor.ModificationTime',
        dm: '修改时间',
      }),
      width: 140,
      search: false,
      key: 'gmtModified',
      dataIndex: 'gmtModified',
      valueType: 'dateTime',
      sorter: (a: any, b: any) => a.gmtModified - b.gmtModified,
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.logMonitor.Operation',
        dm: '操作',
      }),
      dataIndex: 'action',
      search: false,
      key: 'action',
      width: 100,
      render: (text: any, record: any) => (
        <>
          <Button
            type="link"
            key="view"
            onClick={() => {
              history.push(`/log/metric/${record.id}?parentId=${id}`);
            }}
          >
            <Tooltip
              title={$i18n.get({
                id: 'holoinsight.pages.logMonitor.Data',
                dm: '数据',
              })}
            >
              <BarChartOutlined />
            </Tooltip>
          </Button>
          <Popover content={popColumns(record)}>...</Popover>
        </>
      ),
    },

    {
      key: 'search',
      hideInTable: true,
      search: false,
      dataIndex: 'search',
      renderFormItem: () => {
        return (
          <Search
            placeholder={$i18n.get({
              id: 'holoinsight.pages.logMonitor.KeywordSearch',
              dm: '关键字搜索',
            })}
          />
        );
      },
    },
  ];

  function changeView(value: string) {
    setView(value);
  }
  //打开关闭高级搜索
  const changeSearch = () => {
    setSearchOpen(!searchOpen);
  };
  //修改列表类型
  const changeType = (v: string) => {
    changeView(v);
  };
  //新增文件夹
  const handleFOpenAddFolder = () => {
    childRef.current.handleOpenAddFolder();
  };
  const onSearch = (v: string) => {
    setSearchValue(v);
    actionRef.current?.reload();
  };

  const handleImportFun = () => {
    navigator.clipboard
      .readText()
      .then((data) => {
        try {
          let res = JSON.parse(data);
          if (res.conf) {
            res.parentFolderId = id;
            create(res).then(() => {
              message.success('导入成功');
              childRef?.current?.refeshs();
              childRefs?.current?.refeshs();
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
  const operations = () => {
    return (
      <>
        {view === 'folder' ? (
          <Button
            key="open"
            onClick={() => {
              childRef?.current?.refeshs();
              childRefs?.current?.refeshs();
            }}
            style={{ marginRight: '10px' }}
          >
            <RedoOutlined />
          </Button>
        ) : null}
        {view === 'table' ? (
          <>
            <Search
              placeholder={$i18n.get({
                id: 'holoinsight.pages.logMonitor.EnterAName',
                dm: '请输入名称',
              })}
              allowClear
              onSearch={onSearch}
              style={{ width: 200, marginRight: '10px' }}
            />
            <Button
              key="open"
              onClick={() => changeSearch()}
              style={{ marginRight: '10px' }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.logMonitor.AdvancedSearch',
                dm: '高级搜索',
              })}
            </Button>
          </>
        ) : null}

        <Radio.Group
          key={'back'}
          defaultValue="folder"
          value={view}
          onChange={(v) => changeType(v.target.value)}
          style={{ marginRight: '10px' }}
        >
          <Radio.Button value="table">
            <Tooltip
              title={$i18n.get({
                id: 'holoinsight.pages.logMonitor.TileList',
                dm: '平铺列表',
              })}
            >
              <ContainerOutlined />
            </Tooltip>
          </Radio.Button>
          <Radio.Button value="folder">
            <Tooltip
              title={$i18n.get({
                id: 'holoinsight.pages.logMonitor.FolderList',
                dm: '文件夹列表',
              })}
            >
              <FolderOpenOutlined />
            </Tooltip>
          </Radio.Button>
        </Radio.Group>
        {view === 'folder' ? (
          <Button
            onClick={() => handleFOpenAddFolder()}
            style={{ marginRight: '10px' }}
          >
            <IconFont type="icon-tianjiawenjian" />
            {$i18n.get({
              id: 'holoinsight.components.FolderView.CreateAFolder',
              dm: '新建文件夹',
            })}
          </Button>
        ) : null}
        <Button
          key="out"
          type="primary"
          onClick={() => {
            history.push(`/log/metric/create/${id}/`);
          }}
          style={{ marginRight: '10px' }}
        >
          <PlusOutlined />
          {$i18n.get({ id: 'holoinsight.pages.logMonitor.Add', dm: '新增' })}
        </Button>
        <Button
          key="import"
          type="primary"
          onClick={() => {
            Modal.confirm({
              title: '请确认是否导入配置？',
              onOk: () => {
                importJson();
              },
            });
          }}
          style={{ marginRight: '10px' }}
        >
          导入配置
        </Button>
      </>
    );
  };
  return (
    <>
      <div
        style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '10px' }}
      >
        {$i18n.get({
          id: 'holoinsight.pages.logMonitor.LogMonitoring',
          dm: '日志监控',
        })}
      </div>
      <Card className={styles.card}>
        <Tabs
          defaultActiveKey="3"
          size="large"
          tabBarExtraContent={operations()}
          onChange={(value) => {
            setIsMyFavorite(value === '1' ? true : false);
            childRefs?.current?.refeshs();
            childRef?.current?.refeshs();
          }}
        >
          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.pages.logMonitor.MyFavorite',
              dm: '我收藏的',
            })}
            key="1"
          >
            {isMyFavorite && view === 'folder' ? (
              <FordView
                id={id}
                changeView={changeView} //切换视图
                ref={childRefs}
                types={'favoriteAlrm'}
              />
            ) : (
              <ProTable
                columns={columns}
                actionRef={actionRefs}
                search={searchOpen ? { defaultCollapsed: false } : searchOpen}
                request={async () => {
                  // 表单搜索项会从 params 传入，传递给后端接口。
                  // console.log(params, sorter, filter);
                  const favoriteData: any = await userFavoriteQueryAll();
                  const filterData = favoriteData.filter((item: any) => {
                    return item.type === 'logmonitor';
                  });
                  filterData.forEach((item: any) => {
                    item.id = item.relateId - 0;
                    item.favorited = true;
                  });
                  return {
                    data: filterData,
                    total: filterData.length,
                    success: true,
                  };
                }}
                rowKey="key"
                pagination={{
                  showQuickJumper: true,
                  showSizeChanger: true,
                }}
                toolBarRender={() => []}
              />
            )}
          </TabPane>
          {/* <TabPane tab="我创建的" key="2">
             我创建的
            </TabPane> */}
          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.pages.logMonitor.All',
              dm: '全部',
            })}
            key="3"
          >
            {view === 'folder' ? (
              <FordView
                id={id}
                changeView={changeView} //切换视图
                refs={childRef}
              />
            ) : (
              <ProTable
                columns={columns}
                actionRef={actionRef}
                search={searchOpen ? { defaultCollapsed: false } : searchOpen}
                request={async (params, sorter, filter) => {
                  // 表单搜索项会从 params 传入，传递给后端接口。
                  // console.log(params, sorter, filter);
                  const pageRequest = {
                    pageNum: params.current,
                    pageSize: params.pageSize,
                    target: {
                      ...params,
                      name:
                        (params.name && params.name !== '') || searchOpen
                          ? params.name
                          : searchValue,
                      tenant: initialState.currentTenant,
                    },
                  };
                  const data: any = await pageQuery(pageRequest);
                  const favoriteData: any = await userFavoriteQueryAll();
                  data.items.map((item: any) => {
                    let fflag = false;
                    favoriteData.forEach((fItem: any) => {
                      if (item.id.toString() === fItem.relateId) {
                        fflag = true;
                      }
                    });
                    if (fflag) {
                      item.favorited = true;
                      return item;
                    } else {
                      return item;
                    }
                  });
                  return {
                    data: data.items,
                    total: data.totalCount,
                    success: true,
                  };
                }}
                rowKey="key"
                pagination={{
                  showQuickJumper: true,
                  showSizeChanger: true,
                }}
                toolBarRender={() => []}
              />
            )}
          </TabPane>
        </Tabs>
      </Card>
    </>
  );
};

export default LogMonitoring;
