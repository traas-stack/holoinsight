import {
  Button,
  Card,
  Col,
  Form,
  Input,
  Radio,
  Row,
  Select,
} from 'antd';
import _ from 'lodash';
import React, { useEffect, useState,useImperativeHandle } from 'react';
import styles from './index.less';
import { useModel } from '@umijs/max';
import { queryAll, queryByTenant } from '@/services/customplugin/api';
import { getTenantAppByCondition } from '@/services/infra/api';
import { MinusOutlined } from '@ant-design/icons';
import $i18n from '../../i18n';
import LogComp from '@/components/AgentFormList/LogComp'
import FilterForm from '../FilterForm';
const { Option } = Select;

const FormListBase = React.forwardRef((props: any, ref) => {
  const { initialState } = useModel('@@initialState');
  const { formConfig, form, selectKey, setSelectKey, installed } = props;
  const [serverData, setServerData] = useState<any>([]);
  const [logEditVisible, setLogEditVisible] = useState<boolean>(false);
  const [logEditConfig, setLogEditConfig] = useState<any>({});
  const [appList, setAppList] = useState([]);
  const [logStatus, setLogStatus] = useState<String>('add');
  const [logFilter, setLogFilter] = useState<boolean>(true);
  const [tableData, setTableData] = useState<any[]>([]);
  const [tableKey, setTableKey] = useState<number>(0);
  const configList = _.cloneDeep(formConfig?.configList || []);
  let formInitialValue = [{}];

  let formaterFormList: any = [];
  if (formConfig?.formType !== 'none') {
    formaterFormList = _.chunk(formConfig?.formList || [], 3);
  }
  const Tenant = async () => {
    const params = {
      tenant: initialState.currentTenant,
    };

    const res = await queryByTenant(params);
    const tableName = res?.[0].name;
    const param = {
      tableName,
      tenant: initialState.currentTenant,
    };

    const resquest = await queryAll(param);
    let tableData = resquest?.map((item: any) => {
      return {
        key: `${item._uk}`,
        title: `${item.ip}`,
        desc: `${item.ip}(${item.hostname})`,
      };
    });
    setServerData(tableData);
  };
  useEffect(() => {
    if ((formConfig?.filters || []).length) {
      Tenant();
    }
  }, [(formConfig?.filters || []).lentgh]);

  useEffect(() => {
    getTenantAppByCondition({}).then((res:any) => {
      const list = (res || []).map((item:any) => {
        let newItem:any = {};
        newItem.label = item.app;
        newItem.value = item.app;
        return newItem;
      });
      setAppList(list);
    });
  }, []);

  useImperativeHandle(ref, () => ({
     getTable:()=>{
      return tableData;
     },
     setTable:(data:any)=>{
       setTableData(data || [])
     }
  }));

  const onSave = (obj?:any, type?:string, id?:number) => {
    if(logFilter){
      if(type === 'add'){
        tableData.push(obj);
        setTableData(tableData);
      }else{
        const newArr = JSON.parse(JSON.stringify(tableData));
        newArr.splice(id, 1 ,obj)
        setTableData(newArr);
      }
    }
    setLogEditVisible(false);
  };
  
  function handleDeleteItem(key:number){
    const newArr = JSON.parse(JSON.stringify(tableData));
    newArr.splice(key,1)
    setTableData(newArr);
  }

  function handleEditItem(record:any, key:number){
    setTableKey(key);
    setLogEditVisible(true);
    setLogStatus('edit');
    setLogEditConfig(record);
  }

  function handleChangeShow(status:boolean){
     setLogFilter(true);
     setLogEditVisible(status);
     
  }

  function handleChangeStatus(status: string){
    setLogStatus(status);
  }



  return (
    <div>
      {formConfig?.filters?.length ? (
        <Card className={styles.agentConfigCard}>
          <FilterForm
            type="integration"
            form={form}
            selectKey={selectKey}
            setSelectKey={setSelectKey}
            serverData={serverData}
          />
        </Card>
      ) : null}

      {/* {logEditVisible && (
        <DrawerLogEdit
          tableKey = {tableKey}
          value={logEditConfig}
          logFilter = {logFilter}
          action={logStatus}
          visible={logEditVisible}
          installed={installed}
          onSave={onSave}
          onCancle={() => {
            setLogEditVisible(false);
          }}
        />
      )} */}
      {formConfig.formType === 'multiple' ? (
        <Form.List name="json" initialValue={formInitialValue}>
          {(fields, { add: addItem, remove: removeItem }) => {
            return (
              <>
                {fields.map((fieldAll: any, fieldIndex: any) => {
                  return (
                    <div key={fieldAll.name}>
                      <Card className={styles.card}>
                        {formaterFormList.map((item:any, index:number) => {
                          return (
                            <Row key={index}>
                              <Form.Item
                                label="conf"
                                labelCol={{ span: 16 }}
                                name={[fieldAll.name, 'conf']}
                                rules={[
                                  {
                                    required: false,
                                    message: $i18n.get({
                                      id: 'holoinsight.components.AgentFormList.Required',
                                      dm: '必填项',
                                    }),
                                  },
                                ]}

                                hidden={true}
                              />
                              {item.map((ele: any, key: number) => {
                                return (
                                  <React.Fragment key={ele.value}>
                                    {ele.type === 'selectConfig' && (
                                      <Col span={1}>
                                        <a
                                          onClick={() => {
                                            setLogEditConfig(
                                              formConfig?.configList?.[
                                              fieldIndex
                                              ] || {},
                                            );
                                            setLogStatus('edit');
                                            setLogFilter(false);
                                            setLogEditVisible(true);
                                          }}
                                        >
                                          {$i18n.get({
                                            id: 'holoinsight.components.AgentFormList.Preview',
                                            dm: '预览',
                                          })}
                                        </a>
                                      </Col>
                                    )}

                                    <Col
                                      span={7}
                                      offset={key % 3 === 0 ? 0 : 1}
                                    >
                                      <Form.Item
                                        label={ele.name}
                                        labelCol={{ span: 16 }}
                                        name={[fieldAll.name, ele.value]}
                                        rules={[
                                          {
                                            required: ele.required,
                                            message: $i18n.get({
                                              id: 'holoinsight.components.AgentFormList.Required',
                                              dm: '必填项',
                                            }),
                                          },
                                        ]}

                                      >
                                        {(() => {
                                          switch (ele.type) {
                                            case 'selectConfig':
                                              return (
                                                <Select style={{ width: 250 }}>
                                                  {configList?.map(
                                                    (config: any) => {
                                                      return (
                                                        <Option
                                                          value={config.name}
                                                          key={config.name}
                                                        >
                                                          {config.name}
                                                        </Option>
                                                      );
                                                    },
                                                  )}
                                                </Select>
                                              );
                                            case 'selectRange':
                                              return (
                                                <Select
                                                  mode="tags"
                                                  options={appList}
                                                  style={{ width: 250 }}
                                                  placeholder="select apps"
                                                ></Select>
                                              );
                                            case 'radio':
                                              return (
                                                <Radio.Group>
                                                  {ele.optionValues?.map(
                                                    (val: any) => {
                                                      return (
                                                        <Radio
                                                          value={val.value}
                                                          key={val.value}
                                                        >
                                                          {val.name}
                                                        </Radio>
                                                      );
                                                    },
                                                  )}
                                                </Radio.Group>
                                              );
                                            case 'select':
                                              return (
                                                <Select style={{ width: 250 }}>
                                                  {ele.optionValues?.map(
                                                    (val: any) => {
                                                      return (
                                                        <Select.Option
                                                          value={val.value}
                                                          key={val.value}
                                                        >
                                                          {val.name}
                                                        </Select.Option>
                                                      );
                                                    },
                                                  )}
                                                </Select>
                                              );
                                            case 'selectMulti':
                                              return (
                                                <Select
                                                  style={{ width: 250 }}
                                                  mode="multiple"
                                                >
                                                  {ele.optionValues?.map(
                                                    (val: any) => {
                                                      return (
                                                        <Select.Option
                                                          value={val.value}
                                                          key={val.value}
                                                        >
                                                          {val.name}
                                                        </Select.Option>
                                                      );
                                                    },
                                                  )}
                                                </Select>
                                              );
                                            default:
                                              return (
                                                <Input
                                                  style={{ width: 250 }}
                                                  placeholder={ele.placeholder}
                                                />
                                              );
                                          }
                                        })()}
                                      </Form.Item>
                                    </Col>
                                    {index === formaterFormList.length - 1 &&
                                      key === item.length - 1 ? (
                                        <Button
                                          style={{ margin: '34px 0px 0px 10px' }}
                                          type="primary"
                                          icon={<MinusOutlined />}
                                          shape="circle"
                                          size="small"
                                          onClick={() =>
                                            removeItem(fieldAll.name)
                                          }
                                        ></Button>
                                      ) : null}
                                  </React.Fragment>
                                );
                              })}
                            </Row>
                          );
                        })}
                      </Card>
                    </div>
                  );
                })}

                <Button
                  style={{ width: '100%' }}
                  onClick={() => addItem()}
                >
                  {$i18n.get({
                    id: 'holoinsight.components.AgentFormList.Add',
                    dm: '添加',
                  })}
                </Button>
              </>
            );
          }}
        </Form.List>
      ) : formConfig.formType === 'single' ? (
        <Card className={styles.card}>
          {formaterFormList.map((item:any, index:number) => {
            return (
              <Row key={index}>
                {item.map((ele: any, key: number) => {
                  return (
                    <Col
                      span={7}
                      offset={key % 3 === 0 ? 0 : 1}
                      key={ele.value}
                    >
                      <Form.Item
                        label={ele.name}
                        labelCol={{ span: 16 }}
                        name={['json', ele.value]}
                        rules={[
                          {
                            required: true,
                            message: $i18n.get({
                              id: 'holoinsight.components.AgentFormList.Required',
                              dm: '必填项',
                            }),
                          },
                        ]}
                      >
                        <Input placeholder={ele.placeholder} />
                      </Form.Item>
                    </Col>
                  );
                })}
              </Row>
            );
          })}
        </Card>
      ) : (formConfig.formType === 'ltp' || formConfig.formType === 'multilog') ? (
        <LogComp
      tableData = {tableData}
      handleChangeShow = {handleChangeShow}
      handleChangeStatus = {handleChangeStatus}
      handleEditItem = { handleEditItem }
      handleDeleteItem = { handleDeleteItem }
      /> 
     ) : null}
    </div>
  );
})

export default FormListBase;