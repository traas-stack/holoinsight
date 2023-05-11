import {
  Button,
  Drawer,
  Empty,
  Form,
  Input,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
} from 'antd';
import * as React from 'react';
import type { FormInstance } from 'antd/lib/form';
import { request, universal } from '@/Magi';
import { flatten, uniqBy } from 'lodash';
import $i18n from '../i18n';
interface State {
  visible: boolean;
  target?: any;
  index: number;
  stackMetrics: any;
  bzmBizs: any;
  monitorOptions: any[];
  newSources: any;
  metricTags: any[];
}

interface Props {
  dashboard: any;
}

export default class VariableForm extends React.PureComponent<Props, State> {
  state: State = {
    visible: false,
    index: -1,
    stackMetrics: {},
    bzmBizs: {},
    monitorOptions: [],
    newSources: {},
    metricTags: [],
  };

  form = React.createRef<FormInstance>();
  constructor(props: Props) {
    super(props);
    const { dashboard } = this.props;
    dashboard.variables.on('configschange', this.rerender);

    this.preHandleDataSetOptions = this.preHandleDataSetOptions.bind(this);
    this.handleCustomDataSource = this.handleCustomDataSource.bind(this);
    this.onMonitorChange = this.onMonitorChange.bind(this);
    this.onCombinedDimChange = this.onCombinedDimChange.bind(this);
  }

  componentDidMount() {
    this.preHandleDataSetOptions();
  }

  componentWillUnmount() {
    const { dashboard } = this.props;
    dashboard.variables.off('configschange', this.rerender);
  }

  componentDidUpdate(prevProps: Props, prevState: State) {
    const { visible: preVisible } = prevState;
    const { visible, monitorOptions, target } = this.state;

    if (preVisible !== visible && visible && this.form.current && target) {
      const form = this.form.current;
      const { type, monitor, combinedDim, dim } = target;

      if (
        type === 'ds-query' &&
        monitor &&
        !new RegExp(/^ds(-([a-z]|[A-Z]|\d){4}){3}/).test(`${monitor}`)
      ) {
        const option = monitorOptions.find((option: any) => {
          if (option.dsId === monitor) {
            if (combinedDim) {
              const combinedDimObj = option.combinedDims?.find(
                (item: any) => item.value === combinedDim,
              );

              if (combinedDimObj) {
                return combinedDimObj.dims?.find(
                  (item: any) => item.value === dim,
                );
              }
            } else {
              return option.dims?.find((item: any) => item.value === dim);
            }
          }
          return false;
        });

        if (option) {
          form.setFieldsValue({
            ...target,
            monitor: option.value,
          });
        }
      }
    }
  }

  rerender = () => {
    this.forceUpdate();
  };

