import {
  alarmRobotCreate,
  alarmRobotDelete,
  alarmRobotUpdate,
  pageQueryAlarmRobot,
} from '@/services/alarm/api';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Form, Input, message, Modal, Radio } from 'antd';
import { debounce } from 'lodash';
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
const { TextArea } = Input;
export default (props: any) => {
  const initialState = props.initialState;

  const [isModalVisible, setModalVisible] = useState(false);
  const [searchParam, setSearchParam] = useState({
    groupName: '',
  });

  const [popType, setPopType] = useState('');
  const [editId, setEditID] = useState('');
  const [popForm] = Form.useForm();
  const [isKey, setIsKey] = useState(false);

  const actionRef = useRef<any>();

  function handleDelete(id: number) {
    confirm({
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmRobot.AreYouSureYouWant',
        dm: '请确认是否删除？',
      }),
      icon: <ExclamationCircleOutlined />,
      okText: $i18n.get({ id: 'holoinsight.alarm.alarmRobot.Ok', dm: '确定' }),
      okType: 'danger',
      cancelText: $i18n.get({
        id: 'holoinsight.alarm.alarmRobot.Cancel',
        dm: '取消',
      }),
      onOk() {
        alarmRobotDelete(id).then(() => {
          if (actionRef.current) {
            actionRef.current.reloadAndRest();
          }
        });
      },
      onCancel() {
      },
    });
  }

  const columns: ProColumns<TableListItem>[] = [
    {
      title: $i18n.get({ id: 'holoinsight.alarm.alarmRobot.Name', dm: '名称' }),
      dataIndex: 'groupName',
      key: 'groupName',
    },

    {
      title: $i18n.get({
        id: 'holoinsight.alarm.alarmRobot.CreationTime',
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
        id: 'holoinsight.alarm.alarmRobot.UpdateTime',
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
        id: 'holoinsight.alarm.alarmRobot.Operation',
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
                if (record.extra) {
                  const newExtra = JSON.parse(record.extra);
                  if (newExtra.isSecret) {
                    setIsKey(true);
                  }
                  const backData = { ...record, ...newExtra };
                  popForm.setFieldsValue(backData);
                } else {
                  popForm.setFieldsValue(record);
                }
              }}
            >
              {$i18n.get({
                id: 'holoinsight.alarm.alarmRobot.Edit',
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
                id: 'holoinsight.alarm.alarmRobot.Delete',
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
        let params: any = {};
        let funcSubmit =
          popType === 'add' ? alarmRobotCreate : alarmRobotUpdate;
        if (popType === 'edit') {
          params.id = editId;
        }
        params.groupName = res.groupName;
        params.robotUrl = res.robotUrl;
        params.tenant = initialState.currentTenant;
        params.extra = JSON.stringify({
          isSecret: res.isSecret,
          secret: res.secret,
        });
        funcSubmit(params).then((data) => {
          if (data) {
            message.success(
              `${
                popType === 'add'
                  ? $i18n.get({
                      id: 'holoinsight.alarm.alarmRobot.AddSuccess',
                      dm: '添加成功',
                    })
                  : $i18n.get({
                      id: 'holoinsight.alarm.alarmRobot.EditSuccess',
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
      .catch(() => {})
      .finally(() => {
        setIsKey(false);
      });
  }
  const debounceHandleOk = debounce(handleOk, 1000, {
    leading: true,
    trailing: false,
  });

  function handleCancel() {
    popForm.resetFields();
    setIsKey(false);
    setModalVisible(false);
  }
  function handleSearchName(value: string) {
    searchParam.groupName = value;
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

          const data: any = await pageQueryAlarmRobot(pageRequest);
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
          id: 'holoinsight.alarm.alarmRobot.DingtalkGroupsOfRobotManagement',
          dm: '钉钉群机器人管理',
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
                  id: 'holoinsight.alarm.alarmRobot.SearchName',
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
                id: 'holoinsight.alarm.alarmRobot.Create',
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
                id: 'holoinsight.alarm.alarmRobot.CreateADingtalkGroupChat',
                dm: '新建钉钉群聊机器人',
              })
            : $i18n.get({
                id: 'holoinsight.alarm.alarmRobot.EditDingtalkGroupChatRobot',
                dm: '编辑钉钉群聊机器人',
              })
        }
        open={isModalVisible}
        onOk={debounceHandleOk}
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
              id: 'holoinsight.alarm.alarmRobot.GroupChatRobotName',
              dm: '群聊机器人名称：',
            })}
            name="groupName"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.alarm.alarmRobot.PleaseEnterTheNameOf',
                  dm: '请输入群聊机器人名称',
                }),
              },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="webhook"
            name="robotUrl"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.alarm.alarmRobot.EnterWebhook',
                  dm: '请输入webhook',
                }),
              },
            ]}
          >
            <TextArea rows={4} />
          </Form.Item>
          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.alarm.alarmRobot.EncryptionOrNot',
              dm: '是否加密',
            })}
            name="isSecret"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.alarm.alarmRobot.SelectWhetherToEncrypt',
                  dm: '请选择是否加密',
                }),
              },
            ]}
          >
            <Radio.Group
              onChange={(e) => {
                setIsKey(e.target.value);
              }}
            >
              <Radio value={true}>
                {$i18n.get({
                  id: 'holoinsight.alarm.alarmRobot.Yes',
                  dm: '是',
                })}
              </Radio>
              <Radio value={false}>
                {$i18n.get({ id: 'holoinsight.alarm.alarmRobot.No', dm: '否' })}
              </Radio>
            </Radio.Group>
          </Form.Item>
          {isKey ? (
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.alarm.alarmRobot.Key',
                dm: '密钥',
              })}
              name="secret"
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.alarm.alarmRobot.EnterAKey',
                    dm: '请输入密钥',
                  }),
                },
                {
                  validator(rules, value) {
                    if (/^SEC/g.test(value)) {
                      return Promise.resolve();
                    } else {
                      return Promise.reject(
                        $i18n.get({
                          id: 'holoinsight.alarm.alarmRobot.InvalidKey',
                          dm: '密钥不合法',
                        }),
                      );
                    }
                  },
                },
              ]}
            >
              <Input />
            </Form.Item>
          ) : null}
        </Form>
      </Modal>
    </>
  );
};
