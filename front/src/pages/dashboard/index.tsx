import { Button, Divider, message, Popconfirm, Modal, Dropdown } from 'antd';
import { PlusOutlined, ImportOutlined, DownOutlined } from '@ant-design/icons';
import React, { useRef } from 'react';
import type { ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { history, Link } from 'umi';
import request from '../../services/request';
import { addDashboard, queryDashboardById } from '@/services/dashbard/index';
import { getDashBoardPageQuery } from '@/services/infra/api';
import { userFavoriteDeleteById } from '@/services/tenant/favoriteApi';
import $i18n from '../../i18n';

const Dashboard: React.FC = () => {
  const actionRef = useRef<ActionType>();

  const handleDel = async (record: any) => {
    userFavoriteDeleteById('dashboard', record.id);
    const data = await request(`/webapi/v1/dashboard/${record.id}`, {
      method: 'DELETE',
    });

    if (data) {
      message.success(
        $i18n.get({
          id: 'holoinsight.pages.dashboard.DeletedSuccessfully',
          dm: '删除成功',
        }),
      );
    }
    actionRef.current?.reload(true);
  };

  function handleClone(record: any) {
    queryDashboardById(record.id).then((res: any) => {
      if (res) {
        const conf = res.conf;
        conf.title =
          conf.title +
          $i18n.get({ id: 'holoinsight.pages.dashboard.Clone', dm: '_克隆' });
        addDashboard(conf).then((data) => {
          if (data) {
            message.success(
              $i18n.get({
                id: 'holoinsight.pages.dashboard.ClonedSuccessfully',
                dm: '克隆成功！',
              }),
            );
            actionRef.current?.reload();
          }
        });
      }
    });
  }
  const handleImportFun = () => {
    navigator.clipboard.readText().then((data) => {
      try {
        let conf = JSON.parse(data)
        if (conf.groupType || conf.title) {
          addDashboard(conf).then((data) => {
            if (data) {
              message.success(
                '导入成功'
              );
              actionRef.current?.reload();
            }
          });
        } else {
          message.error(
            '导入失败，请检查剪切板中的导入配置'
          );
        }
      } catch (error) {
        message.error(
          '导入失败，请检查剪切板中的导入配置'
        );
      }

    }).catch(() => {
      message.error(
        '导入失败，请检查剪切板中的导入配置'
      );
    })
  }
  let timer: any = null;
  const debounce = (fn: any, wait: number) => {
    return function () {
      if (timer) clearTimeout(timer);
      timer = setTimeout(() => {
        fn.apply(this, arguments);
      }, wait);
    };
  };
  const importJson = debounce(handleImportFun, 1000)

  const columns = [
    {
      title: $i18n.get({ id: 'holoinsight.pages.dashboard.Name', dm: '名称' }),
      dataIndex: 'title',
      render: (text: any, record: any) => {
        const type = record.type;
        return (
          <Link
            to={
              type === 'magi'
                ? `/m/dashboard/preview/${record.id}`
                : `/dashboard/view/${record.id}`
            }
          >
            {text}
          </Link>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.dashboard.CreationTime',
        dm: '创建时间',
      }),
      dataIndex: 'gmtCreate',
      key: 'gmtCreate',
      valueType: 'dateTime',
    },

    {
      title: $i18n.get({
        id: 'holoinsight.pages.dashboard.ModificationTime',
        dm: '修改时间',
      }),
      dataIndex: 'gmtModified',
      key: 'gmtModified',
      search: false,
      valueType: 'dateTime',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.dashboard.Operation',
        dm: '操作',
      }),
      dataIndex: 'actions',
      width: 200,
      search: false,
      render: (text: any, record: any) => {
        return (
          <>
            {/* <Divider type="vertical" />
            <Link
             to={
               type === 'magi'
                 ? `/m/dashboard/update/${record.id}`
                 : `/dashboard/view/${record.id}`
             }
            >
             编辑
            </Link> */}
            {/* <Divider type="vertical" /> */}
            <Popconfirm
              title={$i18n.get({
                id: 'holoinsight.pages.dashboard.AreYouSureYouWant',
                dm: '确定要删除吗？',
              })}
              onConfirm={() => handleDel(record)}
            >
              <a>
                {$i18n.get({
                  id: 'holoinsight.pages.dashboard.Delete',
                  dm: '删除',
                })}
              </a>
            </Popconfirm>
            <Divider type="vertical" />
            <Popconfirm
              title={$i18n.get({
                id: 'holoinsight.pages.dashboard.AreYouSureYouWant.1',
                dm: '确定要克隆吗？',
              })}
              onConfirm={() => handleClone(record)}
            >
              <a>
                {$i18n.get({
                  id: 'holoinsight.pages.dashboard.Clone.1',
                  dm: '克隆',
                })}
              </a>
            </Popconfirm>
          </>
        );
      },
    },
  ];
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
    <ProTable
      columns={columns}
      actionRef={actionRef}
      toolBarRender={() => [
        <Button
          onClick={() => {
            history.push('/m/dashboard/create');
          }}
          key="button"
          icon={<PlusOutlined />}
          type="primary"
        >
          {$i18n.get({ id: 'holoinsight.pages.dashboard.Create', dm: '新建' })}
        </Button>,
         <Dropdown menu={{ items }} placement="bottomLeft" arrow>
         <Button>更多<DownOutlined /></Button>
       </Dropdown>
      ]}
      request={async (params) => {
        const pageRequest = {
          pageNum: params.current,
          pageSize: params.pageSize,
          target: { ...params },
        };
        const dashboards = await getDashBoardPageQuery(pageRequest);
        return {
          data: dashboards.items,
          total: dashboards.totalCount,
          success: true,
        };
      }}
    ></ProTable>
  );
};

export default Dashboard;