  handleCustomDataSource(targets: any[]) {
    const { newSources } = this.state;
    return targets.map((target) => {
      const { uuid, type, source = {}, groupbyName, outsideDomainId } = target;
      const groupbyType = target.groupbyType || source.groupbyType;
      const dsId = source.groupbyType === 'post' ? source.id : source.dsId;
      const {
        pluginType,
        name,
        tables,
        original = {},
      } = newSources[dsId] || source;

      if (
        (pluginType === 'dataset' && groupbyType === 'pre') ||
        pluginType === 'multiGroupbyMultiValueSecond' ||
        pluginType === 'multiGroupbyMultiValueMinute'
      ) {
        return {
          label: $i18n.get(
            {
              id: 'holoinsight.src.MagiContainer.variables.UuidUuidIdDsidName',
              dm: '【UUID: {uuid}】-【ID: {dsId}】-【名称: {name}】',
            },
            { uuid: uuid, dsId: dsId, name: name },
          ),
          value: `${uuid}=${dsId}`,
          showCombinedDims: true,
          combinedDims: tables.map((table: any) => ({
            label: table.tableName,
            value: table.table,
            dims: table.labels.map((item: any) => ({
              label: `${item.name}(${item.alias})`,
              value: item.name,
            })),
          })),
          type,
          pluginType,
          dsId,
          originalDsId: original.dsId,
          groupbyName,
          groupbyType,
          outsideDomainId,
        };
      }
      if (pluginType === 'dataset' && groupbyType === 'post') {
        return {
          label: $i18n.get(
            {
              id: 'holoinsight.src.MagiContainer.variables.UuidUuidIdDsidName',
              dm: '【UUID: {uuid}】-【ID: {dsId}】-【名称: {name}】',
            },
            { uuid: uuid, dsId: dsId, name: name },
          ),
          value: `${uuid}=${dsId}`,
          showCombinedDims: false,
          dims: original?.tables?.labels?.map((item: any) => ({
            label: `${item.name}(${item.alias})`,
            value: item.name,
          })),
          type,
          pluginType,
          dsId,
          originalDsId: original.dsId,
          groupbyName,
          groupbyType,
          outsideDomainId,
        };
      }

      return {
        label: $i18n.get(
          {
            id: 'holoinsight.src.MagiContainer.variables.UuidUuidIdDsidName',
            dm: '【UUID: {uuid}】-【ID: {dsId}】-【名称: {name}】',
          },
          { uuid: uuid, dsId: dsId, name: name },
        ),
        value: `${uuid}=${dsId}`,
        showCombinedDims: false,
        dims: tables?.labels?.map((item: any) => ({
          label: `${item.name}(${item.alias})`,
          value: item.name,
        })),
        type,
        pluginType,
        dsId,
        originalDsId: original.dsId || dsId,
        groupbyName,
        groupbyType: groupbyType || source.groupbyType,
        outsideDomainId,
      };
    });
  }

  preFetchSource(customDsIds: any[], afterFetch: () => void) {
    const promises = customDsIds.map(async (dsId: any) => {
      try {
        const [[source]] = await universal.read([
          {
            modelName: 'customPlugin',
            op: 'R',
            body: { id: dsId },
          },
        ]);

        return Promise.resolve({ dsId, source });
      } catch (error) {
        return Promise.resolve({ dsId });
      }
    });

    Promise.all(promises).then((results: any) => {
      const obj = {};

      results.forEach(({ source }: { dsId: any; source: any }) => {
        if (source) {
        }
      });

      this.setState({ newSources: obj }, () => {
        afterFetch();
      });
    });
  }

