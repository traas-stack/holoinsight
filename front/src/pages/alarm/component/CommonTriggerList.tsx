import $i18n from '@/i18n';
import { Button, Col, Form, Input, Row, Select } from 'antd';
import React from 'react';
import { WEEK_DATE } from './const'

const { Option } = Select;
const alarmLevel = [
  {
    label: $i18n.get({
      id: 'holoinsight.pages.alarm.Detail.Emergency',
      dm: '紧急',
    }),
    value: '1',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.pages.alarm.Detail.Serious',
      dm: '严重',
    }),
    value: '2',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.pages.alarm.Detail.High',
      dm: '高',
    }),
    value: '3',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.pages.alarm.Detail.Medium',
      dm: '中',
    }),
    value: '4',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.pages.alarm.Detail.Low',
      dm: '低',
    }),
    value: '5',
  },
];

const timeOptions = [
  {
    label:"1分钟",
    value: "MINUTE",
  },
  {
    label:"5分钟",
    value: "FIVE_MINUTE",
  },
  {
    label:"15分钟",
    value: "QUARTER_HOUR",
  },
  {
    label:"半小时",
    value: "HALF_HOUR",
  },
  {
    label:"1小时",
    value: "HOUR",
  },
  {
    label:"1天",
    value: "DAY",
  },
  {
    label:"1周",
    value: "WEEK",
  }
]

const periodOptions = [
  {
    label:"最近N个周期",
    value: "Current",
  },
  {
    label:"最近N个周期同环比差值",
    value: "PeriodValue",
  },
  {
    label:"最近N个周期同环比绝对值",
    value: "PeriodAbs",
  },
  {
    label:"最近N个周期同环比涨跌百分比",
    value: "PeriodRate",
  }
]

