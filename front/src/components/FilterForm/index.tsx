import { useState, useEffect } from 'react';

import {
  Select,
  Form,
  Row,
  Col,
  Button,
  Transfer,
  Input,
  Tag,
} from 'antd';
import $i18n from '@/i18n/index';
const { Option } = Select;
import { PlusOutlined, MinusOutlined } from '@ant-design/icons';
import styles from './index.less';
import { getTenantAppByCondition } from '@/services/infra/api';

const layout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 },
};

export default function FilterForm(props: any) {
  const { form, selectKey, setSelectKey, backData, serverData, type } = props;
  const [targetKeys, setTargetKeys] = useState<any>([]);
  const [selectedKeys, setSelectedKey] = useState<any>([]);
  const [appList, setAppList] = useState([]);

  useEffect(() => {
    setTargetKeys(backData);
  }, [backData && JSON.stringify(backData)]);
  useEffect(() => {
    getTenantAppByCondition({}).then((res:any) => {
      const list = (res || []).map((item:any) => {
        let newItem:any = {};
        newItem.label = item.app;
        newItem.value = item.app;

        return {
          ...item,
          label: item.app,
          value: item.app,
        };
      });
      setAppList(list);
    });
  }, []);
  const handleChange = (targetKeyss: any) => {
    setTargetKeys(targetKeyss);
  };
  const handleSelectChange = (
    sourceSelectedKeys: string[],
    targetSelectedKeys: string[],
  ) => {
    setSelectedKey([...sourceSelectedKeys, ...targetSelectedKeys]);
  };
  return (
    <>
      <Form.Item
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.TypeFiltering',
          dm: '类型筛选',
        })}
        name="typeFilter"
        {...layout}
        rules={[
          {
            required: true,
            message: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.SelectACollectionType',
              dm: '请选择采集类型',
            }),
          },
        ]}
      >
        <Select
          style={{ width: 200 }}
          onChange={(value) => {
            setSelectKey(value);
            if (value === 'label') {
              form.resetFields(['collectRanges']);
            } else {
              form.setFieldsValue({
                collectRanges: [],
              });
            }
          }}
        >
          <Option value="app" key='app'>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Application',
              dm: '应用',
            })}
          </Option>

          {/* <Option value="label"  key='label'>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Label',
              dm: '标签',
            })}
          </Option> */}

          {/* <Option value="_uk">
                        {$i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Machine',
                            dm: '机器',
                        })}
                    </Option> */}
        </Select>
      </Form.Item>
      {selectKey ? (
        <Form.Item
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.MetadataFiltering',
            dm: '元数据筛选',
          })}
          {...layout}
          name={selectKey === 'lable' ? undefined : 'collectRanges'}
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.PleaseEnterTheCollectionRange',
                dm: '请输入采集范围',
              }),
            },
          ]}
        >
          {selectKey === '_uk' ? (
            <Transfer
              listStyle={{ width: type === 'integration' ? 300 : 600 }}
              selectAllLabels={[
                $i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Select',
                  dm: '选择',
                }),
                $i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Selected',
                  dm: '已选',
                }),
              ]}
              showSelectAll={false}
              showSearch
              dataSource={serverData}
              targetKeys={targetKeys}
              selectedKeys={selectedKeys}
              render={(item) => item.desc}
              onChange={handleChange}
              onSelectChange={handleSelectChange}
            />
          ) : selectKey === 'app' ? (
            <Select
              mode="multiple"
              style={{ width: 500 }}
              placeholder="Tags Mode"
            >
              {appList.map((item) => {
                return (
                  <Option value={item.value} key={item.value}>
                    <div className={styles.optionCnt}>
                      <div className={styles.optionLabel}>{item.label}</div>
                      {(item.workspace || item.namespace) &&
                        <div className={styles.optionTag}>
                          {item.workspace && <Tag color="blue">workspace: {item.workspace}</Tag>}
                          {item.namespace && <Tag color="geekblue">namespace: {item.namespace}</Tag>}
                        </div>}
                    </div>
                  </Option>
                );
              })}
            </Select>
          ) : (
            <Form.List name="collectRanges" initialValue={[{}]}>
              {(fields, { add: addItem, remove: removeItem }) => {
                return (
                  <>
                    {fields.map((fieldAll: any) => {
                      return (
                        <div key={fieldAll.name}>
                          <Row>
                            <Col span={6}>
                              <Form.Item
                                labelCol={{ span: 2, offset: 1 }}
                                name={[fieldAll.name, 'key']}
                                rules={[
                                  {
                                    required: true,
                                    message: $i18n.get({
                                      id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Required',
                                      dm: '必填项',
                                    }),
                                  },
                                ]}
                              >
                                <Input
                                  placeholder={$i18n.get({
                                    id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.FilterKey',
                                    dm: '筛选key',
                                  })}
                                />
                              </Form.Item>
                            </Col>

                            <Col span={6}>
                              <Form.Item
                                labelCol={{ span: 2, offset: 1 }}
                                name={[fieldAll.name, 'value']}
                                rules={[
                                  {
                                    required: true,
                                    message: $i18n.get({
                                      id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.Required',
                                      dm: '必填项',
                                    }),
                                  },
                                ]}
                              >
                                {/* <Input placeholder='筛选Value' /> */}

                                <Select
                                  placeholder={$i18n.get({
                                    id: 'holoinsight.logMonitor.LogMonitorEdit.BasicInfo.FilterValue',
                                    dm: '筛选Value',
                                  })}
                                  mode="tags"
                                  tokenSeparators={[',']}
                                ></Select>
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
          )}
        </Form.Item>
      ) : null}
    </>
  );
}
