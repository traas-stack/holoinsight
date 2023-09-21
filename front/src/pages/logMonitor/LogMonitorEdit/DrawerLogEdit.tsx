import React, { useState, useEffect } from 'react';
import { Form, Drawer, Button } from 'antd';
import $i18n from '../../../i18n';
import LogSegmentation from './LogSegmentation';
import CollectMetric from './CollectMetric';
import { handleLRColumnValue } from './Common';
import BasicInfo from './BasicInfo';
import { queryByTenant, queryAll } from '@/services/customplugin/api';
import _ from 'lodash';
import { useModel } from 'umi';

interface IProps {
  value: any;
  action: any;
  visible: boolean;
  installed: boolean;
  logFilter: boolean | undefined;
  onSave: (p?: any, d?: string, i?:number) => void;
  onCancle: (p?: any, d?: any) => void;
  tableKey: number;
}

const DrawerLogEdit = (props: IProps) => {
  const { initialState } = useModel('@@initialState');
  const [form] = Form.useForm();
  const { value, visible, action, onSave, onCancle, logFilter,tableKey } = props;
  const [editItem, setEditItem] = useState<any>();
  const [selectKey, setSelectKey] = useState('');
  const [serverData, setServerData] = useState<any>([]);

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
    const backCollectRanges =
      seletIndex === 'label'
        ? conf?.collectRanges?.condition?.map((item: any) => {
            return {
              key: Object.keys(item)[0],
              value: Object.values(item)[0],
            };
          })
        : conf?.collectRanges?.condition?.[0]?.[seletIndex];
    setSelectKey(seletIndex);
    const editItem = {
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
          afterFilters: metric.afterFilters
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
      collectRanges: backCollectRanges,
      logPath: conf?.logPaths || [],
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
          calConfig: split?.translate || {},
        };
      }),
    };

    form.setFieldsValue(editItem);
    setEditItem(editItem);
  };
  const Tenant = async () => {
    const res:any = await queryByTenant({
      tenant: initialState.currentTenant,
    });
    const tableName = res?.[0].name;

    const resquest:any = await queryAll({
      tableName,
      tenant: initialState.currentTenant,
    });
    let tableData = resquest?.map((item: any) => {
      return {
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
      initDatasource(value);
    }
  }, [action]);

  const save = () => {
    form.validateFields().then((values) => {
      const param = form.getFieldsValue(true);
      const obj: any = {
        name: param.name,
        periodType: param.periodType,
        sampleLog: param?.sampleLog?.selectValue,
        conf: {},
      };

      const newCondition =
        values.typeFilter === 'label'
          ? values.collectRanges.map((item: any) => {
              return {
                [item.key]: item.value,
              };
            })
          : [
              {
                [values.typeFilter]: values.collectRanges,
              },
            ];

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
            metricType: metric.metricType,
            containValue: metric.containsValue,
            tags: metric.tags,
            metrics: [
              {
                name: metric.metrics || 'value',
                func: metric.func,
              },
            ],
            afterFilters:metric.afterFilters
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
      if (action === 'edit') {
        onSave(obj,'edit',tableKey);
      }else{
        onSave(obj,'add');
      }
    });
  };

  return (
    <Drawer
      title={$i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerLogEdit.DimensionDefinition',
        dm: '日志配置',
      })}
      width={1400}
      placement="right"
      open={visible}
      closable={false}
      onClose={() => {
        onCancle(false);
      }}
      footer={
        <div>
          <Button
            type="primary"
            style={{ marginRight: 18 }}
            onClick={() => {
              save();
            }}
          >
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerLogEdit.Save',
              dm: '保存',
            })}
          </Button>

          <Button
            onClick={() => {
              onCancle(false);
            }}
          >
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.DrawerLogEdit.Cancel',
              dm: '取消',
            })}
          </Button>
        </div>
      }
      footerStyle={{
        display: 'flex',
        justifyContent: 'flex-end',
      }}
    >
      <Form form={form}>
        {/* <DrawerBasicInfo
          serverData={serverData}
          form={form}
          logFilter = {logFilter}
          editItem={editItem}
          selectKey={selectKey}
          setSelectKey={setSelectKey}
        /> */}
        <BasicInfo
          serverData={serverData}
          form={form}
          logFilter = {logFilter}
          editItem={editItem}
          selectKey={selectKey}
          setSelectKey={setSelectKey}
        />

        <LogSegmentation 
          serverData={serverData} 
          form={form} 
          edit={action === 'edit'} 
          editItem={editItem || {}} 
        />

        <CollectMetric form={form} id={null} />
      </Form>
    </Drawer>
  );
};

export default DrawerLogEdit;
