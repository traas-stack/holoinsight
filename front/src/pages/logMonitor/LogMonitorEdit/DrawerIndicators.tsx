import {
  Button,
  Col,
  Drawer,
  Form,
  Input,
  Radio,
  Row,
  Select,
  Space,
  Typography,
} from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useEffect, useState } from 'react';
import $i18n from '../../../i18n';
import styles from './DrawerFilter.less';

const layout = {
  wrapperCol: { span: 16 },
};

const { Option } = Select;
const { Text } = Typography;
interface IProps {
  id?: any;
  tenant?: string;
  title: string;
  splitForm: any;
  tableInfo: any;
  edit: boolean;
  onChange: (p?: any, d?: any) => void;
}

const DrawerIndicators = (props: IProps) => {
  const { title, onChange, splitForm, edit, tableInfo, id } = props;
  const [form] = Form.useForm();
  const [visible, setFilVisible] = useState(false);
  const [countVisible, setCountVisible] = useState(false);
  const [metricType, setMetricType] = useState();
  const [numoptions, setNumOptions] = useState<any[]>([]);
  const [tagoptions, setTagOptions] = useState<any[]>([]);
  const data = splitForm.getFieldValue('collectMetrics');
  const isPattern = splitForm.getFieldValue('logPattern');
  useEffect(() => {
    const colsSplit = splitForm.getFieldValue('colsSplit') || [];
    const options = colsSplit.map((item: any) => {
      return {
        columntype: item.dimType,
        name: item.name,
        item: item,
      };
    });
    const numoptionsd = options.filter(
      (item: any) => item.columntype === 'VALUE',
    );
    const tagoptionsd = options.filter(
      (item: any) => item.columntype === 'DIM',
    );

    setNumOptions(numoptionsd);
    setTagOptions(tagoptionsd);
  }, [visible]);

  const showDrawer = () => {
    setFilVisible(true);
  };
  const onClose = () => {
    setFilVisible(false);
  };
  const handlesubmit = () => {
    form
      .validateFields()
      .then((values: any) => {
        setFilVisible(false);
        if (onChange) {
          onChange(values);
        }
        // onChange && onChange(values);
      })
      .catch((e) => {});
  };

  // 编辑时初始化 form
  useEffect(() => {
    if (edit) {
      if (tableInfo && Object.keys(tableInfo).length > 0) {
        setMetricType(tableInfo.metricType);
        form.setFieldsValue({
          ...tableInfo,
        });
      }
    } else {
      form.resetFields();
    }
  }, [tableInfo]);
  useEffect(() => {
    if (form.getFieldValue('metricType') === 'count') {
      setCountVisible(true);
      form.setFieldsValue({ func: 'count' });
    } else {
      setCountVisible(false);
    }
  }, [form.getFieldValue('metricType')]);

  const selectAll = () => {
    let tags = tagoptions.map((item: any) => {
      return item.name;
    });
    form.setFieldsValue({ tags });
  };
  return (
    <>
      {edit ? (
        <a onClick={showDrawer}>{title}</a>
      ) : (
        <Button
          type="dashed"
          icon={<PlusOutlined />}
          onClick={showDrawer}
          style={{ borderColor: '#46a6ff', width: '100%' }}
          disabled={(data || []).length > 0 && isPattern}
        >
          {title}
        </Button>
      )}

      <Drawer
        title={title}
        width={800}
        placement="right"
        onClose={onClose}
        open={visible}
        closable={false}
        footer={
          <div>
            <Button
              type="primary"
              style={{ marginRight: 18 }}
              onClick={() => handlesubmit()}
            >
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Save',
                dm: '保存',
              })}
            </Button>

            <Button
              onClick={() => {
                setFilVisible(false);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Cancel',
                dm: '取消',
              })}
            </Button>
          </div>
        }
        footerStyle={{
          display: 'flex',
          justifyContent: 'flex-end',
        }}
      >
        <div className={styles.filtercontainer}>
          <Form
            form={form}
            layout="horizontal"
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Name',
                dm: '名称',
              })}
              name="tableName"
              {...layout}
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.NameIsRequired',
                    dm: '名称是必填项',
                  }),
                },

                () => ({
                  validator(_, value) {
                    if (!/^[0-9a-zA-Z]{1,}[0-9a-zA-Z_]*$/.test(value)) {
                      return Promise.reject(
                        new Error(
                          $i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.PleaseEnterEnglishAndUnderscore',
                            dm: '请输入数字，英文和下划线',
                          }),
                        ),
                      );
                    } else {
                      return Promise.resolve();
                    }
                  },
                }),

                () => ({
                  validator(_, value) {
                    const tableArr = splitForm.getFieldsValue().collectMetrics;
                    const nameArr = (tableArr || []).map((item: any) => {
                      return item.tableName;
                    });
                    if (edit || !nameArr.includes(value)) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      new Error(
                        $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.TheNameOfTheMetric',
                          dm: '监控指标名称不能重复',
                        }),
                      ),
                    );
                  },
                }),
              ]}
            >
              <Input
                placeholder={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Name',
                  dm: '名称',
                })}
                // disabled={edit}
                // addonAfter={
                //   edit && !form.getFieldValue('name') ? `_${id}` : null
                // }
              />
            </Form.Item>
            {edit && id ? (
              <Form.Item label="ID" hidden>
                <Text disabled>{id}</Text>
              </Form.Item>
            ) : null}
            {!isPattern ? (
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.IndicatorDefinition',
                  dm: '指标定义',
                })}
                required
                name="metricType"
                {...layout}
              >
                <Radio.Group
                  onChange={(e) => {
                    setMetricType(e.target.value);
                  }}
                >
                  <Radio value="contains">
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.KeywordCount',
                      dm: '关键词次数统计',
                    })}
                  </Radio>

                  <Radio value="count">
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.LogTraffic',
                      dm: '日志流量',
                    })}
                  </Radio>

                  <Radio value="select">
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.NumericalExtraction',
                      dm: '数值提取',
                    })}
                  </Radio>
                </Radio.Group>
              </Form.Item>
            ) : null}

            {metricType === 'contains' && (
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Keywords',
                  dm: '关键词',
                })}
                name="containsValue"
                required={metricType === 'contains'}
                {...layout}
              >
                <Input
                  placeholder={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.EnterAKeyword',
                    dm: '输入关键字',
                  })}
                />
              </Form.Item>
            )}

            {metricType === 'select' && (
              <Form.Item
                name="metrics"
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.IndicatorSelection',
                  dm: '指标选择',
                })}
                {...layout}
              >
                <Select placeholder="Please select" style={{ width: '100%' }}>
                  {numoptions.map((item: any) => {
                    return <Option key={item.name}>{item.name}</Option>;
                  })}
                </Select>
              </Form.Item>
            )}
            {!isPattern ? (
              <Row style={{ marginLeft: 20 }}>
                <Col span={18}>
                  <Form.Item
                    name="tags"
                    label={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.DimensionSelection',
                      dm: '维度选择',
                    })}
                    rules={[{ required: false, message: '' }]}
                    {...layout}
                  >
                    <Select
                      mode="multiple"
                      placeholder="Please select"
                      style={{ width: '100%' }}
                    >
                      {tagoptions.map((item: any) => {
                        return <Option key={item.name}>{item.name}</Option>;
                      })}
                    </Select>
                  </Form.Item>
                </Col>
                <Col pull={3}>
                  <Button onClick={selectAll}>选择所有维度</Button>
                </Col>
              </Row>
            ) : null}

            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.CalculationType',
                dm: '计算类型',
              })}
              rules={[{ required: true, message: '' }]}
              {...layout}
            >
              <Row>
                {!isPattern ? (
                  <>
                    <Col span={7}>
                      <Form.Item name="funcType" initialValue="sample" noStyle>
                        <Select
                          placeholder={$i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.SimpleType',
                            dm: '简单类型',
                          })}
                        >
                          <Option value="sample">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.SimpleType',
                              dm: '简单类型',
                            })}
                          </Option>

                          {/* <Option value="complex">复杂类型</Option> */}
                        </Select>
                      </Form.Item>
                    </Col>

                    <Col span={13} offset={1}>
                      <Form.Item
                        name="func"
                        rules={[
                          {
                            required: true,
                            message: $i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.SelectACalculationMethod',
                              dm: '请选择计算方式',
                            }),
                          },
                        ]}
                      >
                        <Select disabled={countVisible}>
                          <Option value="count">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.NumberOfRows',
                              dm: '求行数',
                            })}
                          </Option>

                          <Option value="sum">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Summation',
                              dm: '求和',
                            })}
                          </Option>

                          <Option value="max">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.FindTheMaximumValue',
                              dm: '求最大值',
                            })}
                          </Option>

                          <Option value="min">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.FindTheMinimumValue',
                              dm: '求最小值',
                            })}
                          </Option>

                          <Option value="avg">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerIndicators.Average',
                              dm: '求平均值',
                            })}
                          </Option>
                        </Select>
                      </Form.Item>
                    </Col>
                  </>
                ) : (
                  <Select
                    disabled
                    options={[{ label: '日志采样', value: 'loganalysis' }]}
                    value="loganalysis"
                  ></Select>
                )}
              </Row>
            </Form.Item>
            {!isPattern ? (
              <Form.List name="afterFilters">
                {(fields, { add, remove }) => (
                  <div className={styles.addFilter}>
                    <Button
                      style={{ marginBottom: 15 }}
                      onClick={() => {
                        add();
                      }}
                    >
                      新增后置过滤
                    </Button>
                    {fields.map(({ key, name }) => (
                      <Space
                        key={key}
                        style={{
                          display: 'flex',
                          marginBottom: 8,
                        }}
                        align="baseline"
                      >
                        <Form.Item
                          name={[name, 'name']}
                          rules={[
                            {
                              required: true,
                              message: $i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Required',
                                dm: '必填项',
                              }),
                            },
                          ]}
                        >
                          <Select
                            placeholder="Please select"
                            style={{ width: 100 }}
                          >
                            {tagoptions.map((item: any) => {
                              return (
                                <Option key={item.name}>{item.name}</Option>
                              );
                            })}
                          </Select>
                        </Form.Item>
                        <Form.Item
                          name={[name, 'filterType']}
                          rules={[
                            {
                              required: true,
                              message: $i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Required',
                                dm: '必填项',
                              }),
                            },
                          ]}
                        >
                          <Select
                            style={{ width: 90, marginLeft: 10 }}
                            options={[
                              { value: 'IN', label: '白名单' },
                              { value: 'NOT_IN', label: '黑名单' },
                            ]}
                          />
                        </Form.Item>
                        <Form.Item
                          name={[name, 'values']}
                          rules={[
                            {
                              required: true,
                              message: $i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Required',
                                dm: '必填项',
                              }),
                            },
                          ]}
                        >
                          <Select
                            mode="tags"
                            style={{ marginLeft: 10, width: 400 }}
                          />
                        </Form.Item>
                        <Button
                          danger
                          style={{ marginLeft: 10 }}
                          onClick={() => remove(name)}
                        >
                          删除
                        </Button>
                      </Space>
                    ))}
                  </div>
                )}
              </Form.List>
            ) : null}
          </Form>
        </div>
      </Drawer>
    </>
  );
};

export default DrawerIndicators;
