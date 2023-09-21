import $i18n from '@/i18n';
import { NAMES } from '@/pages/alarm/component/const';
import { queryMetricLike, querySchema } from '@/services/tenant/api';
import { useSearchParams } from '@umijs/max';
import {
  AutoComplete,
  Button,
  Col,
  Form,
  Input,
  Radio,
  Row,
  Select,
} from 'antd';
import React, { useEffect, useState } from 'react';
const { Option } = Select;

export default function CommonFormList(props: any) {
  const {
    fieldAll = null,
    triggerType,
    setShowGroup,
    showGroup,
    form,
    ipDropArr,
    setIpDropArr,
    assembleStatus,
    setAssembleStatus,
  } = props;
  const [typeDropArr, setTypeDropArr] = useState([]); //设置下拉的选项
  const [metricLength, setMetricLength] = useState(1);
  const [isSelect, setIsSelect] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();
  useEffect(() => {
    // queryMetricLike().then((res) => {
    //   if (Array.isArray(res)) {
    //     const dropArr = res.map((item: string) => {
    //       const newItem: any = {};
    //       newItem.label = item;
    //       newItem.value = item;
    //       return newItem;
    //     });
    //     setTypeDropArr(dropArr);
    //   }
    // });
    if (location.search.includes('app') && !isSelect) {
      setIsSelect(true);
    }
  }, []);
  const isRequire = (fieldKey) => {
    if (
      document.querySelector('.select-box .ant-select-selection-item')
        ?.title === searchParams.get('app') &&
      fieldKey === 0
    ) {
      return false;
    }
    return true;
  };

  // useEffect(() => {
  //   const mapList = Array.isArray(
  //     form.getFieldValue()?.triggers[fieldAll.name]?.datasources,
  //   )
  //     ? form.getFieldValue()?.triggers[fieldAll.name]?.datasources
  //     : [];
  //   let expressionVal = '';
  //   mapList.forEach((item, idx) => {
  //     if (idx > 0) {
  //       expressionVal = expressionVal + '+' + NAMES[idx];
  //     } else {
  //       expressionVal = NAMES[idx];
  //     }
  //   });
  //   mapList.length > 1 ? setShowSwitch(false) : setShowSwitch(true);
  //   form.setFieldValue(['triggers', fieldAll.name, 'query'], expressionVal);
  // }, [metricLength]);

  function handleSearch(value: string) {
    queryMetricLike(value).then((res) => {
      if (Array.isArray(res)) {
        const dropArr: any = res.map((item: string) => {
          const newItem: any = {};
          newItem.label = item;
          newItem.value = item;
          return newItem;
        });
        setTypeDropArr(dropArr);
      }
    });
  }

  function handleChangeCheckBox(e: any, id: number, idAll: number = 0) {
    isSelect ? setIsSelect(false) : null;
    if (e.length) {
      form.resetFields([['triggers', idAll, 'datasources', id, 'filters']]);
      if (Array.isArray(showGroup[idAll])) {
        showGroup[idAll].push(id);
      } else {
        showGroup[idAll] = [id];
      }
      setShowGroup([...showGroup]);
    } else {
      form.resetFields([['triggers', idAll, 'datasources', id, 'filters']]);
      const newArr = (showGroup[idAll] || []).filter(
        (item: any) => item !== id,
      );

      showGroup[idAll] = newArr;
      setShowGroup([...showGroup]);
    }
  }

  function handleChange(value: string, fieldAll: any, field: any) {
    const data = form.getFieldValue([
      'triggers',
      fieldAll.name,
      'datasources',
      field.name,
    ]);

    data.groupBy = [];
    (data.filters || []).forEach((ele: any, n: number) => {
        if (ele && ele?.key) {
            ele.key = '';
        }
    });
    form.setFields([
      {
        name: ['triggers', fieldAll.name, 'datasources', field.name],
        value: data,
      },
    ]);

    // form.setFieldsValue({ triggers: "" });
    if (value) {
      querySchema({
        name: value,
      })
        .then((res) => {
          const newRes = res?.tags?.map((item: string) => {
            const newItem: any = {};
            newItem.label = item;
            newItem.value = item;
            return newItem;
          });
          if (ipDropArr?.[fieldAll.name]?.[field.name]) {
            ipDropArr[fieldAll.name][field.name] = newRes;
          } else {
            ipDropArr[fieldAll.name] = [];
            ipDropArr[fieldAll.name][field.name] = newRes;
          }
          setIpDropArr([...ipDropArr]);
        })
        .catch((err) => {});
    }
  }

  function handleChangeRule(value: string, fieldAll: any, field: any) {
    let data = form.getFieldValue([
      'triggers',
      fieldAll.name,
      'datasources',
      field.name,
    ]);

    if (value === 'none') {
      data.groupBy = [];
      data.downsample = null;
    } else {
      data.downsample = 1;
    }
    form.setFields([
      {
        name: ['triggers', fieldAll.name, 'datasources', field.name],
        value: data,
      },
    ]);

    const newValue = value === 'none' ? 'true' : 'false';
    if (assembleStatus?.[fieldAll.name]) {
      assembleStatus[fieldAll.name][field.name] = newValue;
    } else {
      assembleStatus[fieldAll.name] = [];
      assembleStatus[fieldAll.name][field.name] = newValue;
    }
    setAssembleStatus([...assembleStatus]);
  }

  return (
    <>
      <Form.List
        name={fieldAll ? [fieldAll.name, 'datasources'] : 'datasources'}
        initialValue={[
          {
            fieldKey: 0,
            isListField: true,
            key: 0,
            // name: 0,
            isEdit: false,
          },
        ]}
      >
        {(fields, { add, remove }) => {
          return (
            <>
              {fields.map((field: any) => {
                return (
                  <div key={field.key}>
                    <Row>
                      <Form.Item
                        label="触发条件名称"
                        name={[field.name, 'triggerTitle']}
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
                        <Input style={{ width: 600 }} />
                      </Form.Item>
                    </Row>
                    <Row className="alarm-condition">
                      <div style={{ width: '36px' }}>
                        {/* <Form.Item labelCol={{ span: 2, offset: 1 }} initialValue={NAMES[field.name] === '0' ? 'a' : NAMES[field.name]} name={[field.name, 'name']} rules={[{ required: true, message: '必填项' }]}>
                              <Input readOnly />
                             </Form.Item> */}

                        <span className="condition-text">
                          {NAMES[field.name]}
                        </span>
                      </div>

                      <Col span={3}>
                        <Form.Item
                          labelCol={{ span: 2, offset: 1 }}
                          {...field}
                          name={[field.name, 'metricType']}
                          rules={[
                            {
                              required: true,
                              message: $i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Required',
                                dm: '必填项',
                              }),
                            },
                          ]}
                          initialValue={'metric'}
                        >
                          <Select
                            placeholder={$i18n.get({
                              id: 'holoinsight.pages.alarm.Detail.IndicatorSelection',
                              dm: '指标选择',
                            })}
                            allowClear
                          >
                            <Option value="metric">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.CommonFormList.MetricMonitoring',
                                dm: '指标监控',
                              })}
                            </Option>
                            {/* <Option value="log">
                                {$i18n.get({
                                  id: 'holoinsight.components.HeaderSearch.LogMonitoring',
                                  dm: '日志监控',
                                })}
                               </Option>
                               <Option value="integration">
                                {$i18n.get({
                                  id: 'holoinsight.pages.alarm.Detail.IntegratedMonitoring',
                                  dm: '集成监控',
                                })}
                               </Option>
                               <Option value="app">
                                {$i18n.get({
                                  id: 'holoinsight.src.components.defaultLayout.ApplicationMonitoring',
                                  dm: '应用监控',
                                })}
                               </Option> */}
                          </Select>
                        </Form.Item>
                      </Col>

                      <Col span={5}>
                        <Form.Item
                          labelCol={{ span: 2, offset: 1 }}
                          {...field}
                          name={[field.name, 'metric']}
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
                          {/* <Select onChange={(value) => handleChange(value, fieldAll, field)} options={typeDropArr} >
                               </Select> */}

                          <AutoComplete
                            filterOption={true}
                            options={typeDropArr}
                            onSearch={(value) => handleSearch(value)}
                            onChange={(value) =>
                              handleChange(value, fieldAll, field)
                            }
                          />

                          {/* <Input /> */}
                        </Form.Item>
                      </Col>
                      {triggerType !== 'pql' && (
                        <>
                          <Col span={3}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              {...field}
                              name={[field.name, 'aggregator']}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                              initialValue={'avg'}
                            >
                              <Select
                                allowClear
                                onChange={(value) =>
                                  handleChangeRule(value, fieldAll, field)
                                }
                              >
                                <Option value="avg">
                                  {$i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.AvgbyAggregation',
                                    dm: 'avgBy聚合',
                                  })}
                                </Option>

                                <Option value="count">
                                  {$i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.CountbyAggregation',
                                    dm: 'countBy聚合',
                                  })}
                                </Option>

                                <Option value="min">
                                  {$i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.MinbyAggregation',
                                    dm: 'minBy聚合',
                                  })}
                                </Option>

                                <Option value="max">
                                  {$i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.MaxbyAggregation',
                                    dm: 'maxBy聚合',
                                  })}
                                </Option>

                                <Option value="sum">
                                  {$i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.SumbyAggregation',
                                    dm: 'sumBy聚合',
                                  })}
                                </Option>

                                <Option value="none">NONE</Option>
                              </Select>
                            </Form.Item>
                          </Col>

                          <Col span={3}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              {...field}
                              name={[field.name, 'groupBy']}
                            >
                              {/* <Select options={ipDropArr?.[fieldAll.name]?.[field.name] || []} allowClear>
                              </Select> */}

                              {/* <AutoComplete
                              filterOption={true}
                              options={ipDropArr?.[fieldAll?.name]?.[field?.name] || []}
                              allowClear
                              disabled={assembleStatus?.[fieldAll?.name]?.[field?.name] === 'true' ? true : false}
                              /> */}

                              <Select
                                mode="multiple"
                                options={
                                  ipDropArr?.[fieldAll?.name]?.[field?.name] ||
                                  []
                                }
                                disabled={
                                  assembleStatus?.[fieldAll?.name || 0]?.[
                                    field?.name
                                  ] === 'true'
                                    ? true
                                    : false
                                }
                              ></Select>

                              {/* <Input /> */}
                            </Form.Item>
                          </Col>

                          <Col span={3}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              {...field}
                              name={[field.name, 'downsample']}
                              initialValue={'1'}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.pages.alarm.Detail.OriginalTimeWindow',
                                  dm: '原始时间窗口',
                                })}
                                suffix={$i18n.get({
                                  id: 'holoinsight.pages.alarm.Detail.Minutes',
                                  dm: '分钟',
                                })}
                                disabled={true}
                              />
                            </Form.Item>
                          </Col>
                        </>
                      )}

                      {triggerType === 'rule' &&
                      field.name === fields.length - 1 ? (
                        <div style={{ marginRight: '10px' }}>
                          <Form.Item
                            labelCol={{ span: 2, offset: 1 }}
                            name="alarmLevel"
                          >
                            <Button
                              type="primary"
                              onClick={() => {
                                setMetricLength(
                                  (metricLength) => metricLength + 1,
                                );
                                add();
                              }}
                            >
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.AddMetricCalculation',
                                dm: '增加指标计算',
                              })}
                            </Button>
                          </Form.Item>
                        </div>
                      ) : null}

                      {fields.length > 1 ? (
                        <Form.Item labelCol={{ span: 2, offset: 1 }} name="1">
                          <Button
                            danger
                            onClick={() => {
                              handleChangeCheckBox(
                                '',
                                field.name,
                                fieldAll.name,
                              );

                              if (ipDropArr?.[fieldAll.name]?.[field.name]) {
                                ipDropArr?.[fieldAll.name]?.splice(
                                  field.name,
                                  1,
                                );

                                setIpDropArr([...ipDropArr]);
                              }
                              if (
                                assembleStatus?.[fieldAll.name]?.[field.name]
                              ) {
                                assembleStatus?.[fieldAll.name]?.splice(
                                  field.name,
                                  1,
                                );

                                setIpDropArr([...assembleStatus]);
                              }
                              remove(field.name);
                              setMetricLength(
                                (metricLength) => metricLength - 1,
                              );
                            }}
                          >
                            {$i18n.get({
                              id: 'holoinsight.pages.alarm.Detail.DeleteMetricCalculation',
                              dm: '删除指标计算',
                            })}
                          </Button>
                        </Form.Item>
                      ) : null}
                    </Row>
                    {triggerType !== 'pql' && (
                      <Form.Item
                        initialValue={''}
                        label={$i18n.get({
                          id: 'holoinsight.pages.alarm.Detail.AdvancedFeaturesMonitoringDataBlack',
                          dm: '高级功能/监控数据黑白名单',
                        })}
                        name={[field.name, 'check']}
                      >
                        <Radio.Group
                          disabled={false}
                          options={[
                            {
                              label: '是',
                              value: '1',
                            },
                            {
                              label: '否',
                              value: '0',
                            },
                          ]}
                          // defaultValue={location.search.includes('app=') ? [1] : [0]}
                          onChange={(e) => {
                            form.setFieldValue(
                              [
                                'triggers',
                                fieldAll.name,
                                'datasources',
                                field.name,
                                'filters',
                              ],
                              [{}],
                            );
                            // handleChangeCheckBox(e, field.name, fieldAll?.name);
                          }}
                        />
                      </Form.Item>
                    )}
                    {/* <Checkbox>高级功能/配置报警配置时间</Checkbox> */}
                    <Form.Item
                      shouldUpdate={(pre: any, next: any) => {
                        if (
                          pre?.triggers?.[fieldAll.name]?.['datasources']?.[
                            field.name
                          ]?.check !==
                          next?.triggers?.[fieldAll.name]?.['datasources']?.[
                            field.name
                          ]?.check
                        ) {
                          return true;
                        } else {
                          return false;
                        }
                      }}
                      noStyle
                    >
                      {(form) => {
                        const type = form.getFieldValue([
                          'triggers',
                          fieldAll.name,
                          'datasources',
                          field.name,
                          'check',
                        ]);
                        if (type === '1') {
                          return (
                            <Form.List
                              name={[field.name, 'filters']}
                              initialValue={
                                [
                                    {
                                      // fieldKey: 0,
                                      // isListField: true,
                                      // // key: 0,
                                      // name: 0,
                                      // isEdit: false
                                    },
                                ]
                              }
                            >
                              {(fields, { add, remove }) => {
                                return (
                                  <>
                                    {fields.map((fieldItem: any) => {
                                      return (
                                        <div
                                          className="highFuture-operate"
                                          key={fieldItem.key}
                                        >
                                          <Row className="alarm-condition">
                                            <div>
                                              <span className="condition-text">
                                                {$i18n.get({
                                                  id: 'holoinsight.pages.alarm.Detail.Filtering',
                                                  dm: '过滤',
                                                })}
                                              </span>
                                            </div>
                                            <Col span={3}>
                                              <Form.Item
                                                labelCol={{
                                                  span: 2,
                                                  offset: 1,
                                                }}
                                                name={[fieldItem.name, 'type']}
                                                rules={[
                                                  {
                                                    required: isRequire(
                                                      fieldItem.key,
                                                    ),
                                                    message: $i18n.get({
                                                      id: 'holoinsight.pages.alarm.Detail.Required',
                                                      dm: '必填项',
                                                    }),
                                                  },
                                                ]}
                                              >
                                                <Select
                                                  options={[
                                                    {
                                                      label: '黑名单',
                                                      value: 'not_literal_or',
                                                    },
                                                    {
                                                      label: '白名单',
                                                      value: 'literal_or',
                                                    },
                                                  ]}
                                                  allowClear
                                                ></Select>

                                                {/* <Input /> */}
                                              </Form.Item>
                                            </Col>
                                            <Col span={4}>
                                              <Form.Item
                                                labelCol={{
                                                  span: 2,
                                                  offset: 1,
                                                }}
                                                name={[fieldItem.name, 'key']}
                                                rules={[
                                                  {
                                                    required: isRequire(
                                                      fieldItem.key,
                                                    ),
                                                    message: $i18n.get({
                                                      id: 'holoinsight.pages.alarm.Detail.Required',
                                                      dm: '必填项',
                                                    }),
                                                  },
                                                ]}
                                              >
                                                {/* <Select placeholder="" options={ipDropArr?.[fieldAll.name]?.[field.name] || []} allowClear>
                                                </Select> */}
                                                <AutoComplete
                                                  filterOption={true}
                                                  options={
                                                    ipDropArr?.[
                                                      fieldAll.name
                                                    ]?.[field.name] || []
                                                  }
                                                  allowClear
                                                  defaultValue={
                                                    location.search.includes(
                                                      'app=',
                                                    ) && fieldItem.key === 0
                                                      ? ['app']
                                                      : null
                                                  }
                                                />
                                                {/* <Input /> */}
                                              </Form.Item>
                                            </Col>
                                            <Col
                                              span={6}
                                              className="select-box"
                                            >
                                              <Form.Item
                                                labelCol={{
                                                  span: 2,
                                                  offset: 1,
                                                }}
                                                name={[
                                                  fieldItem.name,
                                                  'whites',
                                                ]}
                                                rules={[
                                                  {
                                                    required: isRequire(
                                                      fieldItem.key,
                                                    ),
                                                    message: $i18n.get({
                                                      id: 'holoinsight.pages.alarm.Detail.Required',
                                                      dm: '必填项',
                                                    }),
                                                  },
                                                ]}
                                              >
                                                <Select
                                                  mode="tags"
                                                  style={{ width: '100%' }}
                                                  tokenSeparators={[',']}
                                                  allowClear
                                                  defaultValue={
                                                    location.search.includes(
                                                      'app=',
                                                    ) && fieldItem.key === 0
                                                      ? [
                                                          searchParams.get(
                                                            'app',
                                                          ),
                                                        ]
                                                      : []
                                                  }
                                                ></Select>
                                                {/* <Input /> */}
                                              </Form.Item>
                                            </Col>
                                            {fieldItem.name ===
                                            fields.length - 1 ? (
                                              <Button
                                                className="add"
                                                type="primary"
                                                onClick={() => {
                                                  add();
                                                }}
                                              >
                                                {$i18n.get({
                                                  id: 'holoinsight.pages.alarm.Detail.Increase',
                                                  dm: '增加',
                                                })}
                                              </Button>
                                            ) : null}
                                            {
                                            (fieldItem.key === 0 ||
                                              fieldItem.key ===
                                                1) ? null : fields.length >
                                              1 ? (
                                              <Button
                                                className="delete"
                                                danger
                                                onClick={() =>
                                                  remove(fieldItem.name)
                                                }
                                              >
                                                {$i18n.get({
                                                  id: 'holoinsight.pages.alarm.Detail.Delete',
                                                  dm: '删除',
                                                })}
                                              </Button>
                                            ) : null}
                                            {/* {  fields.length > 1 && (!isCloudRun && (field.key !== 'appId' ||  field.key!== 'envId')) ? (
                                              <Button
                                                className="delete"
                                                danger
                                                onClick={() => remove(fieldItem.name)}
                                              >
                                                {$i18n.get({
                                                  id: 'holoinsight.pages.alarm.Detail.Delete',
                                                  dm: '删除',
                                                })}
                                              </Button>
                                            ) : null
                                          } */}
                                          </Row>
                                        </div>
                                      );
                                    })}
                                  </>
                                );
                              }}
                            </Form.List>
                          );
                        } else {
                          return null;
                        }
                      }}
                    </Form.Item>
                  </div>
                );
              })}
            </>
          );
        }}
      </Form.List>
      {/* {showSwitch && triggerType === 'rule' && (
        <Switch
          checkedChildren={$i18n.get({
            id: 'holoinsight.pages.alarm.CommonFormList.Expression',
            dm: '表达式',
          })}
          unCheckedChildren={$i18n.get({
            id: 'holoinsight.pages.alarm.CommonFormList.Expression',
            dm: '表达式',
          })}
          style={{ marginBottom: '20px' }}
          checked={showExpression}
          onChange={() => {
            setShowExpression(!showExpression);

          }}
        />
      )} */}
      {triggerType === 'rule' && (
        <>
          <Row>
            <Form.Item
              label="是否补零"
              name={[fieldAll.name, 'zeroFill']}
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
              <Radio.Group>
                <Radio value={true}>是</Radio>
                <Radio value={false}>否</Radio>
              </Radio.Group>
            </Form.Item>
          </Row>
          <Row className="alarm-condition">
            <div>
              <span className="condition-text">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Expression',
                  dm: '表达式',
                })}
              </span>
            </div>

            <Col span={6}>
              <Form.Item
                labelCol={{ span: 5, offset: 1 }}
                name={[fieldAll.name, 'query']}
                rules={[
                  {
                    required: true,
                    message: $i18n.get({
                      id: 'holoinsight.pages.alarm.Detail.Required',
                      dm: '必填项',
                    }),
                  },
                ]}
                initialValue={'a'}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
        </>
      )}
    </>
  );
}
