import { MinusOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Col, Form, Input, message, Modal, Radio, Row } from 'antd';
import React, { useState } from 'react';
import $i18n from '../../../i18n';
import { alarmWebBlockCreate } from '../../../services/alarm/api';

const plainOptions = [
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Minutes',
      dm: '10分钟',
    }),
    value: '10_m',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Minutes.1',
      dm: '30分钟',
    }),
    value: '30_m',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Hour',
      dm: '1小时',
    }),
    value: '1_hour',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Hours',
      dm: '3小时',
    }),
    value: '3_hour',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Day',
      dm: '1天',
    }),
    value: '24_hour',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.suspendAlarmModal.Custom',
      dm: '自定义',
    }),
    value: '6',
  },
];

const SuspendAlarmModal: React.FC<any> = (props) => {
  const [popForm] = Form.useForm();
  const [userDefine, setUserDefine] = useState(false);

  const { popVisible, handleClose, refesh, uniqueId } = props;
  function handleChangeValue(e: any) {
    const flag = e.target.value === '6' ? true : false;
    setUserDefine(flag);
  }
  function handleSubmitBlock() {
    popForm
      .validateFields()
      .then((res) => {
        let param: {
          uniqueId?: any;
          minute?: any;
          hour?: any;
          tags?: any;
        } = {};
        param.uniqueId = uniqueId;
        if (res?.hour || res?.minute) {
          param.minute = res.minute;
          param.hour = res.hour;
        } else {
          const tag = res.time.split('_');
          let minute, hour;
          if (tag[1] === 'm') {
            minute = tag[0];
            hour = 0;
          } else {
            minute = 0;
            hour = tag[0];
          }
          param.minute = minute;
          param.hour = hour;
        }
        let obj: any = {};
        res.tags.forEach((element: any) => {
          obj[element.key] = element.value;
        });
        param.tags = JSON.stringify(obj);
        alarmWebBlockCreate(param).then(() => {
          message.success(
            $i18n.get({
              id: 'holoinsight.Alarm.suspendAlarmModal.AlertBlockedSuccessfully',
              dm: '屏蔽告警成功',
            }),
          );
          handleClose();
          refesh();
        });
      })
      .catch(() => {});
  }
  return (
    <Modal
      width={700}
      title={$i18n.get({
        id: 'holoinsight.Alarm.suspendAlarmModal.ShieldAlarm',
        dm: '屏蔽报警',
      })}
      open={popVisible}
      onOk={() => handleSubmitBlock()}
      onCancel={() => handleClose()}
    >
      <Form name="basic" labelCol={{ span: 4 }} form={popForm}>
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.suspendAlarmModal.PauseTime',
            dm: '暂停时间',
          })}
          name="time"
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.Alarm.suspendAlarmModal.PleaseEnterAPauseTime',
                dm: '请输入暂停时间',
              }),
            },
          ]}
        >
          <Radio.Group onChange={handleChangeValue} options={plainOptions} />
        </Form.Item>

        {userDefine ? (
          <Input.Group>
            <Row>
              <Col offset={4} span={6}>
                <Form.Item
                  name="hour"
                  rules={[
                    {
                      required: true,
                      message: $i18n.get({
                        id: 'holoinsight.Alarm.suspendAlarmModal.PleaseEnterAPauseTime',
                        dm: '请输入暂停时间',
                      }),
                    },
                  ]}
                >
                  <Input
                    width={'20%'}
                    addonAfter={$i18n.get({
                      id: 'holoinsight.Alarm.suspendAlarmModal.Hours.1',
                      dm: '小时',
                    })}
                  />
                </Form.Item>
              </Col>

              <Col span={6}>
                <Form.Item
                  name="minute"
                  rules={[
                    {
                      required: true,
                      message: $i18n.get({
                        id: 'holoinsight.Alarm.suspendAlarmModal.PleaseEnterAPauseTime',
                        dm: '请输入暂停时间',
                      }),
                    },
                  ]}
                >
                  <Input
                    width={'20%'}
                    addonAfter={$i18n.get({
                      id: 'holoinsight.Alarm.suspendAlarmModal.Minutes.2',
                      dm: '分钟',
                    })}
                  />
                </Form.Item>
              </Col>
            </Row>
          </Input.Group>
        ) : null}

        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.suspendAlarmModal.FilterConditions',
            dm: '过滤条件',
          })}
        >
          <Form.List name="tags" initialValue={[{}]}>
            {(fields, { add: addItem, remove: removeItem }) => {
              return (
                <>
                  {fields.map((fieldAll: any) => {
                    return (
                      <div key={fieldAll.name}>
                        <Row>
                          <Col span={8}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              name={[fieldAll.name, 'key']}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.Alarm.suspendAlarmModal.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.Alarm.suspendAlarmModal.FilterConditionKey',
                                  dm: '过滤条件key',
                                })}
                              />
                            </Form.Item>
                          </Col>

                          <Col offset={1} span={8}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              name={[fieldAll.name, 'value']}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.Alarm.suspendAlarmModal.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.Alarm.suspendAlarmModal.FilterConditionValue',
                                  dm: '过滤条件value',
                                })}
                              />
                            </Form.Item>
                          </Col>

                          <Button
                            style={{ margin: '4px 10px 0px 10px' }}
                            type="primary"
                            icon={<PlusOutlined />}
                            shape="circle"
                            size="small"
                            onClick={() => addItem()}
                          ></Button>

                          {fields.length > 1 ? (
                            <Button
                              style={{ margin: '4px 0px 0px 0px' }}
                              type="primary"
                              icon={<MinusOutlined />}
                              shape="circle"
                              size="small"
                              onClick={() => removeItem(fieldAll.name)}
                            ></Button>
                          ) : null}
                        </Row>
                      </div>
                    );
                  })}
                </>
              );
            }}
          </Form.List>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default SuspendAlarmModal;
