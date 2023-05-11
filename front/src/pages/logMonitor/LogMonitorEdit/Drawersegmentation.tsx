import { Input, Select, Form, Drawer, Row, Col, Button } from 'antd';
import styles from './LogSegmentation.less';
import $i18n from '../../../i18n';
const { Option } = Select;

interface IProps {
  value: any;
  onChange: (p?: any, d?: any) => void;
}

const DrawerSegmentation = (props: IProps) => {
  const { value, onChange } = props;
  const { dimensionVisible, splitLists, splitListsIndex } = value;
  const [form] = Form.useForm();
  const saveDimension = () => {
    form.validateFields().then((values) => {
      const oldsplitList = splitLists[splitListsIndex];
      const newsplitList = {
        ...oldsplitList,
        name: values.listName,
        columntype: values.listType,
        defaultValue: values.defaultFun,
        addfun: values.addfun,
      };

      splitLists[splitListsIndex] = newsplitList;
      setTimeout(() => {
        onChange(false, [...splitLists]);
      }, 1000);
      form.setFieldsValue({ listName: '', defaultFun: '', addfun: [] });
    });
  };

  return (
    <Drawer
      title={$i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.DimensionDefinition',
        dm: '维度定义',
      })}
      width={720}
      placement="right"
      maskClosable
      open={dimensionVisible}
      closable={false}
      footer={
        <div>
          <Button
            type="primary"
            style={{ marginRight: 18 }}
            onClick={() => {
              saveDimension();
            }}
          >
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.Save',
              dm: '保存',
            })}
          </Button>

          <Button
            onClick={() => {
              onChange(false, splitLists);
            }}
          >
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.Cancel',
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
      <Form form={form}>
        <div className={styles.filtercontainer}>
          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.ColumnName',
              dm: '列名称',
            })}
          </div>

          <Form.Item
            name="listName"
            wrapperCol={{ span: 22 }}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.PleaseEnterTheName',
                  dm: '请填写名称',
                }),
              },
            ]}
          >
            <Input />
          </Form.Item>

          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.ColumnType',
              dm: '列类型',
            })}
          </div>

          <Form.Item name="listType" initialValue={splitLists?.[splitListsIndex]?.columntype}>
            <Select style={{ width: 120 }}>
              <Option value="dimension">
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.DimensionColumn',
                  dm: '维度列',
                })}
              </Option>

              <Option value="numerical">
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.NumericColumn',
                  dm: '数值列',
                })}
              </Option>
            </Select>
          </Form.Item>

          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.ColumnValue',
              dm: '列值',
            })}
            wrapperCol={{ span: 22 }}
          >
            {splitLists?.[splitListsIndex]?.samplingvalue}
          </Form.Item>

          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.FunctionConversion',
              dm: '函数转换',
            })}
          </div>

          <Form.Item>
            <Form.List name="addfun">
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }) => (
                    <Form.Item key={key} wrapperCol={{ span: 22 }}>
                      <Row>
                        <Col span={6}>
                          <Form.Item {...restField} name={[name, 'funName']}>
                            <Select
                              placeholder={$i18n.get({
                                id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.EnterOrSelectAFunction',
                                dm: '请输入或选择函数名',
                              })}
                              allowClear
                            >
                              {[
                                '@in',
                                '@!in',
                                '@contains',
                                '@!contains',
                                '@left_right',
                                '@combox',
                                '@simpleRegex',
                              ].map((val: string) => {
                                return (
                                  <Option key={val} value={val}>
                                    {val}
                                  </Option>
                                );
                              })}
                            </Select>
                          </Form.Item>
                        </Col>

                        <Col span={14} offset={1}>
                          <Form.Item {...restField} name={[name, 'funParameter']}>
                            <Input
                              placeholder={$i18n.get({
                                id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.Parameter',
                                dm: '参数',
                              })}
                            />
                          </Form.Item>
                        </Col>

                        <Col span={2} style={{ marginLeft: 18, marginTop: 4 }}>
                          <a onClick={() => remove(name)}>
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.Delete',
                              dm: '删除',
                            })}
                          </a>
                        </Col>
                      </Row>
                    </Form.Item>
                  ))}

                  <Form.Item wrapperCol={{ span: 22 }}>
                    <Button
                      type="dashed"
                      onClick={() => add()}
                      block
                      style={{ borderColor: '#46a6ff' }}
                    >
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.AddLogPath',
                        dm: '新增日志路径',
                      })}
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item>

          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.Drawersegmentation.DefaultValue',
              dm: '默认值',
            })}
          </div>

          <Form.Item name="defaultFun" wrapperCol={{ span: 22 }}>
            <Input />
          </Form.Item>
        </div>
      </Form>
    </Drawer>
  );
};

export default DrawerSegmentation;
