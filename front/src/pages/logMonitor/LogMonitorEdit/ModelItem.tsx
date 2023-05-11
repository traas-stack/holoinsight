import {
  Alert,
  Button,
  Divider,
  Form,
  Input,
  message,
  Modal,
  Radio,
  Select,
  Space,
} from 'antd';
import  { useEffect, useState } from 'react';
import $i18n from '../../../i18n';

const { Option } = Select;

const forms = [
  // 'yyyy-MM-dd HH:mm:ss.SSS',
  'yyyy-MM-dd HH:mm:ss',
  'yyyy-MM-ddTHH:mm:ss',
  // 'yyyy-MM-dd HH:mm',
];

const zones = ['GMT+8', 'UTC', 'Asia/Shanghai', 'America/Los_Angeles'];

const transFuncs: any = {
  '@append': 'append',
  '@mapping': 'mapping',
  '@regexp': 'regexp',
  '@contains': 'contains',
  '@const': 'const',
};

interface IProps {
  setOpen: (p: boolean) => void;
  open: boolean;
  form: any;
  edit: boolean;
  columnData: any[];
  updateTable: (p: any) => void;
  index: number;
  name?: string;
  splitType: string;
  LR_Params?: any;
}

const ModelComponent = (props: IProps) => {
  const {
    open,
    setOpen,
    name,
    form,
    edit,
    columnData,
    updateTable,
    index,
    splitType,
    LR_Params,
  } = props;

  const [canClick, setCanClick] = useState(false);
  const [oldContent, setOldContent] = useState<any>();
  const [selectObj, setSelectObj] = useState<any>({});

  const handleOk = async () => {
    // const validates = [
    //   'nameModel',
    //   'dimType',
    //   'defaultValue',
    //   'transforms',
    //   'nullAble',
    //   'forms',
    //   'zones',
    // ];
    form.validateFields().then((res: any) => {
      let colsSplitList: any;
      switch (splitType) {
        case 'SEP':
          colsSplitList = form.getFieldValue('colsSplit') || [];
          break;
        case 'LR':
          colsSplitList = form.getFieldValue('colsSplit') || [];
          break;
        case 'REGEXP':
          colsSplitList = form.getFieldValue('colsSplit') || [];
          break;
        default:
          colsSplitList = form.getFieldValue('colsGroovy');
      }

      let dimName = '';
      const list = JSON.parse(JSON.stringify(columnData));
      const {
        forms,
        zones,
        nameModel,
        dimType,
        nullAble,
        defaultValue,
        transforms,
      } = res;
      const isTimeType = dimType === 'TIME';
      // 根据name判断 edit 和 add
      if (name) {
        dimName = isTimeType
          ? `时间(${forms}##${zones === 'GMT+8' ? '默认' : zones})`
          : nameModel;
        const otherList = colsSplitList.filter((it: any) => it.name !== name);
        const flag = otherList.some((it: any) => it.name === dimName);
        if (flag) {
          message.warning(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.TheColumnNameAlreadyExists',
              dm: '列名已经存在',
            }),
          );

          return;
        }
        if (isTimeType) {
          const flagTime = otherList.some((it: any) => it.dimType === 'TIME');
          if (flagTime) {
            message.warning(
              $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.OnlyOneTimeColumnCan',
                dm: '时间列仅能配置一个',
              }),
            );

            return;
          }
        }
        const i = list[index].names.findIndex(
          (item: any) => item.name === name,
        );
        list[index].names.splice(i, 1, { name: dimName, nullable: nullAble }); // 修改names名称
        updateTable(list); // 更新 展示列数据
        // 更新form

        const obj = colsSplitList.filter((item: any) => item.name === name)[0];
        obj.dimType = dimType;
        obj.name = dimName;
        obj.nullable = nullAble;
        obj.calConfig = {
          transforms: transforms,
          defaultValue,
        };

        const { nullable: oldNullAble, dimType: oldDimType } = oldContent;
        // 列类型 维度配置修改时清除code
        if (oldNullAble !== nullAble || oldDimType !== dimType) {
          delete obj.code;
        }
        if (res.dimType === 'VALUE') {
          obj.dataType = 'DOUBLE';
        } else if (res.dimType === 'TIME') {
          obj.extInfo = {
            zone: zones,
            format: forms,
          };

          obj.code = '_time';
          obj.dataType = 'DATE';
        } else {
          obj.dataType = 'STRING';
        }
      } else {
        if (isTimeType) {
          const flag = colsSplitList.some((it: any) => it.dimType === 'TIME');
          if (flag) {
            message.warning(
              $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.OnlyOneTimeColumnCan',
                dm: '时间列仅能配置一个',
              }),
            );

            return;
          }
          dimName = `时间(${forms}##${zones === 'GMT+8' ? '默认' : zones})`;
        } else {
          dimName = nameModel;
          const flag = colsSplitList.some((it: any) => it.name === dimName);
          if (flag) {
            message.warning(
              $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.TheColumnNameAlreadyExists',
                dm: '列名已经存在',
              }),
            );

            return;
          }
        }
        if (list[index].names) {
          list[index].names.push({ name: dimName, nullable: nullAble });
        } else {
          list[index].names = [{ name: dimName, nullable: nullAble }];
        }
        updateTable(list); // 用于渲染表格

        const obj: any = {};
        obj.dimType = dimType;
        obj.name = dimName;
        obj.nullable = nullAble;
        obj.calConfig = {
          transforms: transforms,
          defaultValue,
        };

        if (splitType === 'LR') {
          obj.left = LR_Params.left;
          obj.right = LR_Params.right;
          obj.leftIndex = LR_Params.leftIndex;
        } else {
          obj.pos = index;
        }
        if (res.dimType === 'VALUE') {
          obj.dataType = 'DOUBLE';
        } else if (res.dimType === 'TIME') {
          obj.extInfo = {
            zone: zones,
            format: forms,
          };

          obj.code = '_time';
          obj.dataType = 'DATE';
        } else {
          obj.dataType = 'STRING';
        }
        colsSplitList.push(obj);
      }
      // 表单项赋值
      if (splitType === 'SEP') {
        form.setFieldsValue({
          colsSplit: colsSplitList,
        });
      } else if (splitType === 'REGEXP') {
        form.setFieldsValue({
          colsSplit: colsSplitList,
        });
      } else if (splitType === 'LR') {
        form.setFieldsValue({
          colsSplit: colsSplitList,
        });
      } else {
        form.setFieldsValue({
          colsGroovy: colsSplitList,
        });
      }
      form.resetFields(['forms', 'zones']);
      setOpen(false);
    });
  };

  const handleCancel = () => {
    setOpen(false);
    setCanClick(false);
    form.resetFields(['forms', 'zones']);
  };

  const handleTypeChange = (e: string) => {
    setCanClick(e === 'TIME');
    form.setFieldsValue({ nullAble: false });
  };

  const setFormMappingsFun = (transforms: any) => {
    
    let arr = ['mapping', 'regexp', 'contains'];
    let obj: any = {};
    (Array.isArray(transforms) ? transforms : []).forEach((item, index) => {
      obj = {
        ...obj,
        [index]: arr.includes(item.type),
      };
    });
    setSelectObj(obj);
  };

  useEffect(() => {
    if (!open) return;
    if (name) {
      let colsList: any;
      switch (splitType) {
        case 'SEP':
          colsList = form.getFieldValue('colsSplit');
          break;
        case 'REGEXP':
          colsList = form.getFieldValue('colsSplit');
          break;
        case 'LR':
          colsList = form.getFieldValue('colsSplit');
          break;
        default:
          colsList = form.getFieldValue('colsGroovy');
      }
      let newName = name;
      if (name?.includes('##')) {
        newName = name.split('##')[0].replace('时间(', '');
      }
      const filterItem = colsList.find((v: any) => v.name === name);
      const { nullable, dimType, calConfig, extInfo = {} } = filterItem;
      setFormMappingsFun(calConfig.transforms);
      setOldContent({ nullable, dimType, calConfig });
      form.setFieldsValue({
        nameModel: newName,
        transforms: calConfig?.transforms || [],
        dimType,
        nullAble: nullable,
        defaultValue: calConfig?.defaultValue,
      });

      if (dimType === 'TIME') {
        form.setFieldsValue({
          zones: name.split('##')[1].replace(')', ''),
          forms: newName,
        });

        setCanClick(true);
      }
    } else {
      form.setFieldsValue({
        nameModel: '',
        transforms: [],
        dimType: 'DIM',
        defaultValue: '',
        nullAble: true,
      });
      setCanClick(false);
    }
  }, [name, open]);

  const handleChange = (value: string, params: any) => {
    let arr = ['mapping', 'regexp', 'contains'];
    let key = params.fKey;
    let obj = {
      [key]: arr.includes(value),
    };
    setSelectObj({ ...selectObj, ...obj });
  };
  return (
    <Modal
      title={
        edit && !!name
          ? $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.EditColumnConfiguration',
            dm: '编辑列配置',
          })
          : $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.AddColumnConfiguration',
            dm: '新增列配置',
          })
      }
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      destroyOnClose
      width={550}
    >
      {edit && !!name && (
        <Alert
          message={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.NoteModifyingTheColumnType',
            dm: '提示： 列类型和维度配置修改将会导致该列数据丢失',
          })}
          type="warning"
          closable
          style={{ marginBottom: 15 }}
        />
      )}
      <Form.Item
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.ColumnValue',
          dm: '列值',
        })}
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
        labelAlign="right"
      >
        {columnData[index]?.columnValue === null
          ? 'null'
          : columnData[index]?.columnValue}
      </Form.Item>

      <Form.Item
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
        labelAlign="right"
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.ColumnType',
          dm: '列类型',
        })}
        name="dimType"
      >
        <Select style={{ width: 200 }} onChange={handleTypeChange}>
          <Option value="DIM">
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.CommonDimensionColumn',
              dm: '普通维度列',
            })}
          </Option>

          {/* <Option value="TIME">时间列</Option> */}

          <Option value="VALUE">
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.NumericColumn',
              dm: '数值列',
            })}
          </Option>
          <Option value="TIME">
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.TimeColumn',
              dm: '时间列',
            })}
          </Option>
        </Select>
      </Form.Item>
      <Form.Item
        shouldUpdate={(prev, next) => prev.dimType !== next.dimType}
        noStyle
      >
        {(form): any => {
          if (form.getFieldValue('dimType') !== 'TIME') {
            return (
              <Form.Item
                name="nameModel"
                initialValue={name}
                rules={[
                  { required: true },
                  () => ({
                    validator(_, value) {
                      if (!/^[0-9a-zA-Z]{1,}[0-9a-zA-Z_-]*$/.test(value)) {
                        return Promise.reject(
                          new Error(
                            $i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.PleaseEnterEnglishAndUnderscore',
                              dm: '请输入英文和下划线',
                            }),
                          ),
                        );
                      } else {
                        return Promise.resolve();
                      }
                    },
                  }),
                ]}
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.ColumnName',
                  dm: '列名称',
                })}
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 19 }}
                labelAlign="right"
              >
                <Input
                  autoComplete="off"
                  placeholder={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.EnterAColumnName',
                    dm: '请输入列名称',
                  })}
                  disabled={edit && !!name}
                />
              </Form.Item>
            );
          }
          return (
            <>
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.Format',
                  dm: '格式',
                })}
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 19 }}
                initialValue={'yyyy-MM-dd HH:mm:ss'}
                name="forms"
              >
                <Select style={{ width: 200 }}>
                  {forms.map((it: any) => (
                    <Select.Option value={it}>{it}</Select.Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.TimeZone',
                  dm: '时区',
                })}
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 19 }}
                initialValue={'GMT+8'}
                name="zones"
              >
                <Select style={{ width: 200 }}>
                  {zones.map((it: any) => (
                    <Select.Option value={it}>
                      {it === 'GMT+8'
                        ? $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.Default',
                          dm: '默认',
                        })
                        : it}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </>
          );
        }}
      </Form.Item>

      <Form.Item
        name="nullAble"
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.DimensionConfiguration',
          dm: '维度配置',
        })}
        labelAlign="right"
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
        tooltip={{
          title: $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.IfItIsSetTo',
            dm: '如果设置成不可空，当该列数据为空时，该条日志将被过滤。',
          }),
        }}
      >
        <Radio.Group disabled={canClick}>
          <Radio value={true}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.Nullable',
              dm: '可空',
            })}
          </Radio>

          <Radio value={false}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.NotEmpty',
              dm: '不可空',
            })}
          </Radio>
        </Radio.Group>
      </Form.Item>

      <Form.Item
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.ConversionFunction',
          dm: '转换函数',
        })}
        labelAlign="right"
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
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
        <Form.List name="transforms">
          {(fields, { add, remove }) => (
            <>
              {fields.map((field) => (
                <div key={field.key}>
                  <Space style={{ display: 'flex' }} align="baseline">
                    <Form.Item
                      name={[field.name, 'type']}
                      fieldKey={[field.fieldKey, 'type']}
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
                      <Select style={{ width: 118 }} onChange={handleChange}>
                        {Object.keys(transFuncs).map((key: string) => {
                          return (
                            <Option
                              key={transFuncs[key]}
                              value={transFuncs[key]}
                              fKey={field.key}
                            >
                              {key}
                            </Option>
                          );
                        })}
                      </Select>
                    </Form.Item>

                    <Form.Item
                      name={[field.name, 'defaultValue']}
                      fieldKey={[field.fieldKey, 'defaultValue']}
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
                          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.DefaultValue',
                          dm: '参数',
                        })}
                        autoComplete="off"
                        style={{ width: 218 }}
                      />
                    </Form.Item>

                    <a onClick={() => remove(field.name)}>
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.Delete',
                        dm: '删除',
                      })}
                    </a>
                  </Space>
                  {selectObj[field.key] && (
                    <Form.List name={[field.name, 'mappings']}>
                      {(fields, { add, remove }) => (
                        <>
                          <Button
                            type="primary"
                            style={{ marginBottom: 15, marginTop: -10 }}
                            onClick={() => {
                              add();
                            }}
                          >
                            新增参数
                          </Button>
                          {fields.map(({ name, key }) => (
                            <Space
                              key={key}
                              style={{
                                display: 'flex',
                                marginBottom: 8,
                              }}
                              align="baseline"
                            >
                              <Form.Item
                                name={[name, 'key']}
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
                                  style={{ width: 150 }}
                                  placeholder="key"
                                />
                              </Form.Item>
                              <Form.Item
                                name={[name, 'value']}
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
                                  style={{ width: 150 }}
                                  placeholder="value"
                                />
                              </Form.Item>
                              <a onClick={() => remove(name)}>
                                {$i18n.get({
                                  id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.Delete',
                                  dm: '删除',
                                })}
                              </a>
                            </Space>
                          ))}
                        </>
                      )}
                    </Form.List>
                  )}
                  <Divider style={{ marginTop: 0 }} />
                </div>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()}>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.AddConversionFunction',
                    dm: '添加转换函数',
                  })}
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
      </Form.Item>

      <Form.Item
        label={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.DefaultValue',
          dm: '默认值',
        })}
        name="defaultValue"
        labelAlign="right"
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
      >
        <Input
          placeholder={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.ModelItem.EnterADefaultValue',
            dm: '请输入默认值',
          })}
          autoComplete="off"
        />
      </Form.Item>
    </Modal>
  );
};
export default ModelComponent;