  async preHandleDataSetOptions() {
    const { dashboard } = this.props;
    if (!dashboard) {
      return;
    }
    const panels: any[] = dashboard.model.panels;
    const { stackMetrics, bzmBizs } = this.state;
    const targets = flatten(panels.map((panel) => panel.targets)).filter(
      (target) => !!target,
    );

    const customTargets = uniqBy(
      targets.filter((target: any) => target.type === 'CUSTOM'),
      'source.dsId',
    );

    const otherTargets = targets.filter(
      (target: any) => target.type !== 'CUSTOM',
    );

    this.preFetchSource(
      customTargets.map((item) =>
        item.source.groupbyType === 'post' ? item.source.id : item.source.dsId,
      ),

      () => {
        this.setState(
          {
            monitorOptions: this.handleCustomDataSource(customTargets),
          },
          () => {
            otherTargets.forEach(async (target: any = {}) => {
              const {
                uuid,
                type,
                source = {},
                stack,
                metric,
                bizId,
                nodeId,
              } = target;
              let combinedDims: any[] = [];

              if (type === 'STACK' || type === 'SOFA_STACK') {
                let metricObj;
                const realStack = type === 'STACK' ? stack : 1;

                if (!stackMetrics[realStack]) {
                  const stackMetricResult = await request(
                    '/webapi/universal/read',
                    {
                      method: 'POST',
                      data: {
                        cmds: [
                          {
                            modelName: 'stackMetric',
                            op: 'R',
                            body: { stackId: realStack },
                            parseLongText: true,
                          },
                        ],
                      },
                    },
                  );

                  this.setState({
                    stackMetrics: {
                      ...stackMetrics,
                      [realStack]: stackMetricResult?.data[0],
                    },
                  });
                  metricObj = stackMetricResult?.data[0].find(
                    (res: any) => res.id === metric,
                  );
                } else {
                  metricObj = stackMetrics[realStack].find(
                    (res: any) => res.id === metric,
                  );
                }

                if (metricObj) {
                  const stackMetricGroupbyResult = await request(
                    '/webapi/universal/read',
                    {
                      method: 'POST',
                      data: {
                        cmds: [
                          {
                            modelName: 'stackMetricGroupby',
                            op: 'R',
                            body: { stackMetricId: { eq: metricObj.id } },
                            parseLongText: true,
                          },
                        ],
                      },
                    },
                  );

                  if (
                    stackMetricGroupbyResult.data &&
                    stackMetricGroupbyResult.data.length > 0
                  ) {
                    combinedDims = stackMetricGroupbyResult.data[0].map(
                      (data: any) => ({
                        label: `${data.displayName}(${data.name})`,
                        value: data.name,
                        dims: data.colConfig?.dims?.map((dim: any) => ({
                          label: `${dim.displayName}(${dim.name})`,
                          value: dim.name,
                        })),
                      }),
                    );
                  }

                  this.setState({
                    monitorOptions: this.state.monitorOptions.concat([
                      {
                        label: $i18n.get(
                          {
                            id: 'holoinsight.src.MagiContainer.variables.UuidUuidIdSourcedsidName',
                            dm: '【UUID: {uuid}】-【ID: {sourceDsId}】-【名称: {metricObjDisplayName}({metricObjName})】',
                          },
                          {
                            uuid: uuid,
                            sourceDsId: source.dsId,
                            metricObjDisplayName: metricObj.displayName,
                            metricObjName: metricObj.name,
                          },
                        ),
                        value: `${uuid}=${source.dsId}`,
                        showCombinedDims: true,
                        combinedDims,
                        type,
                        dsId: source.dsId,
                      },
                    ]),
                  });
                }
              } else if (type === 'BIZ') {
                let bizObj;

                if (!bzmBizs[bizId]) {
                  const bzmBizResult = await request('/webapi/universal/read', {
                    method: 'POST',
                    data: {
                      cmds: [
                        {
                          modelName: 'bzmBiz',
                          op: 'R',
                          body: { treeNodeId: { eq: bizId } },
                          fields: ['id', 'name', 'bizName', 'bizCode'],
                          parseLongText: true,
                        },
                      ],
                    },
                  });

                  this.setState({
                    bzmBizs: {
                      ...bzmBizs,
                      [bizId]: bzmBizResult?.data[0],
                    },
                  });

                  bizObj = bzmBizResult.data[0].find(
                    (res: any) => res.id === nodeId,
                  );
                } else {
                  bizObj = bzmBizs[bizId].find((res: any) => res.id === nodeId);
                }

                if (bizObj) {
                  const subBzmBizResult = await request(
                    '/webapi/universal/read',
                    {
                      method: 'POST',
                      data: {
                        cmds: [
                          {
                            modelName: 'bzmBiz',
                            op: 'R',
                            body: { id: nodeId },
                            fields: ['groupbyConf'],
                            parseLongText: true,
                          },
                        ],
                      },
                    },
                  );

                  if (
                    subBzmBizResult &&
                    subBzmBizResult.data &&
                    subBzmBizResult.data.length > 0
                  ) {
                    combinedDims =
                      subBzmBizResult.data[0][0]?.groupbyConf?.groupbiesBak?.map(
                        (item: any) => ({
                          label: item.displayName,
                          value: item.name,
                          dims: item.groupbies?.map((dim: any) => ({
                            label: `${dim.name}(${dim.name})`,
                            value: dim.name,
                          })),
                        }),
                      );
                  }
                }

                this.setState({
                  monitorOptions: this.state.monitorOptions.concat([
                    {
                      label: $i18n.get(
                        {
                          id: 'holoinsight.src.MagiContainer.variables.UuidUuidIdSourcedsidName.1',
                          dm: '【UUID: {uuid}】-【ID: {sourceDsId}】-【名称: {bizObjName}】',
                        },
                        {
                          uuid: uuid,
                          sourceDsId: source.dsId,
                          bizObjName: bizObj.name,
                        },
                      ),
                      value: `${uuid}=${source.dsId}`,
                      showCombinedDims: true,
                      combinedDims,
                      type,
                      dsId: source.dsId,
                    },
                  ]),
                });
              }
            });
          },
        );
      },
    );
  }

