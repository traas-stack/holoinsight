import React, { useState, useEffect, useRef } from 'react';
import {
  Tabs,
  Drawer,
  Descriptions,
  Button,
  Form,
  Input,
  Typography,
  Table,
  Popconfirm,
  message,
} from 'antd';
import AgentItem from '@/components/AgentItem/index';
import FormListBase from '@/components/AgentFormList/index';
import InterationDashboard from '@/components/InterationDashboard/index';
import styles from './index.less';
import { TcheckInfo, TPopItem } from './index.d';
import {
  getAllProductInfo,
  getProductReceived,
  getQueryProductByName,
  updatePlugin,
  getAllPlugin,
  createPlugin,
  queryPluginById,
  deletePlugin,
} from '@/services/intergration/api';

import moment from 'moment';
import { useModel } from '@umijs/max';
import $i18n from '../../../i18n';
import { userFavoriteDeleteById } from '@/services/tenant/favoriteApi';
import _ from 'lodash';
const { TabPane } = Tabs;

const PopConfig: TPopItem[] = [
  {
    id: 1,
    name: $i18n.get({
      id: 'holoinsight.integration.agentComponent.Overview',
      dm: '概述',
    }),
  },
  {
    id: 2,
    name: $i18n.get({
      id: 'holoinsight.integration.agentComponent.Configuration',
      dm: '配置',
    }),
  },
  {
    id: 3,
    name: $i18n.get({
      id: 'holoinsight.integration.agentComponent.Indicators',
      dm: '指标',
    }),
  },
  {
    id: 4,
    name: $i18n.get({
      id: 'holoinsight.integration.agentComponent.Dashboard',
      dm: '仪表盘',
    }),
  },
];

const tableHeader = [
  {
    title: 'name',
    dataIndex: 'name',
    key: 'name',
  },

  {
    title: 'unit',
    dataIndex: 'unit',
    key: 'unit',
  },

  {
    title: 'desc',
    dataIndex: 'desc',
    key: 'desc',
  },
];

