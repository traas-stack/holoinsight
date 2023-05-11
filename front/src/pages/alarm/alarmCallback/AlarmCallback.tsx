import AlarmCallBack from '@/components/Alarm/alarmCallBackForm';
import {
  alarmWebHookCreate,
  alarmWebHookDelete,
  alarmWebHookUpdate,
  pageQueryAlarmWebHook,
} from '@/services/alarm/api';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Drawer, Input, message, Popconfirm, Tag } from 'antd';
import { useRef, useState } from 'react';

import $i18n from '../../../i18n';
import styles from './index.less';

const { Search } = Input;

export type TableListItem = {
  id: number;
  groupName: string;
  groupInfo: string;
  modifier: string;
  gmtModified: any;
  gmtCreate: any;
};

export default (props: any) => {
  const initialState = props.initialState;
  let formRef = useRef<HTMLFormElement>(null);
  const [isModalVisible, setModalVisible] = useState(false);
  const [searchParam, setSearchParam] = useState({
    webhookName: '',
  });

  const [popType, setPopType] = useState('');
  const [editId, setEditID] = useState('');
  const actionRef = useRef<any>();
  const [formBack, setFormBack] = useState({});
  const [jsonData, setJSONdata] = useState({});
  const [testValue, setTestValue] = useState('');
  function handleChangeChildData(data: any) {
    setJSONdata(data);
  }

  function handleDelete(id: number) {
    alarmWebHookDelete(id).then((res) => {
      if (res) {
        message.success(
          $i18n.get({
            id: 'holoinsight.alarm.alarmCallback.DeletedSuccessfully',
            dm: '删除成功',
          }),
        );
        actionRef.current.reloadAndRest();
      }
    });
  }
  function handleOpenEdit(record: any) {
    setPopType('edit');
    setModalVisible(true);
    setEditID(record.id);
    const backquerest = JSON.parse(record.requestHeaders);
    const arr = Object.keys(backquerest).map((item) => {
      return {
        key: item,
        value: backquerest[item],
      };
    });
    setJSONdata(JSON.parse(record.requestBody));
    let backData = JSON.parse(JSON.stringify(record));
    backData.requestHeaders = arr;
    backData.status = backData.status === 1 ? true : false;
    setTestValue(backData.webhookTest);
    setFormBack(backData);
  }
  const columns: ProColumns<TableListItem>[] = [
    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmCallback.Name',
        dm: '名称',
      }),
      dataIndex: 'webhookName',
      key: 'webhookName',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmCallback.Status',
        dm: '状态',
      }),
      dataIndex: 'status',
      key: 'status',
      render: (_) => {
        return _ === 1 ? (
          <Tag color="success">
            {$i18n.get({
              id: 'holoinsight.alarm.alarmCallback.Enable',
              dm: '开启',
            })}
          </Tag>
        ) : (
          <Tag color="warning">
            {$i18n.get({
              id: 'holoinsight.alarm.alarmCallback.Close',
              dm: '关闭',
            })}
          </Tag>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmCallback.UpdateTime',
        dm: '更新时间',
      }),
      dataIndex: 'gmtModified',
      key: 'gmtModified',
      valueType: 'dateTime',
      onFilter: true,
      sorter: true,
    },

    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmCallback.CreationTime',
        dm: '创建时间',
      }),
      dataIndex: 'gmtCreate',
      key: 'gmtCreate',
      valueType: 'dateTime',
      onFilter: true,
      sorter: true,
    },

    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmCallback.Operation',
        dm: '操作',
      }),
      key: 'option',
      width: 160,
      valueType: 'option',
      render: (_, record: any) => {
        return (
          <>
            <Button
              type="link"
              onClick={() => handleOpenEdit(record)}
            >
              {$i18n.get({
                id: 'holoinsight.alarm.alarmCallback.Edit',
                dm: '编辑',
              })}
            </Button>

            <Popconfirm
              overlayStyle={{ width: 240 }}
              onConfirm={() => handleDelete(record.id)}
              title={$i18n.get({
                id: 'holoinsight.alarm.alarmCallback.AreYouSureYouWant',
                dm: '确定删除吗?',
              })}
            >
              <Button
                danger
                type="link"
                disabled={record.role === 'marketplace'}
              >
                {$i18n.get({
                  id: 'holoinsight.alarm.alarmCallback.Delete',
                  dm: '删除',
                })}
              </Button>
            </Popconfirm>
          </>
        );
      },
    },
  ];

  function handleOk() {
    formRef
      .validateFields()
      .then((res: any) => {
        let downData = JSON.parse(JSON.stringify(res));
        downData.status = res.status ? 1 : 0;
        downData.requestHeaders = {};
        res.requestHeaders.forEach((element: object) => {
          let key = Object.keys(element);
          downData.requestHeaders[element[key[0]]] = element[key[1]];
        });
        downData.requestHeaders = JSON.stringify(downData.requestHeaders);
        downData.webhookTest = testValue;
        let callbackFieldObj: any = {};
        res.requestBody.forEach((element: any) => {
          let key = Object.keys(element);
          let arr = element[key[1]].split('');
          arr.unshift('${');
          arr.push('}');
          callbackFieldObj[element[key[0]]] = arr.join(''); // 获取回调模板字段
        });
        downData.requestBody = JSON.stringify(callbackFieldObj);
        let funcSubmit =
          popType === 'add' ? alarmWebHookCreate : alarmWebHookUpdate;
        if (popType === 'edit') {
          downData.id = editId;
        }
        funcSubmit(downData).then((data) => {
          if (data && Object.keys(data).length > 0) {
            message.success(
              `${
                popType === 'add'
                  ? $i18n.get({
                      id: 'holoinsight.alarm.alarmCallback.AddSuccess',
                      dm: '添加成功',
                    })
                  : $i18n.get({
                      id: 'holoinsight.alarm.alarmCallback.EditSuccess',
                      dm: '编辑成功',
                    })
              }`,
            );
            setFormBack({});
            setModalVisible(false);
            actionRef.current.reloadAndRest();
          }
        });
      })
      .catch(() => {});
    setModalVisible(true);
  }
  function handleCancel() {
    setModalVisible(false);
    setFormBack({});

    setJSONdata({});
  }
  function handleSearchName(value: string) {
    searchParam.webhookName = value;
    setSearchParam({ ...searchParam });
    if (actionRef.current) {
      actionRef.current?.reload();
    }
  }

  return (
    <>
      <ProTable<TableListItem>
        actionRef={actionRef}
        columns={columns}
        request={async (params, sorter) => {
          let gmtObj: any = {};
          gmtObj.sortBy = Object.keys(sorter)[0];
          gmtObj.sortRule =
            sorter[Object.keys(sorter)[0]] === 'ascend' ? 'asc' : 'desc';
          const pageRequest = {
            ...gmtObj,
            pageNum: params.current,
            pageSize: params.pageSize,
            target: {
              ...params,
              ...searchParam,
              tenant: initialState.currentTenant,
            },
          };

          const data = await pageQueryAlarmWebHook(pageRequest);
          return {
            data: data.items,
            success: true,
            total: data.totalCount,
          };
        }}
        rowKey="key"
        pagination={{
          showQuickJumper: true,
          showSizeChanger: true,
        }}
        dateFormatter="string"
        search={false}
        headerTitle={$i18n.get({
          id: 'holoinsight.alarm.alarmCallback.AlertCallbackTemplate',
          dm: '预警回调模版',
        })}
        options={false}
        toolBarRender={() => [
          <>
            {/* <div>
             <Search placeholder={"搜索包含手机号的报警组"} />
          </div> */}

            <div>
              <Search
                onSearch={(value) => handleSearchName(value)}
                placeholder={$i18n.get({
                  id: 'holoinsight.alarm.alarmCallback.SearchName',
                  dm: '搜索名称',
                })}
              />
            </div>

            <Button
              type="primary"
              onClick={() => {
                setPopType('add');
                setModalVisible(true);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.alarm.alarmCallback.Create',
                dm: '新建',
              })}
            </Button>
          </>,
        ]}
      />

      <Drawer
        width={900}
        title={
          popType === 'add'
            ? $i18n.get({
                id: 'holoinsight.alarm.alarmCallback.CreateTemplate',
                dm: '新建模版',
              })
            : $i18n.get({
                id: 'holoinsight.alarm.alarmCallback.EditTemplate',
                dm: '编辑模版',
              })
        }
        open={isModalVisible}
        destroyOnClose
        onClose={handleCancel}
        footer={
          <div className={styles.footerButton}>
            <Button
              type="primary"
              style={{ marginRight: '10px' }}
              onClick={() => handleOk()}
              hidden={formBack?.role === 'marketplace'}
            >
              {$i18n.get({
                id: 'holoinsight.alarm.alarmCallback.Confirm',
                dm: '确认',
              })}
            </Button>

            <Button onClick={() => handleCancel()}>
              {$i18n.get({
                id: 'holoinsight.alarm.alarmCallback.Cancel',
                dm: '取消',
              })}
            </Button>
          </div>
        }
      >
        <AlarmCallBack
          formRef={(ele:any) => (formRef = ele)}
          testParam={testValue}
          json={jsonData}
          form={formBack}
          getTestData={(value: string) => setTestValue(value)}
          handleChangeChildData={handleChangeChildData}
        />
      </Drawer>
    </>
  );
};