  onEdit(row: any, index: number) {
    this.setState(
      {
        visible: true,
        index,
        target: row,
      },
      () => {
        this.preHandleDataSetOptions();
      },
    );
  }

  onNew() {
    this.setState(
      {
        visible: true,
        index: -1,
        target: {
          type: 'select',
          options: {
            multiple: true,
          },
        },
      },
      () => {
        this.preHandleDataSetOptions();
      },
    );

    const form = this.form.current;
    if (form) {
      form.setFieldsValue({ type: 'select' });
    }
  }

  onDelete(index: number) {
    const { variables } = this.props.dashboard;
    const { configs } = variables;
    const newConfigs = [...configs];
    newConfigs.splice(index, 1);
    variables.updateConfig(newConfigs);
  }

  onSubmit() {
    const form = this.form.current;
    const { index, monitorOptions } = this.state;
    const {
      dashboard: { variables },
    } = this.props;
    const { configs } = variables;
    if (!form) return;
    form
      .validateFields()
      .then((values) => {
        const { type, monitor } = values;

        if (type === 'ds-query') {
          const {
            type: dsType,
            pluginType,
            groupbyType,
            outsideDomainId,
            dsId,
            originalDsId,
          } = monitorOptions.find((option) => option.value === monitor) || {};

          if (dsType === 'CUSTOM') {
            values.dsId = dsId;
          } else if (dsType === 'BIZ') {
            values.dsId = `${values.monitor}@@${values.combinedDim}`;
          } else if (dsType === 'STACK' || dsType === 'SOFA_STACK') {
            const dsIdArr = dsId?.split('@@');
            values.dsId = `${dsIdArr[0]}@@${dsIdArr[1]}@@${values.dim}`;
          }

          values.groupbyType = groupbyType;
          values.originalDsId = originalDsId;
          values.dsType = dsType;
          values.pluginType = pluginType;
          values.outsideDomainId = outsideDomainId;
        }

        const newConfigs = [...configs];
        if (type === 'query' || type === 'radioQuery') {
          values.metricTags = this.state.metricTags;
        }
        if (index === -1) {
          newConfigs.push(values);
        } else {
          newConfigs.splice(index, 1, values);
        }
        variables?.updateConfig(newConfigs);
        this.onClose();
      })
      .catch(() => {});
  }

  onClose() {
    this.setState({ visible: false });
  }

  onTypeChange(value: string) {
    const form = this.form.current;
    if (!form || value === 'select') return;
    if (form && value === 'query') {
      form.setFieldsValue({
        monitor: undefined,
        combinedDim: undefined,
        dim: undefined,
        frequency: 1,
      });
    }

    if (form && value === 'ds-query') {
      form.setFieldsValue({
        frequency: 1,
        stack: undefined,
        pql: undefined,
        labelKey: undefined,
        regexp: undefined,
      });
    }
  }

  onMonitorChange() {
    const form = this.form.current;

    if (form) {
      form.setFieldsValue({
        ...form.getFieldsValue(),
        combinedDim: undefined,
        dim: undefined,
      });
    }
  }

  onCombinedDimChange() {
    const form = this.form.current;

    if (form) {
      form.setFieldsValue({
        ...form.getFieldsValue(),
        dim: undefined,
      });
    }
  }

