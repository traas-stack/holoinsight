import CommonBreadcrumb from '@/components/CommonBreadcrumb';
import {
  create,
  queryAll,
  queryById,
  queryByTenant,
  update,
} from '@/services/customplugin/api';
import { Button, Form, message, PageHeader } from 'antd';
import _ from 'lodash';
import { useEffect, useState } from 'react';
import { history } from 'umi';
import $i18n from '../../../i18n';
import BasicInfo from './BasicInfo';
import CollectMetric from './CollectMetric';
import { handleLRColumnValue } from './Common';
import styles from './index.less';
import LogSegmentation from './LogSegmentation';
import { useModel } from 'umi';

const LogMonitor = (props: any) => {
  const [form] = Form.useForm();
  const { action, id, notBead, parentId } = props?.match?.params || props;
  const [editItem, setEditItem] = useState<any>();
  const [selectKey, setSelectKey] = useState('');
  const [serverData, setServerData] = useState<any>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const { initialState } = useModel('@@initialState');

  const translateFun = (item: any) => {
    if (Array.isArray(item.transforms)) {
      item.transforms?.forEach((item: any) => {
        if (item.mappings) {
          let arr = [];
          for (let k in item.mappings) {
            arr.push({ key: k, value: item.mappings[k] });
          }
          item.mappings = arr;
        }
      });
    }
    return item;
  };
  const initDatasource = (initData: any) => {
    const conf = initData.conf;

    const preFilterTable: any[] = [];
    if (conf?.blackFilters?.length > 0) {
      _.forEach(conf?.blackFilters, (filter: any) => {
        const p = {
          right: filter?.rule?.right,
          left: filter?.rule?.left,
          leftIndex:
            filter?.rule?.left === '' ? 0 : filter?.rule?.leftIndex + 1,
        };
        preFilterTable.push({
          operatorType: 'black',
          filterType: filter?.type,
          left: filter?.rule?.left,
          right: filter?.rule?.right,
          leftIndex: filter?.rule?.leftIndex,
          translate: filter?.rule?.translate,
          values: filter.values,
          columnValue:
            filter?.type === 'CONTAINS'
              ? filter.values?.[0]
              : handleLRColumnValue(p, initData.sampleLog),
        });
      });
    }
    if (conf?.whiteFilters?.length > 0) {
      _.forEach(conf?.whiteFilters, (filter: any) => {
        const p = {
          right: filter?.rule?.right,
          left: filter?.rule?.left,
          leftIndex:
            filter?.rule?.left === '' ? 0 : filter?.rule?.leftIndex + 1,
        };
        preFilterTable.push({
          operatorType: 'write',
          filterType: filter?.type,
          left: filter?.rule?.left,
          right: filter?.rule?.right,
          leftIndex: filter?.rule?.leftIndex,
          translate: filter?.rule?.translate,
          values: filter.values,
          columnValue:
            filter?.type === 'CONTAINS'
              ? filter.values?.[0]
              : handleLRColumnValue(p, initData.sampleLog),
        });
      });
    }
    let seletIndex = Object.keys(
      conf?.collectRanges?.condition?.[0] || {},
    )?.[0];

    if (conf?.collectRanges?.condition.length > 1) {
      seletIndex = 'label';
    } else if (
      conf?.collectRanges?.condition.length === 1 &&
      seletIndex !== 'app' &&
      seletIndex !== '_uk'
    ) {
      seletIndex = 'label';
    }
    let backCollectRanges: any[] = [];
    if (seletIndex === 'label') {
      conf?.collectRanges?.condition?.map((item: any, index: number) => {
        Object.keys(item).map((key) => {
          backCollectRanges.push({
            key: key,
            value: item[key],
          });
        });
      });
    } else {
      backCollectRanges = conf?.collectRanges?.condition?.[0]?.[seletIndex];
    }

    setSelectKey(seletIndex);
    const editItem: any = {
      id: initData.id,
      name: initData.name,
      sampleLog: initData.sampleLog
        ? {
            value: [initData.sampleLog],
            selectValue: initData.sampleLog,
          }
        : {},
      status: initData.status === 'ONLINE' ? true : false,
      periodType: initData.periodType,

      collectMetrics: conf.collectMetrics?.map((metric: any) => {
        const baseobj: any = {
          metricType: metric.metricType,
          containsValue: metric.containValue,
          tags: metric.tags || [],
          metrics: metric?.metrics?.[0].name,
          func: metric?.metrics?.[0].func,
          afterFilters: metric.afterFilters,
        };
        if (metric.name) {
          baseobj.tableName = metric.name;
          baseobj.name = true;
        } else {
          baseobj.tableName = metric.tableName;
          baseobj.name = false;
        }
        return baseobj;
      }),
      typeFilter: seletIndex,
      collectRanges: backCollectRanges||[],
      logPath: conf?.logPaths || [],
      maxKeySize: conf?.extraConfig?.maxKeySize || 1000,
      keyCleanInterval: conf?.extraConfig?.keyCleanInterval || false,
      splitType: conf?.logParse?.splitType || 'NO',
      splitFlag:
        conf?.logParse?.splitType === 'SEP'
          ? [conf?.logParse?.separator?.separatorPoint]
          : [],
      splitRegexp:
        conf?.logParse?.splitType === 'REGEXP'
          ? conf?.logParse?.regexp?.expression
          : [],
      multiLine: conf?.logParse?.multiLine?.multi ? true : false,
      lineType: conf?.logParse?.multiLine?.multi
        ? conf?.logParse?.multiLine?.lineType
        : null,
      logRegexp: conf?.logParse?.multiLine?.multi
        ? conf?.logParse?.multiLine?.logRegexp
        : null,

      preFilterChecked:
        conf?.whiteFilters?.length > 0 || conf?.blackFilters?.length > 0,
      preFilterTable,
      colsSplit: conf?.splitCols?.map((split: any) => {
        return {
          name: split.name,
          dimType: split.colType,
          left: split?.rule?.left,
          right: split?.rule?.right,
          leftIndex: split?.rule?.leftIndex,
          pos: split?.rule?.pos,
          nullable: split?.rule?.nullable,
          regexpName: split?.rule?.regexpName,
          calConfig: translateFun(split.rule?.translate) || {},
        };
      }),
      logPattern: conf.logParse?.pattern?.logPattern,
    };
    if (conf?.logParse?.pattern?.logKnownPatterns) {
      editItem.logKnownPatterns = conf?.logParse?.pattern?.logKnownPatterns;
    }
    setEditItem(editItem);
    form.setFieldsValue(editItem);
  };
  const initD = (id: any) => {
    queryById(id).then((res) => {
      initDatasource(res);
    });
  };
  const urlList = [
    {
      name: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogMonitoring',
        dm: '日志监控',
      }),
      url: '/log',
    },

    {
      name: `${
        action === 'create'
          ? $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.CreateAConfiguration',
              dm: '创建配置',
            })
          : $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.EditConfiguration',
              dm: '编辑配置',
            })
      }`,
      url: `/log/metric/create/${parentId}`,
    },
  ];
  // 节流
  const onSubmit = () => {
    form.validateFields().then((values) => {
      const param = form.getFieldsValue(true);
      // console.log(JSON.stringify(param));

      const obj: any = {
        name: param.name,
        parentFolderId: parentId || -1,
        pluginType: param.pluginType || 'log',
        status: param.status ? 'ONLINE' : 'OFFLINE',
        periodType: param.periodType,
        sampleLog: param?.sampleLog?.selectValue,
        conf: {},
      };

      let newCondition: any = [];
      if (values.typeFilter === 'label') {
        let conditions: any = {};
        values.collectRanges.forEach((item: any) => {
          conditions[item.key] = item.value;
        });
        newCondition.push(conditions);
      } else {
        newCondition.push({
          [values.typeFilter]: values.collectRanges,
        });
      }
      const conf: any = {
        collectRanges: {
          table: param.tableName,
          condition: newCondition,
        },

        logPaths: param.logPath?.map((log: any) => {
          return {
            path: log.path,
            type: log.type,
            charset: log.charset || 'utf-8',
            agentLimitKB: log.agentLimitKB || -1,
          };
        }),
        extraConfig: param.keyCleanInterval
          ? {
              keyCleanInterval: param.keyCleanInterval,
              maxKeySize: param.maxKeySize,
            }
          : {
              maxKeySize: param.maxKeySize,
            },
        logParse: {
          splitType: param.splitType === 'NO' ? null : param.splitType,
          separator:
            param.splitType === 'SEP'
              ? {
                  separatorPoint: param?.splitFlag?.[0],
                }
              : null,
          regexp:
            param.splitType === 'REGEXP'
              ? {
                  expression: param?.splitRegexp?.trim(),
                }
              : null,
          multiLine: param.multiLine
            ? {
                multi: true,
                lineType: param.lineType,
                logRegexp: param.logRegexp,
              }
            : {
                multi: false,
              },
          pattern: {
            logPattern: values.logPattern ? true : false,
            maxUnknownPatterns: 50,
            maxLogLength: 300,
            logKnownPatterns: values.logKnownPatterns
              ? values.logKnownPatterns
              : [],
          },
        },

        whiteFilters:
          param.preFilterChecked &&
          param.preFilterTable &&
          param.preFilterTable.length > 0
            ? param.preFilterTable
                .filter((filter: any) => filter.operatorType === 'write')
                .map((filter: any) => {
                  return {
                    type: filter.filterType || 'LR',
                    rule: {
                      left: filter.left,
                      right: filter.right,
                      leftIndex: filter.leftIndex,
                      translate: filter.translate || {},
                    },

                    values: filter.values || [],
                  };
                })
            : [],
        blackFilters:
          param.preFilterChecked &&
          param.preFilterTable &&
          param.preFilterTable.length > 0
            ? param.preFilterTable
                .filter((filter: any) => filter.operatorType === 'black')
                .map((filter: any) => {
                  return {
                    type: filter.filterType || 'LR',
                    rule: {
                      left: filter.left,
                      right: filter.right,
                      leftIndex: filter.leftIndex,
                      translate: filter.translate || {},
                    },

                    values: filter.values || [],
                  };
                })
            : [],

        splitCols: param.colsSplit?.map((split: any) => {
          let arr = ['mapping', 'regexp', 'contains'];
          if (Array.isArray(split.calConfig.transforms)) {
            split.calConfig.transforms?.forEach((item: any) => {
              if (arr.includes(item.type) && item.mappings) {
                let obj: any = {};
                if (Array.isArray(item.mappings)) {
                  item.mappings?.forEach((value: any) => {
                    Object.assign(obj, { [value['key']]: value['value'] });
                  });
                }
                item.mappings = obj;
              } else {
                delete item['mappings'];
              }
            });
          }
          return {
            name: split.name,
            colType: split.dimType,
            rule: {
              left: split.left,
              right: split.right,
              leftIndex: split.leftIndex,
              pos: split.pos,
              nullable: split.nullable,
              regexpName: split.regexpName,
              defaultValue: split.calConfig?.defaultValue,
              translate: split.calConfig || {},
            },
          };
        }),
        collectMetrics: param.collectMetrics?.map((metric: any) => {
          const backObj: any = {
            metricType: param.logPattern ? 'count' : metric.metricType,
            containValue: metric.containsValue,
            tags: metric.tags,
            metrics: [
              {
                name: metric.metrics || 'value',
                func: param.logPattern ? 'loganalysis' : metric.func,
              },
            ],
            afterFilters: metric.afterFilters,
          };
          if (metric.name) {
            backObj.name = metric.tableName;
          } else {
            backObj.tableName = metric.tableName;
          }
          return backObj;
        }),
      };
      obj.conf = conf;
      setLoading(true);

      if (action === 'create') {
        create(obj)
          .then((res:any) => {
            message.success(
              $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.CreatedSuccessfully',
                dm: '创建成功',
              }),
            );
            initD(res.id);
            history.push(`/log/${parentId}`);
          })
          .finally(() => {
            setLoading(false);
          });
      } else if (action === 'edit') {
        obj.id = id;
        update(obj)
          .then(() => {
            // console.log(res);
            message.success(
              $i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.ModifiedSuccessfully',
                dm: '修改成功',
              }),
            );
            initD(id);
            history.push(`/log/${parentId}`);
          })
          .finally(() => {
            setLoading(false);
          });
      }
    });
  };

  const Tenant = async () => {
    const params = {
      tenant: initialState.currentTenant,
    };

    const res:any = await queryByTenant(params);
    const tableName = res?.[0].name;
    const param = {
      tableName,
      tenant:initialState.currentTenant,
    };

    const resquest:any = await queryAll(param);
    let tableData = resquest?.map((item: any) => {
      return {
        // ...item,
        key: `${item._uk}`,
        title: `${item.ip}`,
        desc: `${item.ip}(${item.hostname})`,
      };
    });
    setServerData(tableData);
    form.setFieldsValue({
      tableName,
    });
  };
  useEffect(() => {
    Tenant();
    if (action === 'edit') {
      initD(id);
    }
  }, [action, id]);

  // const handleChange = (changedValues: any) => {
  //   if (!isFormChange) setIsFormChange(true);
  //   if (changedValues.hasOwnProperty('colsSplit')) {
  //     console.log(changedValues)
  //   }
  // };

  return (
    <>
      {!notBead ? <CommonBreadcrumb urlList={urlList} /> : null}

      <div className={styles.container}>
        <PageHeader
          title={
            action === 'edit'
              ? $i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.EditConfiguration',
                  dm: '编辑配置',
                })
              : $i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.CreateAConfiguration',
                  dm: '创建配置',
                })
          }
        >
          <Form name="basic" autoComplete="off" form={form}>
            <BasicInfo
              serverData={serverData}
              form={form}
              logFilter={true}
              editItem={editItem}
              selectKey={selectKey}
              setSelectKey={setSelectKey}
            />

            <LogSegmentation
              serverData={serverData}
              form={form}
              edit={action === 'edit'}
              editItem={editItem}
            />

            <CollectMetric form={form} id={id} tenant={initialState.currentTenant} />

            <Form.Item>
              <Button
                type="primary"
                style={{ marginRight: 18, marginTop: 20 }}
                onClick={() => onSubmit()}
                loading={loading}
              >
                {/* 采集配置保存 */}
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.Save',
                  dm: '保存',
                })}
              </Button>

              <Button
                onClick={() => {
                  history.push('/log');
                }}
              >
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.Cancel',
                  dm: '取消',
                })}
              </Button>
            </Form.Item>
          </Form>
        </PageHeader>
      </div>
    </>
  );
};
export default LogMonitor;
