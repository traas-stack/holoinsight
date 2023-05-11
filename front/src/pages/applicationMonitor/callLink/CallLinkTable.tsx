import {
  getCallLinkList,
  getComponentTraceIds,
} from '@/services/application/api';
import {
  Button,
  Card,
  DatePicker,
  Form,
  Input,
  message,
  Select,
  Space,
  Spin,
  Table,
  Tooltip,
} from 'antd';
import { useEffect, useState } from 'react';
import moment from 'moment';
import {
  CopyOutlined,
  DeleteOutlined,
  LeftOutlined,
  PlusOutlined,
  QuestionCircleOutlined,
  RightOutlined,
} from '@ant-design/icons';
import { history } from 'umi';
import type { TableListItem as TableListSqlItem } from '@/components/App/componentMonitor/index';
import { filter as _filter, find as _find } from 'lodash';
import queryString from 'query-string';
import { useRequest } from 'ahooks';
import type { ColumnsType } from 'antd/lib/table';
import copy from 'copy-to-clipboard';
import $i18n from '../../../i18n';
import CallLinkDetail from './callLinkDetail';
import styles from './index.less';

const { RangePicker } = DatePicker;

interface IProps {
  sqlRecord?: TableListSqlItem | null;
  type?: string;
  app?: string;
  address?: string;
}

type TableListItem = {
  traceIds: string[] | string;
  start: number;
  endpointNames: string[];
  duration: number;
  error: boolean;
};

interface ApiLinkList {
  data: TableListItem[];
  success: boolean;
  total: number;
}

interface OptionType {
  label: string;
  value: string;
  visible: boolean;
}

interface SelectObjType {
  traceIds?: string;
  serviceName?: string;
  minTraceDuration?: string;
  endpointName?: string;
}
type StringKey = Record<string, string>;
type BooleanKey = Record<string, boolean>;
type QueryOrderType = 'BY_START_TIME' | 'BY_DURATION';

type ParamsType = {
  queryOrder?: 'BY_START_TIME' | 'BY_DURATION';
  service?: string;
};

const { Option } = Select;

const mergeObj = (queryArr: SelectObjType[]) => {
  const arr = queryArr;
  let obj = {};
  arr.forEach((x) => {
    obj = { ...obj, ...x };
  });
  return obj;
};