const { Text } = Typography;
const TableAgent = (props: any) => {
  const { data, handleChangeItem, type } = props;
  return data.length ? (
    <div className={styles.componentRow}>
      {data.map((item: any, index: number) => {
        return (
          <div
            className={`${styles.componentRowItem} ${index % 5 === 4 ? '' : styles.mgr20
              }`}
            key={index}
            onClick={() => handleChangeItem(item, index, type)}
          >
            <AgentItem keyIndex={index} data={item} />
          </div>
        );
      })}
    </div>
  ) : (
    <p className={styles.hasNotAgent}>
      {$i18n.get({
        id: 'holoinsight.integration.agentComponent.NoInstalledComponents',
        dm: '暂无安装的组件',
      })}
    </p>
  );
};
const AgentComponent: React.FC<any> = () => {

  const { initialState } = useModel('@@initialState');
  const tableRef = useRef(null);
  const [checkInfo, setCheckInfo] = useState<TcheckInfo>({
    checkKey: '1',
    type: '',
  });
  const [visible, setVisible] = useState<boolean>(false);
  const [allAgent, setAllAgent] = useState([]);
  const [dataItem, setDataItem] = useState<any>({});
  const [hasAgent, setHasAgent] = useState([]);
  const [formConfig, setFormConfig] = useState<any>({});
  const [selectKey, setSelectKey] = useState('');
  const [product, setProduct] = useState('');
  const [popForm] = Form.useForm();
  const [interName, setInterName] = useState('');
  const [initName, setInitName] = useState<any>({});
  const [key, setKey] = useState<string>('1')

  function initData() {
    getAllPlugin().then((res) => {
      if (Array.isArray(res)) {
        const arr: any = res.map((item) => {
          item.hasData = true;
          item.installed = true;
          item.installedId = item.id;
          item.productName = item.product;
          return item;
        });
        setHasAgent(arr);
        getAllProductInfo().then((data: any) => {
          let newData = data.map((item: any) => {
            let flag: boolean | string = false;
            let pluginId: any = '';
            res.forEach((element) => {
              if (element.type === item.type) {
                flag = `${element.id}-${element.name}`;
                pluginId = element.id;
              }
            });
            item.productName = item.name;
            item.hasData = true;
            item.installed = flag;
            item.installedId = pluginId;
            return item;
          });
          setAllAgent(newData);
        });
      }
    });
  }
  useEffect(() => {
    initData();
  }, []);
  function handleChangeTabs(value: string, dataItem: any) {
    if (dataItem.installed && value === '2') {
      queryPluginById(dataItem.installedId).then((res: any) => {
        const config = JSON.parse(res?.json || '{}');
        setProduct(res?.product);
        let key = formConfig.formType === 'single' ? 'conf' : 'confs';
        popForm.setFieldsValue({
          json: config[key],
        });
        if (dataItem.name === 'LTP' || dataItem.name === 'MultiLog') {
          tableRef?.current?.setTable(JSON.parse(res?.json).confs)
        }
        if ((formConfig?.filters || []).length) {
          const baseData = res?.collectRange?.cloudmonitor?.condition;
          let seletIndex = Object.keys(baseData?.[0] || {})?.[0];

          if (baseData.length > 1) {
            seletIndex = 'label';
          } else if (
            baseData.length === 1 &&
            seletIndex !== 'app' &&
            seletIndex !== '_uk'
          ) {
            seletIndex = 'label';
          }
          
          let backCollectRanges =
            seletIndex === 'label'
              ? baseData?.map((item: any) => {
                return {
                  key: Object.keys(item)[0],
                  value: Object.values(item)[0],
                };
              })
              : baseData?.[0]?.[seletIndex];
          setSelectKey(seletIndex);
          popForm.setFieldsValue({
            typeFilter: seletIndex,
            collectRanges: backCollectRanges,
          });
        }
        setInterName(res?.name);
      });
    } else if (value === '2') {
      let jsonObject: any[] = [];
      if (
        formConfig.formType === 'multiple' &&
        formConfig?.configList?.length > 0
      ) {
        formConfig?.configList?.forEach((config: any) => {
          jsonObject.push({ name: config.name, conf: config?.conf });
        });
        popForm.setFieldsValue({
          json: jsonObject,
        });
      }
    }
    checkInfo.checkKey = value;
    setCheckInfo({ ...checkInfo });
  }
  function handleChangeItem(item: any, index: number, type: string) {
    setInitName(item);
    let newformConfig = {};
    allAgent.forEach((ele) => {
      if (ele.type === item.type) {
        newformConfig = ele.form;
      }
    });
    setFormConfig(newformConfig);
    let newObj = { ...checkInfo };
    newObj.checkKey = index;
    newObj.type = type;
    if (item.product) {
      getQueryProductByName(item.product).then((res) => {
        let newData = res[0];
        newData.installed = item.installed;
        newData.installedId = item.id;
        setDataItem(newData);
        setCheckInfo(newObj);
        setVisible(true);
      });
    } else {
      setDataItem(item);
      setCheckInfo(newObj);
      setVisible(true);
    }
  }

  function getMetricsFromLogTemplate(product: any, item: any) {
    let array: any = [];

    if (item && item.json) {
      const json = JSON.parse(item.json);
      _.forEach(json?.confs, confs => {
        let dataSource: any[] = [];
        _.forEach(confs?.conf?.collectMetrics, conf => {
          dataSource.push({
            aggregator: "avg",
            desc: confs.name + ' ' + conf.tableName + " metric",
            format: "number",
            name: product.toLowerCase() +'_' + confs.name + '_' + conf.tableName,
            unit: "default",
          })
        })

        array.push({
          tabName: confs.name,
          dataSource
        })
      })
    }
    return array;
  }

  function renderTabChildren(item: string, dataItem: any) {
    let metricArr = Object.keys(dataItem?.metrics?.subMetrics || {}).map(
      function (el) {
        return {
          tabName: el,
          dataSource: dataItem?.metrics?.subMetrics[el],
        };
      },
    );
    if (dataItem.installed && (dataItem.name === 'LTP' || dataItem.name === 'MultiLog')) {
      metricArr = getMetricsFromLogTemplate(dataItem.name, initName);
    }
    switch (item.id) {
      case 1:
        return (
          <div style={{ margin: '12px 0' }}>
            <div dangerouslySetInnerHTML={{ __html: dataItem.overview }}></div>
          </div>
        );
      case 2:
        return (
          <>
            {/* <Alert message="Info Text" type="info" /> */}

            {/* <div style={{ margin: '12px 0' }}>
              <div
                dangerouslySetInnerHTML={{ __html: dataItem.configuration }}
              ></div>
            </div> */}

            <section style={{ marginBottom: '10px' }}>
              <Form form={popForm} layout="vertical">
                <FormListBase
                  ref={tableRef}
                  selectKey={selectKey}
                  setSelectKey={setSelectKey}
                  formConfig={formConfig}
                  form={popForm}
                  installed={dataItem.installed}
                />
              </Form>
            </section>
          </>
        );
      case 3:
        return (
          <>
            <div style={{ margin: '12px 0' }}>
              <Text>
                The following metrics will be tracked by this integration:
              </Text>
            </div>

            <div>
              <Tabs>
                {(metricArr || []).map((item, index) => {
                  return (
                    <TabPane tab={item.tabName} key={index}>
                      <Table
                        columns={tableHeader}
                        dataSource={item.dataSource}
                        pagination={false}
                      />
                    </TabPane>
                  );
                })}
              </Tabs>
            </div>
          </>
        );

      default:
        return <InterationDashboard dataItem={initName} />;
    }
  }
  function handleSearchByName(value: string) {
    getQueryProductByName(value).then((res) => {
      setAllAgent(res || []);
    });
  }
  function commonback(json: any, data: any) {
    let param: any = {};
    let key;

    switch (formConfig.formType) {
      case 'single':
        key = 'conf';
        break;
      case 'multiple':
        key = 'confs';
        break;
      default:
        key = 'confs';
    }

    param[key] = json;
    param.name = data.name;
    param.type = data.type;
    return JSON.stringify(param);
  }

  function handleUpdateConfig(dataItem: any) {
    popForm
      .validateFields()
      .then((res) => {
        dataItem.json = commonback(res.json || tableRef?.current?.getTable(), dataItem);
        if ((formConfig?.filters || []).length) {
          dataItem.collectRange = {};
          dataItem.collectRange.type = 'cloudmonitor';
          const type = res.typeFilter;
          let newCondition: any = [];
          dataItem.collectRange.cloudmonitor = {
            table: `${initialState.currentTenant}_server`,
            condition:
              type === 'label'
                ? newCondition.concat(res.collectRanges.map((item) => {
                  return {
                    [item.key]: item.value,
                  };
                }))
                :newCondition.concat([
                  {
                    [type]: res.collectRanges,
                  },
                ]),
          };
        }
        if (typeof dataItem.installed === 'string') {
          dataItem.id = Number(dataItem.installed.split('-')[0]);
          dataItem.name = dataItem.installed.split('-')[1];
          dataItem.product = product;
        }
        if (dataItem.installed === true) {
          const typed: any = hasAgent.filter((item) => {
            return item.product === dataItem.name;
          });
          dataItem.id = typed[0].id;
          dataItem.product = typed[0].product;
        }
        dataItem.name = interName;
        let template;
        if (key === '2' && dataItem.installed) {
          hasAgent.forEach((item: any, index: number) => {
            if (item.productName === dataItem.productName) {
              template = item.template;
            }
          })
        } else {
          template = initName.template;
        }
        if (template) {
          dataItem.template = template;
        }
        delete dataItem.installed;
        delete dataItem.hasData;

        updatePlugin(dataItem).then(() => {
          message.success(
            $i18n.get({
              id: 'holoinsight.integration.agentComponent.TheConfigurationHasBeenUpdated',
              dm: '更新配置成功！',
            }),
          );
          initData();
          setVisible(false);
          popForm.resetFields();
        });
      })
      .catch((err) => {
      });
  }

  function hanldleInstall(type: string) {
    popForm
      .validateFields()
      .then((res) => {
        const collectRange = {};
        let newCondition: any = [];
        if ((formConfig?.filters || []).length) {
          const type = res.typeFilter;
          collectRange.type = 'cloudmonitor';
          collectRange.cloudmonitor = {
            table: `${initialState.currentTenant}_server`,
            condition:
              type === 'label'
                ? newCondition.concat(res.collectRanges.map((item) => {
                  return {
                    [item.key]: item.value,
                  };
                }))
                : newCondition.concat([
                  {
                    [type]: res.collectRanges,
                  },
                ]),
          };
        }
        createPlugin({
          type: type,
          collectRange: collectRange,
          tenant: initialState.currentTenant,
          product: dataItem.name,
          template: dataItem.template,
          name: `${initialState.currentTenant}_${type}_1`,
          json: commonback(res.json || tableRef?.current?.getTable() || {}, dataItem),
          version: dataItem.version,
        }).then((res) => {
          if (res) {
            message.success(
              $i18n.get({
                id: 'holoinsight.integration.agentComponent.InstalledSuccessfully',
                dm: '安装成功！',
              }),
            );
            setVisible(false);
            initData();
          }
        });
      })
      .catch(() => { });
  }

  function renderFooter(dataItem: any, checkInfo: any) {
    return (
      <section className={styles.configFooter}>
        {dataItem.installed ? (
          <Popconfirm
            title={$i18n.get({
              id: 'holoinsight.integration.agentComponent.PleaseConfirmWhetherToUninstall',
              dm: '请确认是否卸载？',
            })}
            onConfirm={() => {
              let id;
              if (dataItem.installed === true) {
                const typed: any = hasAgent.filter((item: any) => {
                  return item.product === dataItem.name;
                });
                id = typed[0].id;
              } else {
                id = dataItem.installed.split('-')[0];
              }
              userFavoriteDeleteById('integration', dataItem.name);
              deletePlugin(id).then(() => {
                message.success(
                  $i18n.get({
                    id: 'holoinsight.integration.agentComponent.UninstallSuccessfully',
                    dm: '卸载成功！',
                  }),
                );
                setVisible(false);
                popForm.resetFields();
                initData();
              });
            }}
          >
            <Button danger>
              {$i18n.get({
                id: 'holoinsight.integration.agentComponent.UninstallIntegration',
                dm: '卸载集成',
              })}
            </Button>
          </Popconfirm>
        ) : (
          <Button
            type="primary"
            onClick={() => {
              hanldleInstall(dataItem.type);
            }}
            hidden={checkInfo?.checkKey !== '2'}
          >
            {$i18n.get({
              id: 'holoinsight.integration.agentComponent.InstallationIntegration',
              dm: '安装集成',
            })}
          </Button>
        )}

        {dataItem.installed && checkInfo.checkKey === '2' ? (
          <Button
            style={{ marginRight: '16px' }}
            onClick={() => handleUpdateConfig(dataItem)}
          >
            {$i18n.get({
              id: 'holoinsight.integration.agentComponent.UpdateConfiguration',
              dm: '更新配置',
            })}
          </Button>
        ) : null}
      </section>
    );
  }
  return (
    <div className={styles.intergration}>
      <div className={styles.title}>
        <h2 className={styles.title}>
          {$i18n.get({
            id: 'holoinsight.integration.agentComponent.IntegratedComponentLibrary',
            dm: '集成组件库',
          })}
        </h2>

        <Input.Search
          style={{ width: 200 }}
          placeholder={$i18n.get({
            id: 'holoinsight.integration.agentComponent.ByClusterName',
            dm: '按照集群名称',
          })}
          onSearch={handleSearchByName}
        />
      </div>
      <div
        className={window?.holoTheme === 'holoLight' ? styles.cards : null}
      >
        <Tabs defaultActiveKey="1" onChange={(key) => {
          setKey(key);
        }}>
          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.integration.agentComponent.TheComponentsIInstalled',
              dm: '我安装的组件',
            })}
            key="1"
          >
            <TableAgent
              type="install"
              data={hasAgent}
              handleChangeItem={handleChangeItem}
            />
          </TabPane>

          <TabPane
            tab={$i18n.get({
              id: 'holoinsight.integration.agentComponent.IntegratedComponentLibrary',
              dm: '集成组件库',
            })}
            key="2"
          >
            <TableAgent
              type="all"
              data={allAgent}
              handleChangeItem={handleChangeItem}
            />
          </TabPane>
        </Tabs>
      </div>

      <Drawer
        width={900}
        title={dataItem.name}
        placement="right"
        onClose={() => {
          checkInfo.checkKey = '1';
          setCheckInfo({ ...checkInfo });
          popForm.resetFields();
          setVisible(false);
        }}
        open={visible}
        destroyOnClose={true}
        footer={renderFooter(dataItem, checkInfo)}
      >
        <Descriptions size="middle" column={3}>
          {dataItem.installed ? (
            <>
              <Descriptions.Item
                label={$i18n.get({
                  id: 'holoinsight.integration.agentComponent.InstallationTime',
                  dm: '安装时间',
                })}
              >
                {moment(dataItem.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>

              <Descriptions.Item
                label={$i18n.get({
                  id: 'holoinsight.integration.agentComponent.Status',
                  dm: '状态',
                })}
              >
                {dataItem.status
                  ? $i18n.get({
                    id: 'holoinsight.integration.agentComponent.Normal',
                    dm: '正常',
                  })
                  : $i18n.get({
                    id: 'holoinsight.integration.agentComponent.Error',
                    dm: '错误',
                  })}
              </Descriptions.Item>

              <Descriptions.Item
                label={$i18n.get({
                  id: 'holoinsight.integration.agentComponent.Installer',
                  dm: '安装人',
                })}
              >
                {dataItem.creator}
              </Descriptions.Item>
            </>
          ) : null}

          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.integration.agentComponent.Introduction',
              dm: '简介',
            })}
          >
            {dataItem?.profile || '-'}
          </Descriptions.Item>
        </Descriptions>

        <Tabs
          defaultActiveKey="1"
          onChange={(value) => handleChangeTabs(value, dataItem)}
        >
          {PopConfig.map((item) => {
            return (
              <TabPane tab={item.name} key={item.id}>
                <div>{renderTabChildren(item, dataItem)}</div>
              </TabPane>
            );
          })}
        </Tabs>
      </Drawer>
    </div>
  );
};

export default AgentComponent;
