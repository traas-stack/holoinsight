import React, { useEffect, useState } from 'react';

import {
  Card,
  Input,
  InputNumber,
  message,
  Modal,
  Radio,
  Row,
  Select,
  Table,
} from 'antd';

import $i18n from '../../../i18n';
import styles from './index.less';

interface TableItem {
  columnVal: string;
  count: number;
}

type Obj = {
  leftIndex: number;
  left: string;
  right: string;
  tableData: TableItem[];
  whiteFilters?: string[];
  values?: string[];
  operatorType?: string;
  filterType: string;
};

interface IProps {
  visible: boolean;
  setVisible: (p: boolean) => void;
  sampleLog: string;
  transferFunc: (p: any) => string;
  handleOk: (p: Obj) => void;
  columnType: string;
  type: number;
  field: Obj;
}

const LRModal = (props: IProps) => {
  const {
    visible,
    setVisible,
    sampleLog,
    transferFunc,
    handleOk,
    type,
    columnType,
    field,
  } = props;
  const [leftIndex, setLeftIndex] = useState(0);
  const [left, setLeft] = useState('');
  const [right, setRight] = useState('');
  const [tableData, setTableData] = useState<any[]>([]);
  const [whiteFilters, setWhiteFilters] = useState<any[]>([]);
  const [whiteType, setWhiteType] = useState('');
  const [filterType, setFilterType] = useState('');
  const handleOnMouseUp = () => {
    const sel: any = window.getSelection();
    if (!sel) return;
    const logs = sampleLog || '';
    const text = `${sel}` || '';
    if (logs && text) {
      const range = sel.getRangeAt(0);
      const { startOffset: i } = range; // 选中内容起始位置
      const left = logs[i - 1];
      setLeft(i === 0 ? '' : left);
      setRight(logs[i + text.length] || '');
      const str = logs.substring(0, i); // left之前
      let index = str.indexOf(left); //
      let num = 0;
      while (index !== -1) {
        num++;
        index = str.indexOf(left, index + 1);
      }
      if (i === 0) {
        num = 0;
      }
      const obj = {
        columnVal: text.replace(/ /g, '\u0001'),
        count: logs.split(text).length - 1 || 1,
      };

      setLeftIndex(num === 0 ? 0 : num - 1); // ? 如果选中的是0下标开始
      setTableData([obj]);
    }
  };

  const titleEle = (text: string, num: number) => {
    return <span style={{ fontWeight: 600, fontSize: num }}>{text}</span>;
  };

  useEffect(() => {
    if (visible && columnType === 'CREATE') {
      setLeftIndex(0);
      setLeft('');
      setRight('');
      setTableData([]);
      if (type === 2) {
        setWhiteType('write');
        setFilterType('CONTAINS');
      }
    } else if (visible && columnType === 'EDIT') {
      const {
        leftIndex,
        left,
        right,
        tableData,
        values = [],
        operatorType = 'write',
        filterType,
      } = field;
      setLeft(left);
      setLeftIndex(leftIndex);
      setRight(right);
      if (tableData) {
        setTableData([tableData]);
      }
      if (type === 2) {
        setWhiteFilters([...values]);
        setWhiteType(operatorType);
        setFilterType(filterType);
      }
    } else if (!visible && type === 2) {
      setWhiteFilters([]);
    }
  }, [type, columnType, visible, field]);

  return (
    <Modal
      title={titleEle(
        columnType === 'CREATE'
          ? $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.AddColumnDefinitions',
              dm: '添加列定义',
            })
          : $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.EditColumnDefinitions',
              dm: '编辑列定义',
            }),
        16,
      )}
      open={visible}
      onOk={() => {
        if (
          (!tableData.length || tableData[0].columnVal === ' ') &&
          filterType !== 'CONTAINS'
        ) {
          message.error('列值选取规则未填写');
          return;
        } else if (type === 2 && !whiteFilters.length) {
          message.error(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.TheWhitelistValueIsNot',
              dm: '白名单值未填写',
            }),
          );
          return;
        }
        handleOk({
          leftIndex,
          left,
          right,
          tableData,
          whiteFilters,
          operatorType: whiteType,
          filterType,
        });
      }}
      onCancel={() => {
        if (type === 2) {
          setWhiteFilters([]);
        }
        setVisible(false);
      }}
      destroyOnClose
      width={750}
      // okButtonProps={{
      //   disabled:
      //     (!tableData.length || tableData[0].columnVal === ' ') && filterType !== 'CONTAINS',
      // }}
      className={styles.columnModal}
    >
      <Card
        title={titleEle(
          $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.LogSample',
            dm: '日志样本',
          }),
          14,
        )}
      >
        <div className={styles.sampleBox} onMouseUp={handleOnMouseUp}>
          {sampleLog}
        </div>
      </Card>

      {type === 2 && (
        <Card
          title={titleEle(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.FilterRules',
              dm: '过滤规则',
            }),
            14,
          )}
          style={{ margin: '20px 0' }}
        >
          <Row>
            <Radio.Group
              onChange={(e: any) => {
                setFilterType(e.target.value);
                setWhiteFilters([]);
              }}
              value={filterType}
            >
              <Radio value={'CONTAINS'}>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.KeywordMatching',
                  dm: '关键字匹配',
                })}
              </Radio>

              <Radio value={'LR'}>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.FromLeftToRight',
                  dm: '左起右至',
                })}
              </Radio>
            </Radio.Group>
          </Row>
        </Card>
      )}

      {((type === 2 && filterType === 'LR') || type === 1) && (
        <Card
          title={titleEle(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.ColumnValueSelectionRules',
              dm: '列值选取规则',
            }),
            14,
          )}
          style={{ margin: '20px 0' }}
        >
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <span style={{ marginRight: 8 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.FromLeft',
                dm: '左起第',
              })}
            </span>

            <InputNumber
              style={{ width: 80 }}
              value={leftIndex}
              min={0}
              max={10000}
              onChange={(v) => {
                setLeftIndex(v);
                const text = transferFunc({ leftIndex: v + 1, left, right });
                const arr = [
                  {
                    columnVal: text,
                    count: sampleLog.split(text).length - 1 || 1,
                  },
                ];
                setTableData(text === '' ? [] : arr);
              }}
            />

            <span style={{ marginRight: 8, marginLeft: 8 }}>个</span>

            <Input
              style={{ width: 100 }}
              value={left}
              onChange={(e) => {
                const val = e.target.value;
                setLeft(val);
                if (val === '') {
                  setLeftIndex(0);
                }
                const text = transferFunc({
                  leftIndex: val === '' ? 0 : leftIndex + 1,
                  left: val,
                  right,
                });

                const arr = [
                  {
                    columnVal: text,
                    count: sampleLog.split(text).length - 1 || 1,
                  },
                ];
                setTableData(text === '' ? [] : arr);
              }}
            />

            <span style={{ marginRight: 8, marginLeft: 8 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.Right',
                dm: '右至',
              })}
            </span>

            <Input
              style={{ width: 100 }}
              value={right}
              onChange={(e) => {
                setRight(e.target.value);
                if (e.target.value === '' && left === '') {
                  setLeftIndex(0);
                }
                const text = transferFunc({
                  leftIndex: leftIndex + 1,
                  left,
                  right: e.target.value,
                });

                const arr = [
                  {
                    columnVal: text,
                    count: sampleLog.split(text).length - 1 || 1,
                  },
                ];
                setTableData(text === '' ? [] : arr);
              }}
            />
          </div>
        </Card>
      )}

      {type === 2 && (
        <Card
          title={titleEle(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.BlackAndWhiteList',
              dm: '黑白名单',
            }),
            14,
          )}
          style={{ marginBottom: 20 }}
        >
          {(filterType === 'LR' || filterType === 'CONTAINS') && (
            <Row>
              <Radio.Group
                onChange={(e: any) => {
                  setWhiteType(e.target.value);
                  setWhiteFilters([]);
                }}
                value={whiteType}
              >
                <Radio value={'write'}>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.CommonWhitelist',
                    dm: '通用白名单',
                  })}
                </Radio>

                {type === 2 && (
                  <Radio value={'black'}>
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.UniversalBlacklist',
                      dm: '通用黑名单',
                    })}
                  </Radio>
                )}
              </Radio.Group>
            </Row>
          )}

          {(filterType === 'LR' || filterType === 'CONTAINS') && (
            <Select
              mode="tags"
              placeholder={
                whiteType === 'write'
                  ? $i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.EnterAWhitelistValue',
                      dm: '输入白名单值',
                    })
                  : $i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.EnterABlacklistValue',
                      dm: '输入黑名单值',
                    })
              }
              open={false}
              value={whiteFilters}
              style={{ width: 420, display: 'block', marginTop: 15 }}
              onChange={(val) => {
                setWhiteFilters(val);
              }}
            />
          )}

          {/* {filterType === 'CONTAINS' && (
            <Input
              value={whiteFilters?.[0]}
              style={{ width: 420, display: 'block', marginTop: 15 }}
              onChange={(v) => {
                setWhiteFilters([v.target.value]);
              }}
            />
          )} */}
        </Card>
      )}

      <div style={{ marginBottom: 10, fontWeight: 600, fontSize: 14 }}>
        {$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.ColumnValueSampling',
          dm: '列值采样',
        })}
      </div>

      <Table pagination={false} dataSource={tableData}>
        <Table.Column
          title={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.ColumnValue',
            dm: '列值',
          })}
          dataIndex="columnVal"
          key="columnVal"
          width="85%"
          render={(text: any) => {
            return (
              <div style={{ width: 580 }}>{text.replace(/ /g, '\u0001')}</div>
            );
          }}
        />

        <Table.Column
          title={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LRModal.NumberOfOccurrences',
            dm: '出现次数',
          })}
          dataIndex="count"
          key="count"
          width="15%"
        />
      </Table>
    </Modal>
  );
};

export default LRModal;
