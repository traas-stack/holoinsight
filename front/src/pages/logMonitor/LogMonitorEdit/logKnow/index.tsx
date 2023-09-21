import {
  Button,
  Col,
  Form,
  Input,
  InputNumber,
  Radio,
  Row,
  Select,
} from 'antd';
import React from 'react';
import { LOG_KNOW_TYPE, OPEN_LOG_KNOWN } from '../const';
import styles from './index.less';

const LogKnow: React.FC<any> = (props) => {
  const { form } = props;
  return (
    <>
      <Col offset={2}>
        <Form.Item
          label="模式匹配："
          name="logPattern"
        >
          <Radio.Group
            onChange={(value) => {
              if (value) {
                form.setFieldsValue({ collectMetrics: [] });
              }
            }}
            options={OPEN_LOG_KNOWN}
          />
        </Form.Item>
      </Col>
      <Form.Item
        noStyle
        shouldUpdate={(pre, next) => {
          return pre.logPattern !== next.logPattern;
        }}
      >
        {({ getFieldValue }) => {
          if (getFieldValue('logPattern')) {
            return (
              <Form.List name="logKnownPatterns" initialValue={[{}]}>
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <Row key={key}>
                        <Col span={5} offset={2}>
                          {key === 0 ? (
                            <span className={styles.logtitle}>已知模式</span>
                          ) : null}
                          <Form.Item
                            rules={[
                              {
                                required: true,
                                message: '必填项',
                              },
                            ]}
                            // label="事件名称"
                            {...restField}
                            name={[name, 'eventName']}
                          >
                            <Input />
                          </Form.Item>
                        </Col>

                        <Col span={3} className={styles.mg8}>
                          {key === 0 ? (
                            <span className={styles.logtitle}>采样数量</span>
                          ) : null}
                          <Form.Item
                            // label="采样数量"
                            {...restField}
                            rules={[
                              {
                                required: true,
                                message: '必填项',
                              },
                            ]}
                            name={[name, 'maxSnapshots']}
                          >
                            <InputNumber style={{ width: '100%' }} max={5} min={1} />
                          </Form.Item>
                        </Col>
                        <Col span={4}>
                          {key === 0 ? (
                            <span className={styles.logtitle}>类型</span>
                          ) : null}
                          <Form.Item
                            // label="类型"
                            {...restField}
                            name={[name, 'type']}
                            rules={[
                              {
                                required: true,
                                message: '必填项',
                              },
                            ]}
                          >
                            <Select options={LOG_KNOW_TYPE} />
                          </Form.Item>
                        </Col>
                        <Col span={6} className={styles.mg8}>
                          {key === 0 ? (
                            <span className={styles.logtitle}>值</span>
                          ) : null}
                          <Form.Item
                            // label="值"
                            {...restField}
                            name={[name, 'values']}
                            rules={[
                              {
                                required: true,
                                message: '必填项',
                              },
                            ]}
                          >
                            <Select mode="tags" />
                          </Form.Item>
                        </Col>
                        {fields.length > 1 ? (
                          <Col span={2} style={{ marginLeft: 8, marginTop: 4 }}>
                            <a
                              className={key === 0 ? styles.logOperate : ''}
                              onClick={() => remove(name)}
                            >
                              删除
                            </a>
                          </Col>
                        ) : null}
                      </Row>
                    ))}
                    <Col offset={2}>
                      <Form.Item wrapperCol={{ span: 22 }}>
                        <Button
                          type="dashed"
                          onClick={() => add()}
                          block
                          style={{ borderColor: '#46a6ff' }}
                        >
                          新增
                        </Button>
                      </Form.Item>
                    </Col>
                  </>
                )}
              </Form.List>
            );
          } else {
            return null;
          }
        }}
      </Form.Item>
    </>
  );
};

export default LogKnow;