export default function CommonFormList(props: any) {
  const {
    form,
    fieldAll = null,
    handleChangeType,
    moreCondition,
    numberCp,
    handleMoreCondition,
  } = props;
  return (
    <div style={{ width: '100%' }}>
      <Row>
        <Col span={6}>
          <Form.Item
            // labelCol={{ span: 4 }}
            name={[fieldAll.name, 'type']}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Required',
                  dm: '必填项',
                }),
              },
            ]}
            label="触发条件"
            initialValue={'Current'}
          >
            <Select
              options={periodOptions}
              allowClear
            >
            </Select>
          </Form.Item>
        </Col>
        <Form.Item
        shouldUpdate = {(pre:any,next:any)=>{
          if(pre?.triggers?.[fieldAll.name]?.type !== next?.triggers?.[fieldAll.name]?.type){
              return true;
          }else{
              return false;
          }
        }}  
          noStyle>
        {
          (form)=>{
            const type = form.getFieldValue(['triggers',fieldAll.name, "type"]);
            if(type !== 'Current'){
              return (
                <Col span={2}>
                <Form.Item
                  labelCol={{ span: 6 }}
                  name={[fieldAll.name, 'periodType']}
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
                    options={timeOptions}
                    allowClear
                  >
                  </Select>
                </Form.Item>
              </Col>
              )
            }else{
              return null
            }

          }
        }
        </Form.Item>
  
        <Col span={1}>
          <Form.Item
            labelCol={{ span: 2, offset: 1 }}
            name={[fieldAll.name, 'stepNum']}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Required',
                  dm: '必填项',
                }),
              },
            ]}
            initialValue={'2'}
          >
            <Input />
          </Form.Item>
        </Col>

        <Col span={2}>
          <Form.Item
            labelCol={{ span: 2, offset: 1 }}
            name={[fieldAll.name, 'aggregator']}
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
            <Select placeholder="avg" allowClear>
              <Option value="avg">avg</Option>

              <Option value="count">count</Option>

              <Option value="min">min</Option>

              <Option value="max">max</Option>

              <Option value="sum">sumby</Option>
            </Select>
          </Form.Item>
        </Col>
        <Col span={3}>
          <Form.Item
            initialValue={'1'}
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Cycle',
              dm: '周期',
            })}
            labelCol={{ offset: 1 }}
            name={[fieldAll.name, 'downsample']}
          >
            <Select
              placeholder={$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.Minute',
                dm: '1分钟',
              })}
              options = {WEEK_DATE}
            >
            </Select>
          </Form.Item>
        </Col>
      </Row>

      <Form.List name={[fieldAll.name, 'compareConfigs']} initialValue={[{}]}>
        {(fields, { add, remove }) => {
          return (
            <>
              {fields.map((field: any, findIndex: number) => {
                return (
                  <div key={field.name}>
                    <Row>
                      <Col span={6}>
                        <Form.Item
                          // labelCol={{ span: 4 }}
                          name={[field.name, 'cmp']}
                          label={`告警条件${findIndex + 1}`}
                          rules={[
                            {
                              required: true,
                              message: $i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Required',
                                dm: '必填项',
                              }),
                            },
                          ]}
                          initialValue={'EQ'}
                        >
                          <Select
                            placeholder={$i18n.get({
                              id: 'holoinsight.pages.alarm.Detail.Equal',
                              dm: '等于',
                            })}
                            onChange={(e) =>
                              handleChangeType(e, fieldAll.name, field.name)
                            }
                          >
                            <Option value="EQ">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Equal',
                                dm: '等于',
                              })}
                            </Option>

                            <Option value="NEQ">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.NotEqual',
                                dm: '不等于',
                              })}
                            </Option>

                            <Option value="GT">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Greater',
                                dm: '大于',
                              })}
                            </Option>

                            <Option value="LT">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Less',
                                dm: '小于',
                              })}
                            </Option>

                            <Option value="LTE">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.LessThanOrEqual',
                                dm: '小于等于',
                              })}
                            </Option>

                            <Option value="GTE">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.GreaterThanOrEqual',
                                dm: '大于等于',
                              })}
                            </Option>

                            <Option value="NULL">
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.NoData',
                                dm: '无数据',
                              })}
                            </Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      {!numberCp?.[fieldAll.name]?.[field.name] ? (
                        <>
                          <Col span={2}>
                            <Form.Item
                              labelCol={{ span: 2, offset: 1 }}
                              name={[field.name, 'cmpValue']}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.pages.alarm.Detail.Required',
                                    dm: '必填项',
                                  }),
                                },
                              ]}
                              initialValue={30}
                            >
                              <Input
                                placeholder={$i18n.get({
                                  id: 'holoinsight.pages.alarm.Detail.NumericalValue',
                                  dm: '数值',
                                })}
                              />
                            </Form.Item>
                          </Col>

                          {!(moreCondition?.[fieldAll.name] || []).includes(
                            field.name,
                          ) ? (
                            <Button
                              onClick={() =>
                                handleMoreCondition(
                                  fieldAll.name,
                                  field.name,
                                  'more',
                                )
                              }
                            >
                              {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.MoreConditions',
                                dm: '更多条件',
                              })}
                            </Button>
                          ) : null}

                          {(moreCondition?.[fieldAll.name] || []).includes(
                            field.name,
                          ) ? (
                            <>
                              <Col span={2}>
                                <Form.Item
                                  labelCol={{ span: 2, offset: 1 }}
                                  name={[field.name, 'cmpMore']}
                                >
                                  <Select
                                    placeholder={$i18n.get({
                                      id: 'holoinsight.pages.alarm.Detail.Equal',
                                      dm: '等于',
                                    })}
                                    allowClear
                                  >
                                    <Option value="EQ">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.Equal',
                                        dm: '等于',
                                      })}
                                    </Option>

                                    <Option value="NEQ">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.NotEqual',
                                        dm: '不等于',
                                      })}
                                    </Option>

                                    <Option value="GT">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.Greater',
                                        dm: '大于',
                                      })}
                                    </Option>

                                    <Option value="LT">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.Less',
                                        dm: '小于',
                                      })}
                                    </Option>

                                    <Option value="LTE">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.LessThanOrEqual',
                                        dm: '小于等于',
                                      })}
                                    </Option>

                                    <Option value="GTE">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.GreaterThanOrEqual',
                                        dm: '大于等于',
                                      })}
                                    </Option>
                                  </Select>
                                </Form.Item>
                              </Col>

                              <Col span={2}>
                                <Form.Item
                                  labelCol={{ span: 2, offset: 1 }}
                                  name={[field.name, 'cmpValueMore']}
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
                                  <Input
                                    placeholder={$i18n.get({
                                      id: 'holoinsight.pages.alarm.Detail.NumericalValue',
                                      dm: '数值',
                                    })}
                                  />
                                </Form.Item>
                              </Col>

                              <Button
                                onClick={() =>
                                  handleMoreCondition(
                                    fieldAll.name,
                                    field.name,
                                    'less',
                                  )
                                }
                              >
                                {$i18n.get({
                                  id: 'holoinsight.pages.alarm.Detail.PackUpMore',
                                  dm: '收起更多',
                                })}
                              </Button>
                            </>
                          ) : null}
                        </>
                      ) : null}
                      <Col span={3}>
                        <Form.Item
                          initialValue={'1'}
                          label={'告警等级'}
                          labelCol={{ offset: 1 }}
                          name={[field.name, 'triggerLevel']}
                        >
                          <Select options={alarmLevel}></Select>
                        </Form.Item>
                      </Col>
                      <Button
                        onClick={() => {
                          add();
                        }}
                      >
                        新增
                      </Button>
                      {findIndex > 0 ? (
                        <Button onClick={() => remove(field.name)}>删除</Button>
                      ) : null}
                    </Row>
                  </div>
                );
              })}
            </>
          );
        }}
      </Form.List>
    </div>
  );
}
