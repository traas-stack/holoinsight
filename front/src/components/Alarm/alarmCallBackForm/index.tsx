import { alramTestPreView, listAvailableFields } from '@/services/alarm/api';
import { MinusOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Alert,
  Button,
  Card,
  Col,
  Descriptions,
  Drawer,
  Form,
  Input,
  Radio,
  Row,
  Select,
} from 'antd';
import { useEffect, useState } from 'react';
import ReactJson from 'react-json-view';
import $i18n from '../../../i18n';
import styles from './index.less';

const { Option } = Select;

interface fieldArr {
  fieldName: string;
  describe: string;
}

const options = [
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.alarmCallBackForm.AllAlarmRules',
      dm: '全部报警规则',
    }),
    value: 1,
  },
  {
    label: $i18n.get({
      id: 'holoinsight.Alarm.alarmCallBackForm.ManuallySelectAnAlarmRule',
      dm: '用户手动选择报警规则',
    }),
    value: 2,
  },
];

const methodOptions = [
  { label: 'GET', value: 'GET' },
  { label: 'POST', value: 'POST' },
  { label: 'DELETE', value: 'DELETE' },
  { label: 'PUT', value: 'PUT' },
  { label: 'PATCH', value: 'PATCH' },
];

const AlarmCallForm = (props: any) => {
  const { form = {}, json = {}, formRef, type, getTestData, testParam } = props;
  const [popVisible, setPopVisible] = useState(false);
  const [popForm] = Form.useForm();
  const [testValue, setTestValue] = useState(testParam);
  const [back, setBack] = useState({
    requestMsg: {},
    responseMsg: {},
    code: 0,
  });

  const [hasData, setHasData] = useState(false);
  const [listFields, setFieldsValue] = useState<fieldArr[]>([]);

  const pretreatForm = (form: any) => {
    let requestBodyArr = [];
    let parseRequestBody = JSON.parse(form.requestBody);
    for (let key in parseRequestBody) {
      if (key) {
        requestBodyArr.push({
          fieldName: key,
          fieldValue: parseRequestBody[key].replace(/[${}]/g, ''),
        });
      }
    }
    form.requestBody = requestBodyArr;
  };
  const sortFun = (arr: fieldArr[], key: string): fieldArr[] => {
    let result = arr.slice(0);
    return result.sort((a: any, b: any) => {
      return a[key] < b[key] ? -1 : 1;
    });
  };
  useEffect(() => {
    if (Object.keys(form).length) {
      pretreatForm(form);
      popForm.setFieldsValue(form);
    }
    listAvailableFields()
      .then((item: any) => {
        setFieldsValue(sortFun(item, 'fieldName'));
      })
      .catch(() => {});
  }, []);

  function handleSubmitTest() {
    popForm
      .validateFields()
      .then((res) => {
        if (res) {
          res.testBody = testValue;
          let obj: any = {};
          res.requestHeaders.forEach((element: any) => {
            let key = Object.keys(element);
            obj[element[key[0]]] = element[key[1]];
          });
          res.requestHeaders = JSON.stringify(obj);
          let callbackFieldObj: any = {};
          res.requestBody.forEach((element: any) => {
            let key = Object.keys(element);
            let arr = element[key[1]].split('');
            arr.unshift('${');
            arr.push('}');
            callbackFieldObj[element[key[0]]] = arr.join(''); // 获取回调模板字段
          });
          res.requestBody = JSON.stringify(callbackFieldObj);
          alramTestPreView(res).then((data: any) => {
            if (!data) {
              return;
            }
            try {
              back.requestMsg = JSON.parse(data.requestMsg);
              back.responseMsg = JSON.parse(data.response);
            } catch (e) {
              back.responseMsg = {
                msg: data.response,
              };
            }
            back.code = data.code;
            setBack(JSON.parse(JSON.stringify(back)));
          });
          setHasData(true);
        }
      })
      .catch(() => {});
  }

  return (
    <>
      <Form
        name="basic"
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 12 }}
        form={popForm}
        ref={formRef}
      >
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.alarmCallBackForm.CallbackTemplateName',
            dm: '模版名称',
          })}
          name="webhookName"
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.EnterACallbackTemplateName',
                dm: '请输入回调模版名称',
              }),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.alarmCallBackForm.RequestMethod',
            dm: '请求方式',
          })}
          name="requestType"
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.PleaseEnterTheRequestMethod',
                dm: '请输入请求方式',
              }),
            },
          ]}
        >
          <Select style={{ width: 200 }} options={methodOptions}></Select>

          {/* <Input width={200} /> */}
        </Form.Item>

        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.alarmCallBackForm.CallbackAddress',
            dm: '回调地址',
          })}
          name="requestUrl"
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.EnterACallbackAddress',
                dm: '请输入回调地址',
              }),
            },
          ]}
        >
          <Input width={200} />
        </Form.Item>
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.Alarm.alarmCallBackForm.RequestHeader',
            dm: '请求头',
          })}
        >
          <Form.List name="requestHeaders" initialValue={[{}]}>
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
                                    id: 'holoinsight.Alarm.alarmCallBackForm.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.Alarm.alarmCallBackForm.RequestHeaderKey',
                                  dm: '请求头key',
                                })}
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              name={[fieldAll.name, 'value']}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.Alarm.alarmCallBackForm.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.Alarm.alarmCallBackForm.RequestHeaderValue',
                                  dm: '请求头Value',
                                })}
                              />
                            </Form.Item>
                          </Col>

                          <Button
                            className={styles.addItem}
                            type="primary"
                            icon={<PlusOutlined />}
                            shape="circle"
                            size="small"
                            onClick={() => addItem()}
                          ></Button>

                          {fields.length > 1 ? (
                            <Button
                              className={styles.deleteItem}
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
        <Form.Item
          label="回调模板字段"
          className={styles.callbackField}
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.Required',
                dm: '必填项',
              }),
            },
          ]}
        >
          <Form.List name="requestBody" initialValue={[{}]}>
            {(fields, { add: addField, remove: removeField }) => {
              return (
                <>
                  {fields.map((fieldAll: any) => {
                    return (
                      <div key={fieldAll.name}>
                        <Row>
                          <Col span={8}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              name={[fieldAll.name, 'fieldName']}
                            >
                              <Input placeholder="字段名" />
                            </Form.Item>
                          </Col>

                          <Form.Item
                            labelCol={{ span: 2, offset: 1 }}
                            name={[fieldAll.name, 'fieldValue']}
                            rules={[
                              {
                                required: true,
                                message: $i18n.get({
                                  id: 'holoinsight.Alarm.alarmCallBackForm.Required',
                                  dm: '必填项',
                                }),
                              },
                            ]}
                          >
                            {/* <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.Alarm.alarmCallBackForm.RequestHeaderValue',
                                  dm: '字段Value',
                                })}
                              /> */}
                            <Select
                              style={{ width: 210 }}
                              placeholder="字段值"
                              allowClear
                            >
                              {listFields.map((item) => {
                                return (
                                  <Option
                                    key={item.fieldName}
                                    title={item.describe}
                                  >
                                    {item.fieldName}
                                  </Option>
                                );
                              })}
                            </Select>
                          </Form.Item>
                          <Button
                            className={styles.addItem}
                            type="primary"
                            icon={<PlusOutlined />}
                            shape="circle"
                            size="small"
                            onClick={() => addField()}
                          ></Button>

                          {fields.length > 1 ? (
                            <Button
                              className={styles.deleteItem}
                              type="primary"
                              icon={<MinusOutlined />}
                              shape="circle"
                              size="small"
                              onClick={() => removeField(fieldAll.name)}
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
          <div className={styles.formViewBack}>
            <ReactJson src={json} theme="monokai" />
          </div>
        </Form.Item>

        <div className={styles.jsonEdit}>
          <div style={{ flex: 1 }}>
            {/* <Form.Item
              className={styles.formEidtBack}
              label={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.CallbackTemplate',
                dm: '回调模版',
              })}
              name="requestBody"
              labelCol={{ span: 3 }}
              wrapperCol={{ span: 17 }}
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.Alarm.alarmCallBackForm.CallbackTemplate',
                    dm: '回调模版',
                  }),
                },
                () => ({
                  validator(_, value) {
                    try {
                      if (value) {
                        console.log("value",value);
                        const backData = JSON.parse(value);
                        const backArr = Object.keys(backData);
                        for (let i = 0; i < backArr.length; i++) {
                          if (
                            !/^\${{1}[a-zA-Z_\-0-9]{1,}\}{1}$/g.test(
                              backData[backArr[i]],
                            )
                          ) {
                            return Promise.reject(
                              new Error(
                                $i18n.get(
                                  {
                                    id: 'holoinsight.Alarm.alarmCallBackForm.TheValueOfBackarriIn',
                                    dm: '回调模版中键为{backArrI}的值不合法',
                                  },
                                  { backArrI: backArr[i] },
                                ),
                              ),
                            );
                          }
                        }
                        return Promise.resolve();
                      } else {
                        return Promise.resolve();
                      }
                    } catch (e) {
                      return Promise.reject(
                        new Error(
                          $i18n.get({
                            id: 'holoinsight.Alarm.alarmCallBackForm.TheInputIsNotA',
                            dm: '输入内容非JSON字符串',
                          }),
                        ),
                      );
                    }
                  },
                }),
              ]}
            >
              <Input.TextArea
                style={{ height: 150 }}
                value={innerData}
                onChange={(e) => {
                  handleChangeWord(e);
                }}
              />
            </Form.Item> */}

            <Row>
              <Col span={4}></Col>

              <Button
                style={{ marginBottom: '10px' }}
                onClick={() => {
                  setPopVisible(true);
                }}
              >
                {$i18n.get({
                  id: 'holoinsight.Alarm.alarmCallBackForm.DebugPreview',
                  dm: '调试预览',
                })}
              </Button>
              <Alert
                message={$i18n.get({
                  id: 'holoinsight.Alarm.alarmCallBackForm.YouCanSaveItOnly',
                  dm: '需要调试通过才可以保存',
                })}
                type="warning"
                showIcon
                closable
                style={{ marginBottom: '10px' }}
              />
            </Row>
          </div>
        </div>

        {type === 'alarmRuler' ? (
          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.Alarm.alarmCallBackForm.TemplateSave',
              dm: '模版保存',
            })}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.Alarm.alarmCallBackForm.EnterTheTemplateStatus',
                  dm: '请输入模版状态',
                }),
              },
            ]}
          >
            <Input
              value={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.SaveToTheAlertCallback',
                dm: '保存到告警回调模版',
              })}
              readOnly
              bordered={false}
            />
          </Form.Item>
        ) : (
          <>
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.TemplateStatus',
                dm: '模版状态',
              })}
              name="status"
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.Alarm.alarmCallBackForm.SelectTemplateStatus',
                    dm: '请选择模版状态',
                  }),
                },
              ]}
              initialValue={true}
            >
              <Radio.Group
                options={[
                  {
                    label: $i18n.get({
                      id: 'holoinsight.Alarm.alarmCallBackForm.Enable',
                      dm: '开启',
                    }),
                    value: true,
                  },
                  {
                    label: $i18n.get({
                      id: 'holoinsight.Alarm.alarmCallBackForm.Close',
                      dm: '关闭',
                    }),
                    value: false,
                  },
                ]}
              />
            </Form.Item>

            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.TemplateScopeOfAction',
                dm: '模版作用范围',
              })}
              name="type"
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.Alarm.alarmCallBackForm.SelectTheTemplateScope',
                    dm: '请选择模版作用范围',
                  }),
                },
              ]}
              initialValue={1}
            >
              <Radio.Group options={options} />
            </Form.Item>
          </>
        )}
      </Form>

      <Drawer
        width={500}
        open={popVisible}
        footer={
          <div className={styles.footerButton}>
            <Button
              type="primary"
              style={{ marginRight: '10px' }}
              onClick={() => handleSubmitTest()}
            >
              {$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.StartDebugging',
                dm: '开始调试',
              })}
            </Button>

            <Button onClick={() => setPopVisible(false)}>
              {$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.Cancel',
                dm: '取消',
              })}
            </Button>
          </div>
        }
        onClose={() => setPopVisible(false)}
      >
        <span>
          {$i18n.get({
            id: 'holoinsight.Alarm.alarmCallBackForm.DebuggingParameters',
            dm: '调试参数：',
          })}
        </span>
        <Input.TextArea
          style={{ height: 150 }}
          value={testValue}
          onChange={(e) => {
            setTestValue(e.target.value);
            getTestData(e.target.value);
          }}
        />

        {hasData ? (
          <Card
            title={$i18n.get({
              id: 'holoinsight.Alarm.alarmCallBackForm.DebuggingResults',
              dm: '调试结果',
            })}
          >
            <Descriptions column={1}>
              <Descriptions.Item
                label={$i18n.get({
                  id: 'holoinsight.Alarm.alarmCallBackForm.StatusCode',
                  dm: '状态码',
                })}
              >
                {back.code}
              </Descriptions.Item>
            </Descriptions>

            <Card
              title={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.RequestData',
                dm: '请求数据',
              })}
              className={styles.marginBottom}
            >
              <ReactJson src={back.requestMsg} theme="monokai" />
            </Card>

            <Card
              title={$i18n.get({
                id: 'holoinsight.Alarm.alarmCallBackForm.ResponseData',
                dm: '响应数据',
              })}
            >
              <ReactJson src={back.responseMsg} theme="monokai" />
            </Card>
          </Card>
        ) : null}
      </Drawer>
    </>
  );
};

export default AlarmCallForm;