export default (props: IProps) => {
  const { sqlRecord, type, app, address } = props;
  const [form] = Form.useForm();
  const [copyOpenList, setCopyOpenList] = useState<BooleanKey>({});
  const [sortType, setSortType] = useState<QueryOrderType>('BY_DURATION');
  const [selectOptions, setSelectOptions] = useState<OptionType[]>([
    {
      label: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.InterfaceName',
        dm: '接口名称',
      }),
      value: 'endpointName',
      visible: false,
    },
    {
      label: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.ApplicationName',
        dm: '应用名称',
      }),
      value: 'serviceName',
      visible: false,
    },
    { label: 'TraceID', value: 'traceIds', visible: false },
    {
      label: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.TimeConsumingIsGreater',
        dm: '耗时大于',
      }),
      value: 'minTraceDuration',
      visible: false,
    },
  ]);

  const [isComponentMonitor, setIsComponentMonitor] = useState<boolean>(false);
  const [traceId] = useState<string>('');
  const [current, setCurrent] = useState<number>(1);
  const [activeNum, setActiveNum] = useState<number>(1);
  const [isAll, setIsAll] = useState<boolean>(false);
  const [isFirsh, setIsFirsh] = useState<boolean>(true);
  const [navLoading, setNavLoading] = useState<boolean>(false);
  const [linkList, setLinkList] = useState<ApiLinkList>({
    data: [],
    success: true,
    total: 0,
  });
  const queryNext = async (data: any) => {
    const values = form.getFieldsValue(true);
    const {
      traceState,
      time: { date },
      condition,
      condition1,
    } = values;
    const start = moment(date?.[0]).valueOf();
    const end = moment(date?.[1]).valueOf();
    const queryData: SelectObjType[] = [];
    condition1?.forEach((item: { first: string; last: string }) => {
      const obj: StringKey = {};
      const key = item.first;
      obj[key] = item.last;
      queryData.push(obj);
    });
    const cQueryData = mergeObj(queryData);
    const params: API.ApiCallLink = {
      traceState: traceState,
      paging: {
        pageNum: current + 1,
        pageSize: 10,
      },
      duration: { start, end },
      queryOrder: data?.queryOrder || sortType,
      [condition?.province]: condition?.provinceValue,
      ...cQueryData,
    };

    if (params.serviceName || data?.service) {
      params.serviceName = data?.service || params.serviceName;
    }
    const resNext: any = await getCallLinkList(params);
    if (Array.isArray(resNext?.traces) && resNext?.traces.length > 0) {
      setIsAll(false);
    } else {
      setIsAll(true);
    }
  };
  const getLinkList = useRequest(
    (data: ParamsType) => {
      setNavLoading(true);
      const values = form.getFieldsValue(true);
      const {
        traceState,
        time: { date },
        condition,
        condition1,
      } = values;
      const start = moment(date?.[0]).valueOf();
      const end = moment(date?.[1]).valueOf();
      const queryData: SelectObjType[] = [];
      condition1?.forEach((item: { first: string; last: string }) => {
        const obj: StringKey = {};
        const key = item.first;
        obj[key] = item.last;
        queryData.push(obj);
      });
      const cQueryData = mergeObj(queryData);
      const params: API.ApiCallLink = {
        traceState: traceState,
        paging: {
          pageNum: current,
          pageSize: 10,
        },
        duration: { start, end },
        queryOrder: data?.queryOrder || sortType,
        [condition?.province]: condition?.provinceValue,
        ...cQueryData,
      };

      if (params.serviceName || data?.service) {
        params.serviceName = data?.service || params.serviceName;
      }
      return getCallLinkList(params);
    },
    {
      refreshDeps: [current],
      manual: true,
      onSuccess: async (res: any) => {
        const obj: BooleanKey = {};
        (res?.traces || []).forEach((item: { traceIds: (string | number)[] }) => {
          obj[item.traceIds[0]] = false;
        });
        await queryNext({ queryOrder: sortType, service: app });
        setNavLoading(false);
        setCopyOpenList(obj);
        setLinkList({
          data: res?.traces || [],
          success: true,
          total: res?.traces?.length,
        });
      },
      onError: () => {
        setNavLoading(false);
      },
    },
  );
  async function queryComponentTraceIds(address: string) {
    const date = form.getFieldValue('time')?.date;
    const serviceName = form.getFieldValue('serviceName');
    const condition1 = form.getFieldValue('condition1');
    const cServiceName =
      condition1 && _find(condition1, (o) => o.first === 'serviceName')?.last;
    const sourceName = condition1
      ? cServiceName
      : serviceName || sqlRecord?.sourceName;
    const start = moment(date?.[0]).valueOf();
    const end = moment(date?.[1]).valueOf();
    getComponentTraceIds({
      address: address,
      serviceName: app,
      start,
      end,
    }).then((res) => {
      const list: OptionType[] = JSON.parse(JSON.stringify(selectOptions));
      list.forEach((i) => {
        if (i.value === 'traceIds') {
          i.visible = true;
        }
      });
      setSelectOptions(list);
      form.setFieldsValue({
        condition: {
          province: 'traceIds',
          provinceValue: res,
        },
      });
      if (sourceName) {
        const list: OptionType[] = JSON.parse(JSON.stringify(selectOptions));
        list.forEach((i) => {
          if (i.value === 'serviceName') {
            i.visible = true;
          }
        });
        setSelectOptions(list);
        form.setFieldsValue({
          condition1: [
            {
              first: 'serviceName',
              last: sourceName,
            },
          ],
        });
      }
      getLinkList.run({ queryOrder: sortType, service: app });
    });
  }



  useEffect(() => {
    if (address || sqlRecord) {
      address && queryComponentTraceIds(address);
    } else {
      const list: OptionType[] = JSON.parse(JSON.stringify(selectOptions));
      list.forEach((i) => {
        if (i.value === 'serviceName') {
          i.visible = true;
        }
      });
      setSelectOptions(list);
      getLinkList.run({
        queryOrder: sortType as QueryOrderType,
        service: app,
      });
      if (app) { 
        form.setFieldsValue({
          condition: {
            province: 'serviceName',
            provinceValue: app,
          },
        });
      }
    }
  }, [address, sqlRecord, app]);
  useEffect(() => {
    if (isFirsh) {
      setIsFirsh(false);
    } else {
      getLinkList.run({ queryOrder: sortType, service: app });
    }
  }, [current]);

  const columns: ColumnsType<TableListItem> = [
    {
      title: 'TraceID',
      dataIndex: 'traceIds',
      key: 'traceIds',
      width: 250,
      fixed: 'left',
      ellipsis: true,
      render: (text, record) => {
        return (
          <span>
            <Tooltip
              title={text}
              onOpenChange={(open) => {
                const cCpyOpenList = JSON.parse(JSON.stringify(copyOpenList));
                cCpyOpenList[text] = open;
                setCopyOpenList(cCpyOpenList);
              }}
            >
              <span style={{ display: 'flex', alignItems: 'center' }}>
                <span
                  style={{
                    display: 'inline-block',
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    textOverflow: 'ellipsis',
                    wordBreak: 'keep-all',
                    width: 200,
                    color: record?.error ? '#d4380d' : '#1890ff',
                    cursor: 'pointer',
                  }}
                  onClick={() => {
                    history.push({
                      pathname: '/apm/detailLinkDetail',
                      search: queryString.stringify({
                        traceIds: text,
                      }),
                    });
                  }}
                >
                  {text[0]}
                </span>
                {copyOpenList[text] && (
                  <CopyOutlined
                    style={{ cursor: 'pointer', color: '#1890ff' }}
                    onClick={() => {
                      copy(text);
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.admin.apiKey.CopySuccessfully',
                          dm: '复制成功！',
                        }),
                      );
                    }}
                  />
                )}
              </span>
            </Tooltip>
          </span>
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.GenerationTime',
        dm: '产生时间',
      }),
      dataIndex: 'start',
      key: 'start',
      width: 150,
      sorter: (a, b) => a.start - b.start,
      sortDirections: ['descend', 'ascend'],
      render: (text) => {
        return <span>{moment(text).format('YYYY-MM-DD HH:mm:ss')}</span>;
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.InterfaceName',
        dm: '接口名称',
      }),
      dataIndex: 'endpointNames',
      key: 'endpointNames',
      width: 100,
      render: (_, record) => {
        return (
          <Tooltip title={record?.endpointNames[0]}>
            <div
              style={{
                overflow: 'hidden',
                whiteSpace: 'nowrap',
                textOverflow: 'ellipsis',
                width: 80,
              }}
            >
              {record?.endpointNames[0]}
            </div>
          </Tooltip>
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.ServiceName',
        dm: '服务名称',
      }),
      dataIndex: 'serviceNames',
      key: 'serviceNames',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.ServiceInstance',
        dm: '服务实例',
      }),
      dataIndex: 'serviceInstanceNames',
      key: 'serviceInstanceNames',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.TimeConsuming',
        dm: '耗时',
      }),
      dataIndex: 'duration',
      key: 'duration',
      sorter: (a, b) => a.duration - b.duration,
      render: (text) => {
        return <span>{text}ms</span>;
      },
    },
  ];

  const onTimeChange = (v: string) => {
    switch (v) {
      case '5':
        form.setFieldsValue({
          time: { date: [moment().subtract(5, 'minutes'), moment()] },
        });
        break;
      case '10':
        form.setFieldsValue({
          time: { date: [moment().subtract(10, 'minutes'), moment()] },
        });
        break;
      case '60':
        form.setFieldsValue({
          time: { date: [moment().subtract(60, 'minutes'), moment()] },
        });
        break;
      case '360':
        form.setFieldsValue({
          time: { date: [moment().subtract(360, 'minutes'), moment()] },
        });
        break;

      default:
        break;
    }
  };
  const onFinish = () => {
    setCurrent(1);
    if (address) {
      queryComponentTraceIds(address);
    } else {
      getLinkList.run({ queryOrder: sortType });
    }
  };

  return (
    <div className={styles.callLinkTable}>
      <div
        style={{
          display: isComponentMonitor ? 'none' : 'block',
        }}
      >
        <Card
          style={{
            marginBottom: 20,
          }}
        >
          <Form
            name="dynamic_form_nest_item"
            form={form}
            onFinish={onFinish}
            autoComplete="off"
          >
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Time',
                dm: '时间',
              })}
            >
              <Input.Group compact>
                <Form.Item name={['time', 'day']} initialValue="custom" noStyle>
                  <Select
                    placeholder={$i18n.get({
                      id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.RecentTime',
                      dm: '最近时间',
                    })}
                    style={{ width: 100 }}
                    onChange={onTimeChange}
                  >
                    <Option value="custom">
                      {$i18n.get({
                        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Custom',
                        dm: '自定义',
                      })}
                    </Option>
                    <Option value="5">5min</Option>
                    <Option value="10">10min</Option>
                    <Option value="60">1h</Option>
                    <Option value="360">6h</Option>
                  </Select>
                </Form.Item>
                <Form.Item
                  name={['time', 'date']}
                  initialValue={[moment().subtract(10, 'minutes'), moment()]}
                  noStyle
                >
                  <RangePicker
                    onChange={() =>
                      form.setFieldsValue({
                        time: { day: 'custom' },
                      })
                    }
                    showTime={{
                      hideDisabledOptions: true,
                    }}
                  />
                </Form.Item>
              </Input.Group>
            </Form.Item>
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Status',
                dm: '状态',
              })}
              name="traceState"
              initialValue="ALL"
            >
              <Select
                placeholder={$i18n.get({
                  id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Status',
                  dm: '状态',
                })}
                style={{ width: 350 }}
              >
                <Option value="ALL">ALL</Option>
                <Option value="SUCCESS">SUCCESS</Option>
                <Option value="ERROR">ERROR</Option>
              </Select>
            </Form.Item>
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Object',
                dm: '对象',
              })}
            >
              <Input.Group compact>
                <Form.Item name={['condition', 'province']} noStyle>
                  <Select
                    placeholder={$i18n.get({
                      id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Condition',
                      dm: '条件',
                    })}
                    style={{ width: 100 }}
                    onChange={(v) => {
                      const condition1 = form.getFieldValue('condition1');
                      const condition2 =
                        condition1 &&
                        _filter(JSON.parse(JSON.stringify(condition1)), (o) => {
                          if (o) return o;
                        });
                      const list: OptionType[] = JSON.parse(
                        JSON.stringify(selectOptions),
                      );

                      list.forEach((i) => {
                        if (i.value === v) {
                          i.visible = true;
                        } else {
                          const item = _find(
                            condition2,
                            (o) => o.first === i.value,
                          );

                          if (item) {
                            i.visible = true;
                          } else {
                            i.visible = false;
                          }
                        }
                      });
                      setSelectOptions(list);
                    }}
                  >
                    {selectOptions.map((item) => {
                      return (
                        <Option
                          value={item.value}
                          key={item.value}
                          disabled={item.visible}
                        >
                          {item.label}
                        </Option>
                      );
                    })}
                  </Select>
                </Form.Item>
                <Form.Item name={['condition', 'provinceValue']} noStyle>
                  {form.getFieldValue('condition')?.province === 'traceIds' ? (
                    <Select
                      mode="tags"
                      style={{ width: 400 }}
                      placeholder={$i18n.get({
                        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.EnterAKeyword',
                        dm: '请输入关键词',
                      })}
                      maxTagCount={1}
                      maxTagTextLength={300}
                      allowClear
                    />
                  ) : (
                    <Input
                      style={{ width: 250 }}
                      allowClear
                      placeholder={$i18n.get({
                        id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.EnterAKeyword',
                        dm: '请输入关键词',
                      })}
                    />
                  )}
                </Form.Item>
              </Input.Group>
            </Form.Item>

            <Form.List name="condition1">
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }) => {
                    return (
                      <Space
                        key={key}
                        style={{
                          display: 'flex',
                          marginBottom: 24,
                          marginLeft: 42,
                          alignItems: 'center',
                        }}
                        align="baseline"
                      >
                        <Input.Group compact>
                          <Form.Item
                            {...restField}
                            name={[name, 'first']}
                            noStyle
                          >
                            <Select
                              placeholder={$i18n.get({
                                id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Condition',
                                dm: '条件',
                              })}
                              style={{ width: 100 }}
                              onChange={(v) => {
                                const condition =
                                  form.getFieldValue('condition');
                                const condition1 =
                                  form.getFieldValue('condition1');
                                const condition2 = _filter(
                                  JSON.parse(JSON.stringify(condition1)),
                                  (o) => {
                                    if (o) return o;
                                  },
                                );

                                const list: OptionType[] = JSON.parse(
                                  JSON.stringify(selectOptions),
                                );

                                list.forEach((i) => {
                                  if (i.value === v) {
                                    i.visible = true;
                                  } else {
                                    if (
                                      i.value !== condition?.province &&
                                      !_find(
                                        condition2,
                                        (o) => o.first === i.value,
                                      )
                                    ) {
                                      i.visible = false;
                                    }
                                  }
                                });
                                setSelectOptions(list);
                              }}
                            >
                              {selectOptions.map((item) => {
                                return (
                                  <Option
                                    value={item.value}
                                    key={item.value}
                                    disabled={item.visible}
                                  >
                                    {item.label}
                                  </Option>
                                );
                              })}
                            </Select>
                          </Form.Item>
                          <Form.Item
                            {...restField}
                            name={[name, 'last']}
                            noStyle
                          >
                            {form.getFieldValue('condition1')?.[name]?.first ===
                              'traceIds' ? (
                              <Select
                                mode="tags"
                                style={{ width: 400 }}
                                placeholder={$i18n.get({
                                  id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.EnterAKeyword',
                                  dm: '请输入关键词',
                                })}
                                maxTagCount={1}
                                maxTagTextLength={300}
                                allowClear
                              />
                            ) : (
                              <Input
                                style={{ width: 250 }}
                                allowClear
                                placeholder={$i18n.get({
                                  id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.EnterAKeyword',
                                  dm: '请输入关键词',
                                })}
                              />
                            )}
                          </Form.Item>
                        </Input.Group>
                        <DeleteOutlined
                          onClick={() => {
                            const condition1 = form.getFieldValue('condition1');
                            if (condition1 && condition1.length > 0) {
                              const deleteItem = condition1[name];
                              const list: OptionType[] = JSON.parse(
                                JSON.stringify(selectOptions),
                              );

                              list.forEach((i) => {
                                if (i.value === deleteItem?.first) {
                                  i.visible = false;
                                }
                              });
                              setSelectOptions(list);
                            }
                            remove(name);
                          }}
                        />
                      </Space>
                    );
                  })}
                  <Form.Item
                    style={{ display: 'flex', marginBottom: 8, marginLeft: 42 }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <Button
                        type="link"
                        onClick={() => add()}
                        disabled={fields.length > 2}
                        block
                        icon={<PlusOutlined />}
                      >
                        {$i18n.get({
                          id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.FilterConditions',
                          dm: '过滤条件',
                        })}
                      </Button>
                      <Tooltip
                        title={$i18n.get({
                          id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.TheRelationshipBetweenMultipleConditions',
                          dm: '多个条件之间为且的关系',
                        })}
                      >
                        <QuestionCircleOutlined />
                      </Tooltip>
                    </div>
                  </Form.Item>
                </>
              )}
            </Form.List>
            <Form.Item style={{ marginLeft: 42 }}>
              <Space>
                <Button type="primary" htmlType="submit">
                  {$i18n.get({
                    id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Query',
                    dm: '查询',
                  })}
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
        <Card>
          <div style={{ float: 'right', marginBottom: 10 }}>
            {$i18n.get({
              id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Sort',
              dm: '排序:',
            })}

            <Select
              placeholder={$i18n.get({
                id: 'holoinsight.applicationMonitor.callLink.CallLinkTable.Sort.1',
                dm: '排序',
              })}
              defaultValue={'BY_DURATION'}
              onChange={(v: QueryOrderType) => {
                setCurrent(1);
                setSortType(v);
                getLinkList.run({ queryOrder: v });
              }}
              style={{ width: 200, marginLeft: 10 }}
            >
              <Option value="BY_START_TIME">Start Time</Option>
              <Option value="BY_DURATION">Duration</Option>
            </Select>
          </div>
          <Table
            columns={columns}
            scroll={{ x: 1500 }}
            dataSource={linkList.data}
            loading={getLinkList.loading}
            pagination={false}
            rowKey="key"
          />

          <ul className={styles.linkPagination}>
            <Spin spinning={navLoading}>
              <div>
                <li className={styles.linkPaginationBtn}>
                  <Button
                    onClick={() => setCurrent(current - 1)}
                    disabled={current === 1}
                    icon={<LeftOutlined />}
                  ></Button>
                </li>
                <li
                  className={
                    activeNum === 1
                      ? `${styles.linkPaginationText} ${styles.linkPaginationTextActive}`
                      : `${styles.linkPaginationText}`
                  }
                  onClick={() => {
                    setCurrent(current);
                    setActiveNum(1);
                  }}
                >
                  {current}
                </li>
                {!isAll && (
                  <li
                    className={
                      activeNum === 2
                        ? `${styles.linkPaginationText} ${styles.linkPaginationTextActive}`
                        : `${styles.linkPaginationText}`
                    }
                    onClick={() => {
                      setCurrent(current + 1);
                      setActiveNum(1);
                    }}
                  >
                    {current + 1}
                  </li>
                )}

                <li className={styles.linkPaginationBtn}>
                  <Button
                    onClick={() => setCurrent(current + 1)}
                    disabled={isAll}
                    icon={<RightOutlined />}
                  ></Button>
                </li>
              </div>
            </Spin>
          </ul>
        </Card>
      </div>
      {isComponentMonitor && traceId && (
        <CallLinkDetail
          setIsComponentMonitor={setIsComponentMonitor}
          type={type}
          traceId={traceId}
        />
      )}
    </div>
  );
};
