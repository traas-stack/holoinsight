import { pageQueryAlarmGroup, pageQueryAlarmRobot } from '@/services/alarm/api';
import { Button, Form, Modal, Select, Table, Tabs, Tag } from 'antd';
import { useEffect, useState } from 'react';
import $i18n from '../../i18n';
import { getTenantUserById, queryUserInfo } from '../../services/tenant/api';
import styles from './index.less';
const { TabPane } = Tabs;

const noticeMethod = [
  {
    label: $i18n.get({
      id: 'holoinsight.components.alramSubscribe.Sms',
      dm: '短信',
    }),
    value: 'sms',
  },

  {
    label: $i18n.get({
      id: 'holoinsight.components.alramSubscribe.EmailAddress',
      dm: '邮箱',
    }),
    value: 'email',
  },

  {
    label: $i18n.get({
      id: 'holoinsight.components.alramSubscribe.Voice',
      dm: '语音',
    }),
    value: 'phone',
  },
];

const noticeDingMethod = [
  {
    label: $i18n.get({
      id: 'holoinsight.components.alramSubscribe.DingtalkRobot',
      dm: '钉钉机器人',
    }),
    value: 'dingDingRobot',
  },
];

export default function AlarmSubscribe(props: any) {
  const { getData, subscriberObj, type } = props;
  const [visible, setVisible] = useState(false);
  const [popForm] = Form.useForm();
  const [personArr, setPersonArr] = useState<any[]>([]);
  const [options, setOptions] = useState<any[]>([]);
  const [groupArr, setGroupArr] = useState([]);
  const [userData, setUserData] = useState(subscriberObj.userSub);
  const [userGroupData, setUserGroupData] = useState(
    subscriberObj.userGroupSub,
  );

  const [dingSelect, setDingSelect] = useState([]);
  const [dingGroupData, setDingGroupData] = useState(
    subscriberObj.dingGroupSub,
  );

  const [activeKey, setActiveKey] = useState('1');

  async function getUserInfo() {
    const requestWay = subscriberObj?.userSub.map((user: any) => {
      return getTenantUserById(user.subscriber);
    });
    const backData = ((await Promise.all(requestWay)) || []).map((item) => {
      return {
        value: item.userId,
        label: item.userName || item.loginName,
      };
    });
    setOptions(backData);
    setPersonArr(backData);
  }
  useEffect(() => {
    if (type === 'edit') {
      if (subscriberObj?.userSub?.length > 0) {
        getUserInfo();
      }
    }
    setUserData(subscriberObj.userSub);
    setDingGroupData(subscriberObj.dingGroupSub);
    setUserGroupData(subscriberObj.userGroupSub);
  }, [
    subscriberObj.userSub.length,
    subscriberObj.dingGroupSub.length,
    subscriberObj.userGroupSub.length,
  ]);

  function handleDelete(_: any, item: any, index: any) {
    const deleteData =
      activeKey === '1'
        ? userData
        : activeKey === '2'
        ? dingGroupData
        : userGroupData;
    const type =
      activeKey === '1'
        ? 'userSub'
        : activeKey === '2'
        ? 'dingGroupSub'
        : 'userGroupSub';
    getData({
      type: type,
      data: deleteData.filter((_: any, key: any) => index !== key),
    });
  }

  useEffect(() => {
    if (activeKey !== '2') {
      popForm.setFieldsValue({ ...popForm.getFieldsValue(), noticeType: [] });
    }
  }, [activeKey]);
  const columns = [
    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.NotificationMethod',
        dm: '通知方式',
      }),
      width: 200,
      dataIndex: 'noticeType',
      render: (record: any) => {
        let newRecord = record;
        if (typeof record === 'string') {
          newRecord = JSON.parse(record);
        }
        return (newRecord || []).map((item: any, index: any) => {
          let backName = '';
          noticeMethod.forEach((ele) => {
            if (ele.value === item) {
              backName = ele.label;
              return;
            }
          });
          return <Tag key={index}>{backName}</Tag>;
        });
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.User',
        dm: '用户',
      }),
      dataIndex: 'subscriber',
      render: (record: any) => {
        let backName = '';
        personArr.forEach((ele: any) => {
          if (ele.value === record) {
            backName = ele.label;
            return;
          }
        });
        return <Tag>{backName}</Tag>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.ActionColumn',
        dm: '操作列',
      }),
      width: 160,
      dataIndex: 'address',
      render: (_: any, item: any, index: any) => {
        return (
          <>
            <Button type="link" onClick={() => handleDelete(_, item, index)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.Delete',
                dm: '删除',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  const columnsDing = [
    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.NotificationMethod',
        dm: '通知方式',
      }),
      width: 200,
      dataIndex: 'noticeType',
      render: () => {
        return (
          <>
            {$i18n.get({
              id: 'holoinsight.components.alramSubscribe.DingtalkRobot',
              dm: '钉钉机器人',
            })}
          </>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.DingtalkRobot',
        dm: '钉钉机器人',
      }),
      dataIndex: 'subscriber',
      render: (record: any) => {
        let backName = '';
        dingSelect.forEach((ele: any) => {
          if (Number(ele.value) === Number(record)) {
            backName = ele.label;
            return;
          }
        });
        return <Tag>{backName}</Tag>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.ActionColumn',
        dm: '操作列',
      }),
      width: 160,
      dataIndex: 'address',
      render: (_: any, item: any, index: any) => {
        return (
          <>
            <Button type="link" onClick={() => handleDelete(_, item, index)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.Delete',
                dm: '删除',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  const columnsUserGroups = [
    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.NotificationMethod',
        dm: '通知方式',
      }),
      width: 200,
      dataIndex: 'noticeType',
      render: (record: any) => {
        let newRecord = record;
        if (typeof record === 'string') {
          newRecord = JSON.parse(record);
        }
        return (newRecord || []).map((item: any, index: any) => {
          let backName = '';
          noticeMethod.forEach((ele) => {
            if (ele.value === item) {
              backName = ele.label;
              return;
            }
          });
          return <Tag key={index}>{backName}</Tag>;
        });
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.UserGroup',
        dm: '用户组',
      }),
      dataIndex: 'groupId',
      render: (record: any) => {
        let backName = '';
        groupArr.forEach((ele: any) => {
          if (ele.value === record) {
            backName = ele.label;
            return;
          }
        });
        return <Tag>{backName}</Tag>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.components.alramSubscribe.ActionColumn',
        dm: '操作列',
      }),
      width: 160,
      dataIndex: 'address',
      render: (_: any, item: any, index: any) => {
        return (
          <>
            <Button type="link" onClick={() => handleDelete(_, item, index)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.Delete',
                dm: '删除',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  useEffect(() => {
    pageQueryAlarmGroup({
      pageNum: 1,
      pageSize: 99999,
      target: {},
    }).then((res: any) => {
      const backArr = (res.items || []).map((item: any) => {
        return {
          label: item.groupName,
          value: item.id,
        };
      });
      setGroupArr(backArr);
    });
    pageQueryAlarmRobot({
      pageNum: 1,
      pageSize: 99999,
      target: {},
    }).then((res: any) => {
      const backArr = (res.items || []).map((item: any) => {
        return {
          label: item.groupName,
          value: item.id,
        };
      });
      setDingSelect(backArr);
    });
  }, []);

  async function onSearch(value: string) {
    if (value.length > 3) {
      await queryUserInfo(value).then((res: any = {}) => {
        if (!res || !res.loginName) return;
        const opts = [
          {
            value: res.userId,
            label: res.userName || res.loginName,
          },
        ];

        setOptions(opts);

        personArr.push({
          value: res.userId,
          label: res.userName || res.loginName,
        });
        setPersonArr(personArr);
      });
    } else {
      setOptions([]);
    }
  }

  function handleSubmit() {
    popForm.validateFields().then((res) => {
      const newArr = (res.subscriber || []).map((item: any) => {
        return {
          noticeType: res.noticeType,
          subscriber: item,
        };
      });
      const newGroupArr = (res.groupId || []).map((item: any) => {
        return {
          noticeType: res.noticeType,
          groupId: item,
        };
      });

      let backData: any[] = [];
      switch (activeKey) {
        case '1':
          backData = userData.concat(newArr);
          setUserData(backData);
          getData({
            type: 'userSub',
            data: backData,
          });
          break;
        case '2':
          backData = dingGroupData.concat(newArr);
          setDingGroupData(backData);
          getData({
            type: 'dingGroupSub',
            data: backData,
          });
          break;
        case '3':
          backData = userGroupData.concat(newGroupArr);
          setUserGroupData(backData);
          getData({
            type: 'userGroupSub',
            data: backData,
          });
          break;
      }

      popForm.resetFields();
      setVisible(false);
    });
  }
  return (
    <div className={styles.alarmSubscribe}>
      <Tabs
        defaultActiveKey="1"
        onChange={(value) => {
          setActiveKey(value);
        }}
      >
        <TabPane
          tab={$i18n.get({
            id: 'holoinsight.components.alramSubscribe.UserSubscription',
            dm: '用户订阅',
          })}
          key="1"
        >
          <div className={styles.header}>
            <Button onClick={() => setVisible(true)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.AddSubscription',
                dm: '新增订阅',
              })}
            </Button>

            {/* <Search style={{ width: "200px" }} /> */}
          </div>

          <Table
            size="small"
            columns={columns}
            dataSource={userData}
            pagination={false}
            scroll={{ y: 300 }}
          />
        </TabPane>

        <TabPane
          tab={$i18n.get({
            id: 'holoinsight.components.alramSubscribe.DingtalkGroupSubscription',
            dm: '钉钉群订阅',
          })}
          key="2"
        >
          <div className={styles.header}>
            <Button onClick={() => setVisible(true)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.AddSubscription',
                dm: '新增订阅',
              })}
            </Button>

            {/* <Search style={{ width: "200px" }} /> */}
          </div>

          <Table
            size="small"
            columns={columnsDing}
            dataSource={dingGroupData}
            pagination={false}
            scroll={{ y: 300 }}
          />
        </TabPane>
        <TabPane
          tab={$i18n.get({
            id: 'holoinsight.components.alramSubscribe.UserGroup',
            dm: '用户组',
          })}
          key="3"
        >
          <div className={styles.header}>
            <Button onClick={() => setVisible(true)}>
              {$i18n.get({
                id: 'holoinsight.components.alramSubscribe.AddSubscription',
                dm: '新增订阅',
              })}
            </Button>

            {/* <Search style={{ width: "200px" }} /> */}
          </div>

          <Table
            size="small"
            columns={columnsUserGroups}
            dataSource={userGroupData}
            pagination={false}
            scroll={{ y: 300 }}
          />
        </TabPane>
      </Tabs>

      <Modal
        open={visible}
        title={
          activeKey === '1'
            ? $i18n.get({
                id: 'holoinsight.components.alramSubscribe.AddUserSubscriptions',
                dm: '新增用户订阅',
              })
            : activeKey === '2'
            ? $i18n.get({
                id: 'holoinsight.components.alramSubscribe.AddDingtalkSubscriptions',
                dm: '新增钉钉订阅',
              })
            : $i18n.get({
                id: 'holoinsight.components.alramSubscribe.UserGroupSubscription',
                dm: '用户组订阅',
              })
        }
        onCancel={() => {
          setVisible(false);
        }}
        onOk={() => handleSubmit()}
      >
        <Form
          name="basic"
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          form={popForm}
        >
          <Form.Item
            initialValue={activeKey === '2' ? ['dingDingRobot'] : []}
            label={$i18n.get({
              id: 'holoinsight.components.alramSubscribe.NotificationMethod',
              dm: '通知方式',
            })}
            name="noticeType"
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.components.alramSubscribe.SelectANotificationMethod',
                  dm: '请选择通知方式',
                }),
              },
            ]}
          >
            <Select
              mode="multiple"
              options={activeKey === '2' ? noticeDingMethod : noticeMethod}
              style={{ width: '100%' }}
            />
          </Form.Item>

          {activeKey === '1' ? (
            <>
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.components.alramSubscribe.Subscribers',
                  dm: '订阅人',
                })}
                name="subscriber"
              >
                <Select
                  showSearch
                  mode="multiple"
                  onSearch={onSearch}
                  options={options}
                  filterOption={false}
                  style={{ width: '100%' }}
                  tokenSeparators={[',']}
                ></Select>
              </Form.Item>
            </>
          ) : activeKey === '2' ? (
            <>
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.components.alramSubscribe.DingtalkRobot',
                  dm: '钉钉机器人',
                })}
                name="subscriber"
              >
                <Select
                  mode="multiple"
                  options={dingSelect}
                  style={{ width: '100%' }}
                  tokenSeparators={[',']}
                ></Select>
              </Form.Item>
            </>
          ) : (
            <>
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.components.alramSubscribe.SubscriptionGroup',
                  dm: '订阅组',
                })}
                name="groupId"
              >
                <Select
                  mode="multiple"
                  options={groupArr}
                  style={{ width: '100%' }}
                  tokenSeparators={[',']}
                ></Select>
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  );
}
