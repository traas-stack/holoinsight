import {
  Button,
  Card,
  Form,
  Input,
  Row,
  Select,
  Switch,
} from 'antd';
import { useState } from 'react';
import {
  DownOutlined,
  MinusCircleOutlined,
  UpOutlined,
} from '@ant-design/icons';
import LogFileModal from './LogFileModal';
import FilterForm from '@/components/FilterForm';
import $i18n from '../../../i18n';

const { Option } = Select;

interface IProps {
  form: any;
  editItem: any;
  selectKey: string;
  setSelectKey: any;
  serverData: any;
  logFilter?: boolean | undefined;
}

const layout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 },
};

const pluginTypes = {
  custom: $i18n.get({
    id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.LogMonitoring',
    dm: '日志监控',
  }),
};

const BasicInfo = (props: IProps) => {
  const { form, editItem, selectKey, setSelectKey, serverData, logFilter } =
    props;
  const [buttonIndex, setButtonIndex] = useState<Number>(-1);
  const [visible, setVisible] = useState(false);
  const [advOptShown, setAdvOptShown] = useState(false);
  const serverHost = () => {
    let ip: string = '';
    const uk =
      form.getFieldValue('typeFilter') === '_uk'
        ? form.getFieldValue('collectRanges')?.[0] || ''
        : '';
    (serverData || []).forEach((item: any) => {
      if (item.key === uk) {
        ip = item.title;
      }
    });
    return ip || '';
  };

  return (
    <>
      <Card
        title={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.BasicConfiguration',
          dm: '基本配置',
        })}
      >
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.MonitoringItemType',
            dm: '监控项类型',
          })}
          name="pluginType"
          initialValue="custom"
          hidden
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.PleaseEnterTheName',
                dm: '请填写名称',
              }),
            },
          ]}
          {...layout}
        >
          <Select style={{ width: 300 }} disabled>
            <Option value="custom">
              {pluginTypes[form.getFieldValue('pluginType')]}
            </Option>
          </Select>
        </Form.Item>

        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Name',
            dm: '名称',
          })}
          name="name"
          {...layout}
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.PleaseEnterTheName',
                dm: '请填写名称',
              }),
            },
          ]}
        >
          <Input style={{ width: 300 }} />
        </Form.Item>

        <Form.Item
          name="status"
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Online',
            dm: '上线',
          })}
          {...layout}
          valuePropName="checked"
          initialValue={'checked'}
        >
          <Switch
            checkedChildren={$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Online',
              dm: '上线',
            })}
            unCheckedChildren={$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Offline',
              dm: '下线',
            })}
          />
        </Form.Item>
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.CollectionCycle',
            dm: '采集周期',
          })}
          name="periodType"
          initialValue="MINUTE"
          {...layout}
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.SelectACollectionCycle',
                dm: '请选择采集周期',
              }),
            },
          ]}
        >
          <Select style={{ width: 300 }}>
            <Option value="MINUTE">1min</Option>

            <Option value="SECOND">1s</Option>

            <Option value="FIVE_SECOND">5s</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.CollectionTable',
            dm: '采集表',
          })}
          {...layout}
          name="tableName"
          hidden
        ></Form.Item>
        {logFilter ? (
          <FilterForm
            serverData={serverData}
            form={form}
            backData={editItem?.collectRanges}
            selectKey={selectKey}
            setSelectKey={setSelectKey}
          />
        ) : null}
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.LogPath',
            dm: '日志路径',
          })}
          {...layout}
        >
          <Form.List
            name="logPath"
            rules={[
              {
                validator: async (_, values) => {
                  if (!values || values.length < 1) {
                    return Promise.reject(
                      new Error(
                        $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.ConfigureTheLogPath',
                          dm: '请配置日志路径',
                        }),
                      ),
                    );
                  }
                },
              },
            ]}
          >
            {(fields, { add, remove }, { errors }) => {
              return (
                <>
                  {fields.map(({ key, name, ...restField }, index) => (
                    <Row key={key} style={{ marginBottom: 18 }}>
                      <Form.Item
                        {...restField}
                        name={[name, 'type']}
                        noStyle
                        initialValue={'path'}
                        rules={[
                          {
                            required: true,
                            message: $i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.SelectAPath',
                              dm: '请选择路径',
                            }),
                          },
                        ]}
                      >
                        <Select
                          style={{ width: 220 }}
                          placeholder={$i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.AbsoluteLogPath',
                            dm: '绝对日志路径',
                          })}
                        >
                          <Option value="path">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.AbsoluteLogPath',
                              dm: '绝对日志路径',
                            })}
                          </Option>

                          <Option value="glob">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.RegularMatchingLogPath',
                              dm: '正则匹配日志路径',
                            })}
                          </Option>

                          <Option value="format">
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.MatchTheLogPathContaining',
                              dm: '匹配包含变量的日志路径',
                            })}
                          </Option>
                        </Select>
                      </Form.Item>

                      <Form.Item
                        noStyle
                        shouldUpdate={(prevValues, currentValues) =>
                          prevValues.logPath !== currentValues.logPath
                        }
                      >
                        {({ getFieldValue }): any => {
                          const logPath = getFieldValue('logPath');
                          if (logPath[index]?.type === 'format') {
                            return (
                              <Form.Item
                                noStyle
                                name={[name, 'path']}
                                rules={[
                                  {
                                    required: true,
                                    message: $i18n.get({
                                      id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.EnterALogPathThat',
                                      dm: '请输入匹配包含变量的日志路径',
                                    }),
                                  },
                                ]}
                              >
                                <Input
                                  style={{ width: 500 }}
                                  placeholder={$i18n.get({
                                    id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.ExampleExportLogsAppIns',
                                    dm: '例1，export/Logs/$APP$/$INS_NAME$/hello-world.log ； 例2， $DEPLOY_PATH$',
                                  })}
                                />
                              </Form.Item>
                            );
                          }

                          if (logPath[index]?.type === 'glob') {
                            return (
                              <>
                                <Form.Item
                                  noStyle
                                  name={[name, 'path']}
                                  rules={[
                                    {
                                      required: true,
                                      message: $i18n.get({
                                        id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.EnterTheRegularMatchingExpression',
                                        dm: '填写日志路径的正则匹配表达式',
                                      }),
                                    },
                                  ]}
                                >
                                  <Input
                                    style={{ width: 500 }}
                                    placeholder={$i18n.get({
                                      id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.EnterTheRegularMatchingExpression',
                                      dm: '填写日志路径的正则匹配表达式',
                                    })}
                                  />
                                </Form.Item>
                              </>
                            );
                          }
                          return (
                            <>
                              <Form.Item
                                name={[name, 'path']}
                                rules={[
                                  {
                                    required: true,
                                    message: $i18n.get({
                                      id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.EnterTheAbsolutePathLog',
                                      dm: '请填写绝对路径日志',
                                    }),
                                  },
                                ]}
                                noStyle
                              >
                                <Input
                                  style={{ width: 500 }}
                                  placeholder={$i18n.get({
                                    id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.EnterTheAbsoluteLogPath',
                                    dm: '填写绝对日志路径，以 .log 结尾',
                                  })}
                                  disabled={!logPath[index]}
                                />
                              </Form.Item>

                              <Button
                                onClick={() => {
                                  setVisible(true);
                                  setButtonIndex(index);
                                }}
                                disabled={!logPath[index]?.type}
                              >
                                {$i18n.get({
                                  id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.ScanSelection',
                                  dm: '扫描选取',
                                })}
                              </Button>
                            </>
                          );
                        }}
                      </Form.Item>

                      <Form.Item noStyle>
                        <MinusCircleOutlined
                          style={{ margin: 8 }}
                          onClick={() => remove(name)}
                        />
                      </Form.Item>
                    </Row>
                  ))}

                  <Form.Item wrapperCol={{ span: 22 }}>
                    <Button
                      type="dashed"
                      onClick={() => add()}
                      block
                      style={{ borderColor: '#46a6ff' }}
                    >
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.AddLogPath',
                        dm: '新增日志路径',
                      })}
                    </Button>
                    <Form.ErrorList errors={errors} />
                  </Form.Item>
                </>
              );
            }}
          </Form.List>
        </Form.Item>
        <a
          style={{ color: 'rgb(24, 144, 255)', marginLeft: 60 }}
          onClick={() => {
            setAdvOptShown(!advOptShown);
          }}
        >
          高级选项{advOptShown ? <UpOutlined /> : <DownOutlined />}
        </a>
        {advOptShown && (
          <>
            <Form.Item
              name="maxKeySize"
              style={{ marginTop: 10 }}
              {...layout}
              label="Key数量上限"
              initialValue={1000}
            >
              <Select
                style={{ width: 200 }}
                options={[
                  { value: 1000 },
                  { value: 2000 },
                  { value: 5000 },
                  { value: 10000 },
                  { value: 20000 },
                ]}
              ></Select>
            </Form.Item>
            <Form.Item
              name="keyCleanInterval"
              style={{ marginTop: 10 }}
              {...layout}
              label={'自动清理Key'}
              initialValue="不自动清理"
            >
              <Select
                style={{ width: 400 }}
                options={[
                  { value: false, label: '不自动清理' },
                  {
                    value: 30,
                    label: '清理半小时内无数据的key，半小时执行一次',
                  },
                  { value: 60, label: '清理1小时内无数据的key，1小时执行一次' },
                  {
                    value: 90,
                    label: '清理一个半小时内无数据的key，一个半小时执行一次',
                  },
                ]}
              ></Select>
            </Form.Item>
          </>
        )}
      </Card>
      {form.getFieldValue('typeFilter') && (
        <LogFileModal
          btnText={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.ScanSelection',
            dm: '扫描选取',
          })}
          visible={visible}
          logpath={form.getFieldValue('logPath')?.[buttonIndex]?.path}
          preApp={
            form.getFieldValue('typeFilter') === 'app'
              ? form.getFieldValue('collectRanges')?.[0]
              : ''
          }
          preHost={serverHost()}
          preLabel={
            form.getFieldValue('typeFilter') === 'label'
              ? form.getFieldValue('collectRanges') || ''
              : ''
          }
          handleConfirm={(newLog: any) => {
            let logPath = form.getFieldValue('logPath') || [];
            logPath[buttonIndex].path = newLog;
            form.setFieldsValue({ logPath });
          }}
          handleClose={() => {
            setVisible(false);
          }}
        />
      )}
    </>
  );
};
export default BasicInfo;
