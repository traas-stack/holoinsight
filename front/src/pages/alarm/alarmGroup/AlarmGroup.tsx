import {
  alarmGroupCreate,
  alarmGroupDelete,
  alarmGroupUpdate,
  pageQueryAlarmGroup,
} from '@/services/alarm/api';
import { queryUserInfo } from '@/services/tenant/api';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Form, Input, message, Modal, Select } from 'antd';
import { useRef, useState } from 'react';
import $i18n from '../../../i18n';

const { Search } = Input;
const { confirm } = Modal;

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
  const [isModalVisible, setModalVisible] = useState(false);
  const [searchParam, setSearchParam] = useState({
    groupName: '',
  });

  const [popType, setPopType] = useState('');
  const [editId, setEditID] = useState('');
  const [popForm] = Form.useForm();
  const [options, setOptions] = useState<any[]>([]);

  const actionRef = useRef<any>();
  function handleDelete(id: number) {
    confirm({
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmGroup.AreYouSureYouWant',
        dm: '请确认是否删除？',
      }),
      icon: <ExclamationCircleOutlined />,
      okText: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmGroup.Ok',
        dm: '确定',
      }),
      okType: 'danger',
      cancelText: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmGroup.Cancel',
        dm: '取消',
      }),
      onOk() {
        alarmGroupDelete(id).then(() => {
          if (actionRef.current) {
            actionRef.current.reloadAndRest();
          }
        });
      },
      onCancel() { },
    });
  }

  const columns: ProColumns<TableListItem>[] = [
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmGroup.Name',
        dm: '名称',
      }),
      dataIndex: 'groupName',
      key: 'groupName',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.pages.alarm.AlarmGroup.CreationTime',
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
        id: 'holoinsight.pages.alarm.AlarmGroup.UpdateTime',
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
        id: 'holoinsight.pages.alarm.AlarmGroup.Operation',
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
              onClick={() => {
                setPopType('edit');
                setModalVisible(true);
                setEditID(record.id);
                record.person = record.groupInfo.groupInfo.person;
                popForm.setFieldsValue(record);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.AlarmGroup.Edit',
                dm: '编辑',
              })}
            </Button>

            <Button
              danger
              type="link"
              onClick={() => {
                handleDelete(record.id);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.AlarmGroup.Delete',
                dm: '删除',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  function handleOk() {
    popForm
      .validateFields()
      .then((res) => {
        res.groupInfo = {
          groupInfo: {
            person: res.person,
          },
        };

        const funcSubmit =
          popType === 'add' ? alarmGroupCreate : alarmGroupUpdate;
        if (popType === 'edit') {
          res.id = editId;
        }
        funcSubmit(res).then((data) => {
          if (data) {
            message.success(
              `${popType === 'add'
                ? $i18n.get({
                  id: 'holoinsight.pages.alarm.AlarmGroup.AddSuccess',
                  dm: '添加成功',
                })
                : $i18n.get({
                  id: 'holoinsight.pages.alarm.AlarmGroup.EditSuccess',
                  dm: '编辑成功',
                })
              }`,
            );
            popForm.resetFields();
            setModalVisible(false);
            actionRef.current.reloadAndRest();
          }
        });
      })
      .catch(() => { });
    setModalVisible(true);
  }
  function handleCancel() {
    popForm.resetFields();
    setModalVisible(false);
  }
  function handleSearchName(value: string) {
    searchParam.groupName = value;
    setSearchParam({ ...searchParam });
    if (actionRef.current) {
      actionRef.current?.reload();
    }
  }

  function onSearch(value: string) {
    if (value.length > 3) {
      queryUserInfo(value).then((res: any = {}) => {
        if (!res || !res.loginName) return;
        const opts = [
          {
            value: res.loginName,
            label: res.userName || res.loginName,
          },
        ];
        setOptions(opts);
      });
    } else {
      setOptions([]);
    }
  }
  return (
    <>
      <ProTable<TableListItem>
        actionRef={actionRef}
        columns={columns}
        request={async (params, sorter) => {
          const gmtObj: any = {};
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

          const data: any = await pageQueryAlarmGroup(pageRequest);
          data.items.forEach((item: any) => {
            item['key'] = item.id;
          });
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
          id: 'holoinsight.pages.alarm.AlarmGroup.NotificationGroupManagement',
          dm: '通知组管理',
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
                  id: 'holoinsight.pages.alarm.AlarmGroup.SearchName',
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
                id: 'holoinsight.pages.alarm.AlarmGroup.Create',
                dm: '新建',
              })}
            </Button>

            {/* <Button type="dashed">批量移除</Button> */}
          </>,
        ]}
      />

      <Modal
        title={
          popType === 'add'
            ? $i18n.get({
              id: 'holoinsight.pages.alarm.AlarmGroup.CreateANotificationGroup',
              dm: '新建通知组',
            })
            : $i18n.get({
              id: 'holoinsight.pages.alarm.AlarmGroup.EditNotificationGroup',
              dm: '编辑通知组',
            })
        }
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form
          name="basic"
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          form={popForm}
        >
          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.AlarmGroup.NotificationGroupName',
              dm: '通知组名称',
            })}
            name="groupName"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.AlarmGroup.EnterANotificationGroupName',
                  dm: '请输入通知组名称',
                }),
              },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.AlarmGroup.Personnel',
              dm: '人员',
            })}
            name="person"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.AlarmGroup.PleaseEnterANotificationGroup',
                  dm: '请输入通知组人员',
                }),
              },
            ]}
          >
            <Select
              mode="multiple"
              onSearch={onSearch}
              options={options}
              style={{ width: '100%' }}
              tokenSeparators={[',']}
            ></Select>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};