  onQueryChange() {
    const form = this.form.current;
    if (!form) return;
    form.metrics({
      labelKey: undefined,
    });
  }
  onMetricSearch(value: string) {
  }
  async onMetricSelect(value: string) {
  }
  setMetricTags = (arr: string[]) => {
    this.setState({
      metricTags: arr,
    });
  };
  render() {
    const {
      dashboard: {
        variables: { configs },
      },
    } = this.props;
    const { visible, target, index, monitorOptions, metrics, tags } =
      this.state;
    const names: string[] = configs.map((item) => item.name);
    if (index !== -1) names.splice(index, 1);
    const columns = [
      {
        title: $i18n.get({
          id: 'holoinsight.src.MagiContainer.variables.PlaceholderName',
          dm: '占位符名称',
        }),
        dataIndex: 'name',
      },
      {
        title: $i18n.get({
          id: 'holoinsight.src.MagiContainer.variables.Mode',
          dm: '模式',
        }),
        dataIndex: 'type',
        render(type: string) {
          return {
            select: $i18n.get({
              id: 'holoinsight.src.MagiContainer.variables.TextMode',
              dm: '文本模式',
            }),
            query: $i18n.get({
              id: 'holoinsight.src.MagiContainer.variables.QueryMode',
              dm: '查询模式',
            }),
            radio: 'tab按钮文本模式',
            radioQuery: 'tab按钮查询模式'
          }[type];
        },
      },
      {
        title: $i18n.get({
          id: 'holoinsight.src.MagiContainer.variables.CandidateValue',
          dm: '候选值',
        }),
        dataIndex: 'show',
        width: 200,
        render(_: any, row: any) {
          if (row.type === 'select' || row.type === 'radio' ) return row.options.values;
          if (row.type === 'query' || row.type === 'radioQuery')
            return (
              <span style={{ wordBreak: 'break-all' }}>
              </span>
            );
          return null;
        },
      },
      {
        title: $i18n.get({
          id: 'holoinsight.src.MagiContainer.variables.Operation',
          dm: '操作',
        }),
        dataIndex: 'uuid',
        render: (_: any, row: any, index: number) => {
          return (
            <Space>
              <a onClick={this.onEdit.bind(this, row, index)}>
                {$i18n.get({
                  id: 'holoinsight.src.MagiContainer.variables.Edit',
                  dm: '编辑',
                })}
              </a>
              <Popconfirm
                placement="topRight"
                onConfirm={this.onDelete.bind(this, index)}
                title={$i18n.get({
                  id: 'holoinsight.src.MagiContainer.variables.AreYouSureYouWant',
                  dm: '确认要删除该占位符么',
                })}
              >
                <a>
                  {$i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.Delete',
                    dm: '删除',
                  })}
                </a>
              </Popconfirm>
            </Space>
          );
        },
      },
    ];

    return (
      <>
        {configs && configs.length > 0 ? (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" onClick={this.onNew.bind(this)}>
                {$i18n.get({
                  id: 'holoinsight.src.MagiContainer.variables.CreatePlaceholder',
                  dm: '新建占位符',
                })}
              </Button>
            </div>
            <Table
              pagination={{ hideOnSinglePage: true }}
              columns={columns}
              dataSource={configs}
            />
          </>
        ) : (
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}>
            <Button type="primary" onClick={this.onNew.bind(this)}>
              {$i18n.get({
                id: 'holoinsight.src.MagiContainer.variables.CreatePlaceholder',
                dm: '新建占位符',
              })}
            </Button>
          </Empty>
        )}

        <Drawer
          title={$i18n.get({
            id: 'holoinsight.src.MagiContainer.variables.CreatePlaceholder',
            dm: '新建占位符',
          })}
          open={visible}
          destroyOnClose
          width={720}
          footer={
            <>
              <Space>
                <Button type="primary" onClick={this.onSubmit.bind(this)}>
                  {$i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.Save',
                    dm: '保存',
                  })}
                </Button>
                <Button
                  onClick={() => {
                    this.setState({ visible: false });
                  }}
                >
                  {$i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.Cancel',
                    dm: '取消',
                  })}
                </Button>
              </Space>
            </>
          }
          onClose={this.onClose.bind(this)}
        >
          <Form
            ref={this.form}
            initialValues={target}
            layout="vertical"
          >
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.src.MagiContainer.variables.PlaceholderName',
                dm: '占位符名称',
              })}
              name="name"
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.FillInThePlaceholderName',
                    dm: '请填写占位符名称',
                  }),
                },
                {
                  max: 16,
                  message: $i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.ThePlaceholderCannotExceedCharacters',
                    dm: '占位符长度不能超过16个字符',
                  }),
                },
                {
                  validator(rules, value) {
                    if (names.includes(value))
                      return Promise.reject(
                        $i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.PlaceholdersMustBeUnique',
                          dm: '占位符必须唯一',
                        }),
                      );
                    return Promise.resolve();
                  },
                },
                {
                  validator(rules, value) {
                    if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(value))
                      return Promise.reject(
                        $i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.PlaceholdersCanOnlyUseEnglish',
                          dm: '占位符只能使用英文字符、中文、数字、下划线',
                        }),
                      );

                    return Promise.resolve();
                  },
                },
              ]}
            >
              <Input
                autoComplete="off"
                placeholder={$i18n.get({
                  id: 'holoinsight.src.MagiContainer.variables.EnterAPlaceholderName',
                  dm: '请输入占位符名称',
                })}
              />
            </Form.Item>
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.src.MagiContainer.variables.Mode',
                dm: '模式',
              })}
              name="type"
              rules={[
                {
                  required: true,
                  message: $i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.FillInThePlaceholderName',
                    dm: '请填写占位符名称',
                  }),
                },
              ]}
            >
              <Select onChange={this.onTypeChange.bind(this)}>
                <Select.Option value="select">
                  {$i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.TextMode',
                    dm: '文本模式',
                  })}
                </Select.Option>
                <Select.Option value="query">
                  {$i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.QueryMode',
                    dm: '查询模式',
                  })}
                </Select.Option>
                <Select.Option value="radio">
                   tab按钮文本模式
                </Select.Option>
                <Select.Option value="radioQuery">
                   tab按钮查询模式
                </Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              noStyle
              shouldUpdate={(prev, next) => prev.type !== next.type}
            >
              {({ getFieldValue }) => {
                const type = getFieldValue('type');
                if (type === 'select') {
                  return (
                    <>
                      <Form.Item
                        valuePropName="checked"
                        label={$i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.MultipleOptionsAreSupported',
                          dm: '支持多选',
                        })}
                        name={['options', 'multiple']}
                      >
                        <Switch />
                      </Form.Item>
                      <Form.Item
                        name={['options', 'values']}
                        label={$i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.CandidateValue',
                          dm: '候选值',
                        })}
                        rules={[
                          {
                            required: true,
                            message: $i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.FillInThePlaceholderName',
                              dm: '请填写占位符名称',
                            }),
                          },
                        ]}
                      >
                        <Input.TextArea
                          placeholder={$i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.SeparateThemWithCommasSuch',
                            dm: '请用英文逗号分隔，如 payTM,inspector',
                          })}
                        />
                      </Form.Item>
                    </>
                  );
                }

                if (type === 'ds-query') {
                  return (
                    <>
                      <Form.Item
                        valuePropName="checked"
                        label={$i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.MultipleOptionsAreSupported',
                          dm: '支持多选',
                        })}
                        name="multiple"
                      >
                        <Switch />
                      </Form.Item>
                      <Form.Item
                        name="monitor"
                        label={$i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.MonitoringItems',
                          dm: '监控项',
                        })}
                        rules={[
                          {
                            required: true,
                            message: $i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.SelectAMonitoringItem',
                              dm: '请选择监控项',
                            }),
                          },
                        ]}
                      >
                        <Select
                          options={monitorOptions}
                          onChange={this.onMonitorChange}
                        />
                      </Form.Item>

                      <Form.Item
                        noStyle
                        shouldUpdate={(prev, next) =>
                          prev.monitor !== next.monitor
                        }
                      >
                        {({ getFieldValue }: { getFieldValue: any }) => {
                          const monitor = getFieldValue('monitor');
                          const { showCombinedDims, combinedDims } =
                            monitorOptions.find(
                              (item) => item.value === monitor,
                            ) || {};

                          if (showCombinedDims) {
                            return (
                              <Form.Item
                                name="combinedDim"
                                label={$i18n.get({
                                  id: 'holoinsight.src.MagiContainer.variables.CombinedDimensions',
                                  dm: '组合维度',
                                })}
                                rules={[
                                  {
                                    required: true,
                                    message: $i18n.get({
                                      id: 'holoinsight.src.MagiContainer.variables.SelectACombinedDimension',
                                      dm: '请选择组合维度',
                                    }),
                                  },
                                ]}
                              >
                                <Select
                                  options={combinedDims}
                                  onChange={this.onCombinedDimChange}
                                />
                              </Form.Item>
                            );
                          }

                          return <></>;
                        }}
                      </Form.Item>

                      <Form.Item
                        noStyle
                        shouldUpdate={(prev, next) =>
                          prev.monitor !== next.monitor ||
                          prev.combinedDim !== next.combinedDim
                        }
                      >
                        {({ getFieldValue }: { getFieldValue: any }) => {
                          const monitor = getFieldValue('monitor');
                          const combinedDim = getFieldValue('combinedDim');
                          const {
                            showCombinedDims,
                            combinedDims,
                            dims = [],
                          } = monitorOptions.find(
                            (item) => item.value === monitor,
                          ) || {};

                          let options = [];
                          if (showCombinedDims && combinedDims) {
                            options = combinedDims.find(
                              (item: any) => item.value === combinedDim,
                            )?.dims;
                          } else {
                            options = dims;
                          }

                          return (
                            <Form.Item
                              name="dim"
                              label={$i18n.get({
                                id: 'holoinsight.src.MagiContainer.variables.DimensionName',
                                dm: '维度名称',
                              })}
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.src.MagiContainer.variables.SelectADimensionName',
                                    dm: '请选择维度名称',
                                  }),
                                },
                              ]}
                            >
                              <Select
                                options={options}
                              />
                            </Form.Item>
                          );
                        }}
                      </Form.Item>

                      <Form.Item
                        label={
                          <>
                            {$i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.UpdateFrequency',
                              dm: '更新频率',
                            })}
                          </>
                        }
                        name="frequency"
                        getValueProps={(value) => ({
                          value: value ? `${value}` : value,
                        })}
                        normalize={(value) => Number.parseInt(value, 10)}
                      >
                        <Select>
                          <Select.Option value="1">
                            {$i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.WhenLoadingTheDashboard',
                              dm: '大盘加载时',
                            })}
                          </Select.Option>
                          <Select.Option value="2">
                            {$i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.WhenTheTimeChanges',
                              dm: '时间变更时',
                            })}
                          </Select.Option>
                        </Select>
                      </Form.Item>
                    </>
                  );
                }
                if (type === 'radio'){
                  return (
                    <Form.Item
                    name={['options', 'values']}
                    label={$i18n.get({
                      id: 'holoinsight.src.MagiContainer.variables.CandidateValue',
                      dm: '候选值',
                    })}
                    rules={[
                      {
                        required: true,
                        message: $i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.FillInThePlaceholderName',
                          dm: '请填写占位符名称',
                        }),
                      },
                    ]}
                  >
                    <Input.TextArea
                      placeholder={$i18n.get({
                        id: 'holoinsight.src.MagiContainer.variables.SeparateThemWithCommasSuch',
                        dm: '请用英文逗号分隔，如 payTM,inspector',
                      })}
                    />
                  </Form.Item>
                  )
                }
                return (
                  <>
                    <Form.Item
                      valuePropName="checked"
                      label={$i18n.get({
                        id: 'holoinsight.src.MagiContainer.variables.MultipleOptionsAreSupported',
                        dm: '支持多选',
                      })}
                      name="multiple"
                    >
                      <Switch />
                    </Form.Item>

                    <Form.Item
                      name="stack"
                      label={$i18n.get({
                        id: 'holoinsight.src.MagiContainer.variables.Indicators',
                        dm: '指标',
                      })}
                      rules={[
                        {
                          required: true,
                          message: $i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.SelectAMetric',
                            dm: '请选择指标',
                          }),
                        },
                      ]}
                    >
                      <Select
                        allowClear
                        showSearch
                        placeholder={$i18n.get({
                          id: 'holoinsight.src.MagiContainer.variables.EnterAMetric',
                          dm: '请输入指标',
                        })}
                        onSearch={this.onMetricSearch.bind(this)}
                        onSelect={this.onMetricSelect.bind(this)}
                        options={metrics}
                      />
                    </Form.Item>

                    <Form.Item
                      noStyle
                      shouldUpdate={(prev, next) => {
                        return (
                          prev.stack !== next.stack || prev.pql !== next.pql
                        );
                      }}
                    >
                      {({ getFieldValue }) => {
                        const stack = getFieldValue('stack');
                        return (
                          <>
                            <Form.Item
                              name="labelKey"
                              label={
                                <>
                                  {$i18n.get({
                                    id: 'holoinsight.src.MagiContainer.variables.Dimension',
                                    dm: '维度',
                                  })}
                                </>
                              }
                              rules={[
                                {
                                  required: true,
                                  message: $i18n.get({
                                    id: 'holoinsight.src.MagiContainer.variables.SelectADimension',
                                    dm: '请选择维度',
                                  }),
                                },
                              ]}
                            >
                              <Select
                                allowClear
                                placeholder={$i18n.get({
                                  id: 'holoinsight.src.MagiContainer.variables.SelectADimension',
                                  dm: '请选择维度',
                                })}
                                options={tags}
                              />
                            </Form.Item>
                          </>
                        );
                      }}
                    </Form.Item>
                    <Form.Item
                      label={
                        <>
                          {$i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.RegularExpression',
                            dm: '正则表达式',
                          })}

                        </>
                      }
                      name="regexp"
                    >
                      <Input placeholder="^\d+$" />
                    </Form.Item>
                    <Form.Item
                      label={
                        <>
                          {$i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.UpdateFrequency',
                            dm: '更新频率',
                          })}
                        </>
                      }
                      name="frequency"
                      getValueProps={(value) => ({
                        value: value ? `${value}` : value,
                      })}
                      normalize={(value) => Number.parseInt(value, 10)}
                    >
                      <Select>
                        <Select.Option value="1">
                          {$i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.WhenLoadingTheDashboard',
                            dm: '大盘加载时',
                          })}
                        </Select.Option>
                        <Select.Option value="2">
                          {$i18n.get({
                            id: 'holoinsight.src.MagiContainer.variables.WhenTheTimeChanges',
                            dm: '时间变更时',
                          })}
                        </Select.Option>
                      </Select>
                    </Form.Item>
                    <Form.Item
                      noStyle
                      shouldUpdate={(prev, next) => {
                        return (
                          prev.stack !== next.stack ||
                          prev.regexp !== next.regexp ||
                          prev.labelKey !== next.labelKey
                        );
                      }}
                    >
                      {({ getFieldValue }) => {
                        const stack = getFieldValue('stack');
                        const label = getFieldValue('labelKey');
                        const regexp = getFieldValue('regexp');
                        if (!stack || !label) return null;
                        return (
                          <Form.Item
                            label={$i18n.get({
                              id: 'holoinsight.src.MagiContainer.variables.PreviewMaximumCandidateValuesAre',
                              dm: '预览 (最大显示10条候选值)',
                            })}
                          >
                          </Form.Item>
                        );
                      }}
                    </Form.Item>
                  </>
                );
              }}
            </Form.Item>
            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.src.MagiContainer.variables.Description',
                dm: '描述',
              })}
              name="desc"
              rules={[
                {
                  max: 500,
                  message: $i18n.get({
                    id: 'holoinsight.src.MagiContainer.variables.UpToCharacters',
                    dm: '最多500个字符',
                  }),
                },
              ]}
            >
              <Input.TextArea
                placeholder={$i18n.get({
                  id: 'holoinsight.src.MagiContainer.variables.UpToCharacters',
                  dm: '最多500个字符',
                })}
              />
            </Form.Item>
          </Form>
        </Drawer>
      </>
    );
  }
}
