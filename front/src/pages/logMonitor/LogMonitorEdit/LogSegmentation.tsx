import LogSource from '@/components/CommonLogSource';
import { previewFile } from '@/services/agent/api';
import { splitReq } from '@/services/customplugin/api';
import {
  Button,
  Card,
  Col,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Select,
  Space,
  Spin,
  Switch,
  Table,
  Tag,
  Tooltip,
} from 'antd';
import _ from 'lodash';
import {
  CloseOutlined,
  EditOutlined,
  InfoCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { useEffect, useState } from 'react';
import $i18n from '../../../i18n';
import { handleLRColumnValue } from './Common';
import LogKnow from './logKnow';
import LogLoad from './LogLoad';
import styles from './LogSegmentation.less';
import LRModal from './LRModal';
import ModelComponent from './ModelItem';
interface IProps {
  form: any;
  edit: any;
  editItem: any;
  serverData: any;
}

type Params = {
  leftIndex: number;
  left: string;
  right: string;
  tableData: any[];
  whiteFilters?: string[];
  operatorType?: string;
  filterType?: string;
};

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 18 },
};

const LogSegmentation = (props: IProps) => {
  const { form, edit, editItem, serverData } = props;
  const [splitType, setSplitType] = useState<any>();

  const [index1, setIndex] = useState<number>(0);
  const [names, setNames] = useState<any[]>([]);

  const [columnType, setColumnType] = useState('CREATE');
  const [showLRModal, setShowLRModal] = useState(false);

  const [splitVal, setSplitVal] = useState<any>();

  const [logType, setLogType] = useState(1);
  const [visible, setVisible] = useState(false); // 抽屉
  const [currentField, setCurrentField] = useState<any>();
  const [sliType, setSliType] = useState(1); // 1: LR切分，2：前置过滤
  const [lrTable, setLrTable] = useState<any[]>([]); // LR切分结果表格
  const [LRIndex, setLRIndex] = useState(0);
  const [splitTable, setSplitTable] = useState<any[]>([]); // 分隔符切分结果表格

  const [mulitLine, setMultiLine] = useState(false);
  const [preFilterChecked, setPreFilterChecked] = useState(false);
  const [LR_Params, setParams] = useState<any>();
  const [PropName, setName] = useState<string>(); // 编辑
  const [open, setOpen] = useState(false);
  const [counter, setCounter] = useState<number>(0);
  const [textOpen, settextOpen] = useState<boolean>(false);
  const [btnOpen, setBtnOpen] = useState<boolean>(false);
  const [logReLoading, setLogReLoading] = useState<boolean>(false);
  const [sampleLog, setSampleLog] = useState(
    form.getFieldValue('sampleLog')?.selectValue,
  );

  const initLR = (flag: boolean, cols?: any, sampleLog?: string) => {
    const splitList: any[] = [];
    const list = cols || form.getFieldValue('colsSplit');
    list.forEach((item: any, i: number) => {
      const { left, right, leftIndex, name, nullable } = item;
      splitList[i] = {};
      splitList[i].pos = i;
      splitList[i].columnValue = handleLRColumnValue(
        {
          left,
          right,
          leftIndex: left === '' ? 0 : leftIndex + 1,
        },

        sampleLog,
      );
      splitList[i].names = [{ name, nullable }];
      splitList[i].left = left;
      splitList[i].right = right;
      splitList[i].leftIndex = leftIndex;
      splitList[i].hash = left + right + leftIndex;
    });
    const newArr: any[] = [];
    splitList.forEach((item) => {
      const index = newArr.map((v) => v.hash).indexOf(item.hash);
      if (index === -1) {
        newArr.push(item);
      } else {
        newArr[index].names.push(item.names[0]);
      }
    });
    if (flag) {
      setLrTable(newArr);
      return;
    }
    if (newArr.length && !lrTable.length) {
      setLrTable(newArr);
    } else {
      setLrTable([...lrTable]);
    }
  };
  const initSplit = (
    type: string,
    cols: any,
    sampleLog: string,
    common?: any,
  ) => {
    if (type === 'NO') {
      return;
    }
    if (type === 'LR') {
      initLR(false, cols, sampleLog);
      return;
    }
    const params: any = {};
    params.sampleLogs = [sampleLog];
    params.splitType = type;
    if (type === 'SEP' || type === 'REGEXP') {
      params.seperators = common.replace(/\u0001/g, ' ');
    } else {
      params.script = common;
    }

    splitReq(params).then((res: any) => {
      if (!res || !res.length) {
        Modal.warning({
          title: $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheSplitResultIsEmpty',
            dm: '切分结果为空, 请仔细检查',
          }),
        });
        return;
      }
      const data = res[0];
      const splitList: any[] = [];
      data?.forEach((item: any, i: number) => {
        splitList[i] = {};
        splitList[i].pos = i;
        splitList[i].columnValue =
          item && item.length > 3000 ? item.slice(0, 3000) : item;
        splitList[i].names = cols
          .filter((v: any) => v.pos === i)
          .map((it: any) => {
            return { name: it.name, nullable: it.nullable };
          });
      });
      if (type === 'SEP' || type === 'REGEXP') {
        setSplitTable(splitList);
      }
    });
  };
  useEffect(() => {
    if (
      editItem &&
      editItem.collectRanges &&
      Object.keys(editItem.collectRanges).length > 0
    ) {
      if (editItem.splitType) {
        setSplitType(editItem.splitType);
      }
      if (editItem.preFilterChecked) {
        setPreFilterChecked(editItem.preFilterChecked);
      }
      if (editItem.multiLine) {
        setMultiLine(editItem.multiLine);
      }
      if (editItem.colsSplit) {
        initSplit(
          editItem.splitType,
          editItem.colsSplit,
          editItem.sampleLog.selectValue,
          editItem.splitFlag[0] || editItem.splitRegexp,
        );
      }
      if (editItem.splitFlag) {
        setSplitVal(editItem.splitFlag);
      }
    }
  }, [editItem]);

  // flag: 正常切分为true, 日志样本改变时为false
  const handleSplit = (flag: boolean, info?: any): any => {
    const samples = form.getFieldValue('sampleLog')?.selectValue;
    if (flag && samples === '') {
      message.warning(
        $i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheLogCannotBeEmpty',
          dm: '日志不能为空',
        }),
      );
      return;
    }
    const sliptType = form.getFieldValue('splitType');
    if (sliptType === 'SEP') {
      let content = form.getFieldValue('splitFlag');
      content = content.map((i: string) => i.replace(/\u0001/g, ' '));
      if (!flag && !content.length) return;
      if (!content.length) {
        message.warning(
          $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheDelimiterCannotBeEmpty',
            dm: '分隔符内容不能为空',
          }),
        );
        return;
      }
      splitReq({
        splitType: 'SEP',
        seperators: content.join('|'),
        sampleLogs: [samples],
      }).then((res) => {
        if (!res || !res.length) {
          Modal.warning({
            title: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheSplitResultIsEmpty',
              dm: '切分结果为空, 请仔细检查',
            }),
          });
          return;
        }
        const data = res[0];
        const splitList: any[] = [];
        const cols = form.getFieldValue('colsSplit') || [];
        if (
          data?.length < splitTable?.length &&
          !!cols?.length &&
          data.length < cols?.length
        ) {
          const newCols: any[] = [];
          const splitCols: any[] = [];
          cols?.forEach((col: any) => {
            if (col.pos < data.length) {
              newCols.push(col);
            } else {
              splitCols.push(col);
            }
          });
          Modal.confirm({
            title: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheSplittingRuleChangesDimension',
              dm: '切分规则变化，超过切分长度的维度配置会被自动删除',
            }),
            content: (
              <div>
                <span>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnConfiguration',
                    dm: '列配置',
                  })}
                </span>

                {splitCols.map((i: any) => (
                  <span style={{ color: '#f00' }}>{i.name} </span>
                ))}

                <span>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.WillBeDeleted',
                    dm: '将被删除',
                  })}
                </span>
              </div>
            ),

            onOk: () => {
              data.forEach((item: any, i: number) => {
                splitList[i] = {};
                splitList[i].pos = i;
                splitList[i].columnValue = item;
                splitList[i].names = newCols
                  .filter((v: any) => v.pos === i)
                  .map((it: any) => {
                    return { name: it.name, nullable: it.nullable };
                  });
              });
              form.setFieldsValue({ colsSplit: newCols });
              setSplitTable([...splitList]);
            },
            closable: true,
            okText: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Ok',
              dm: '确定',
            }),
            cancelText: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Cancel',
              dm: '取消',
            }),
            width: 500,
          });
        } else {
          data?.forEach((item: any, i: number) => {
            splitList[i] = {};
            splitList[i].pos = i;
            splitList[i].columnValue = item;
            splitList[i].names = cols
              .filter((v: any) => v.pos === i)
              .map((it: any) => {
                return { name: it.name, nullable: it.nullable };
              });
          });
          setSplitTable([...splitList]);
        }
      });
    } else if (sliptType === 'LR' && flag) {
      const list: any[] = [...lrTable];
      const { leftIndex, left, right, tableData } = info;
      const flag1 = list.some(
        (item) =>
          item.left === left &&
          item.right === right &&
          item.leftIndex === leftIndex,
      );

      if (flag1) {
        message.warning(
          $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheColumnAlreadyExists',
            dm: '列已经存在',
          }),
        );
        return;
      }
      const posList = list.reduce((arr, cur) => {
        arr.push(cur.pos);
        return arr;
      }, []);
      let max = -1;
      if (posList.length) {
        max = Math.max(...posList);
      }
      list.push({
        left,
        right,
        leftIndex,
        columnValue: tableData[0]?.columnVal,
        pos: max + 1,
        names: [],
        hash: left + right + leftIndex,
      });

      setLrTable(list);
      setShowLRModal(false);
    } else if (sliptType === 'REGEXP') {
      const content = form.getFieldValue('splitRegexp');
      if (!content) {
        message.warning(
          $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ExpressionContentCannotBeEmpty',
            dm: 'expression内容不能为空',
          }),
        );
        return;
      }
      splitReq({
        splitType: 'REGEXP',
        seperators: content,
        sampleLogs: [samples],
      }).then((res) => {
        if (!res || !res.length) {
          Modal.warning({
            title: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheSplitResultIsEmpty',
              dm: '切分结果为空, 请仔细检查',
            }),
          });
          return;
        }
        const data = res[0];
        const splitList: any[] = [];
        const cols = form.getFieldValue('colsSplit') || [];
        if (
          data.length < splitTable.length &&
          !!cols?.length &&
          data.length < cols?.length
        ) {
          const newCols: any[] = [];
          const splitCols: any[] = [];
          cols?.forEach((col: any) => {
            if (col.pos < data.length) {
              newCols.push(col);
            } else {
              splitCols.push(col);
            }
          });
          Modal.confirm({
            title: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheSplittingRuleChangesDimension',
              dm: '切分规则变化，超过切分长度的维度配置会被自动删除',
            }),
            content: (
              <div>
                <span>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnConfiguration',
                    dm: '列配置',
                  })}
                </span>

                {splitCols.map((i: any) => (
                  <span style={{ color: '#f00' }}>{i.name} </span>
                ))}

                <span>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.WillBeDeleted',
                    dm: '将被删除',
                  })}
                </span>
              </div>
            ),

            onOk: () => {
              data.forEach((item: any, i: number) => {
                splitList[i] = {};
                splitList[i].pos = i;
                splitList[i].columnValue = item;
                splitList[i].names = newCols
                  .filter((v: any) => v.pos === i)
                  .map((it: any) => {
                    return { name: it.name, nullable: it.nullable };
                  });
              });
              form.setFieldsValue({ colsSplit: newCols });
              setSplitTable([...splitList]);
            },
            closable: true,
            okText: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Ok',
              dm: '确定',
            }),
            cancelText: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Cancel',
              dm: '取消',
            }),
            width: 500,
          });
        } else {
          data.forEach((item: any, i: number) => {
            splitList[i] = {};
            splitList[i].pos = i;
            splitList[i].columnValue = item;
            splitList[i].names = cols
              .filter((v: any) => v.pos === i)
              .map((it: any) => {
                return { name: it.name, nullable: it.nullable };
              });
          });
          setSplitTable([...splitList]);
        }
      });
    }
  };

  // 列配置 新增 & 编辑
  const handleAddOrEdit = (i: number, name?: string) => {
    if (form.getFieldValue('splitType') === 'LR') {
      setParams({
        left: lrTable[i].left,
        right: lrTable[i].right,
        leftIndex: lrTable[i].leftIndex,
      });
    } else {
      setParams(null);
    }
    setName(name);
    setIndex(i);
    setOpen(true);
  };

  const handleClose = (i: number, name: string) => {
    if (form.getFieldValue('splitType') === 'SEP') {
      const list = form.getFieldValue('colsSplit');
      const arr = JSON.parse(JSON.stringify(splitTable));
      // arr.forEach((item: any) => {
      //   if (item.names?.includes(name)) {
      //     item.names = item.names.filter((v: any) => v !== name);
      //   }
      // });
      arr[i].names = arr[i].names.filter((v: any) => v.name !== name);
      setSplitTable(arr);
      form.setFieldsValue({
        colsSplit: list.filter((v: any) => v.name !== name),
      });
    } else {
      let list: any[] = form.getFieldValue('colsSplit');
      const arr = JSON.parse(JSON.stringify(lrTable));
      arr.forEach((item: any) => {
        if (item.names?.includes(name)) {
          item.names = [...item.names].filter((v: any) => v !== name);
        }
      });
      setLrTable(arr);
      list = list.filter((v: any) => v.name !== name);
      form.setFieldsValue({
        colsSplit: list,
      });

      setCounter(counter + 1);
    }
  };

  // 切分表格 删除
  const handleDelete = (index: number, record: any) => {
    const colsSplit = [...form.getFieldValue('colsSplit')].filter(
      (v) =>
        !(
          v.left === record.left &&
          v.right === record.right &&
          v.leftIndex === record.leftIndex
        ),
    );
    if (form.getFieldValue('splitType') === 'REGEXP') {
      const arr = [...splitTable];
      arr.splice(index, 1);
      setSplitTable([...arr]);
    } else {
      const arr = [...lrTable];
      arr.splice(index, 1);
      setLrTable([...arr]);
    }
    form.setFieldsValue({ colsSplit });
    setCounter(counter + 1);
  };

  // 拉取日志成功时，清除已切分内容
  const resetSplitResult = () => {
    handleSplit(false);
    if (form.getFieldValue('splitType') === 'LR') {
      initLR(true, null, form.getFieldValue('sampleLog').selectValue);
    }
    const arr = form.getFieldValue('preFilterTable') || [];
    arr?.forEach((i: any) => {
      const { right, left } = i;
      let { leftIndex } = i;
      leftIndex = left === '' ? 0 : leftIndex + 1;
      i.columnValue =
        handleLRColumnValue(
          { right, left, leftIndex },
          form.getFieldValue('sampleLog').selectValue,
        ) || ' ';
    });
    // setPreFilterTable(arr);
    form.setFieldsValue({ preFilterTable: arr });
  };

  useEffect(() => {
    resetSplitResult();
  }, [sampleLog]);

  // 前置处理 切分
  const handleSplitPre = (obj: Params) => {
    const {
      leftIndex,
      left,
      right,
      tableData,
      whiteFilters,
      operatorType,
      filterType,
    } = obj;
    const list: any[] = form.getFieldValue('preFilterTable');
    // const flag = list.some(
    //   (item) =>
    //     item.left === left &&
    //     item.right === right &&
    //     item.leftIndex === leftIndex,
    // );

    // if (flag) {
    //   message.warning(
    //     $i18n.get({
    //       id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheColumnAlreadyExists',
    //       dm: '列已经存在',
    //     }),
    //   );
    //   return;
    // }
    const hash_ = left + right + leftIndex;
    const item: any = {
      left,
      right,
      leftIndex,
      columnValue:
        filterType === 'CONTAINS' ? whiteFilters?.[0] : tableData[0].columnVal,
      pos: list.length,
      name: `field${hash_}`,
      hash: hash_,
      values: whiteFilters,
      type: '',
      operatorType,
      filterType,
    };

    list.push(item);
    form.setFieldsValue({ preFilterTable: list });
    setShowLRModal(false);
  };

  // 反转义
  const _reverseEscape = (str: string) => {
    const arrEntities = { lt: '<', gt: '>', nbsp: ' ', amp: '&', quot: '"' };
    return str.replace(/&(lt|gt|nbsp|amp|quot);/gi, (all, t) => arrEntities[t]);
  };

  // 日志拉取
  const reloadLog = () => {
    const logSouceArr = Array.isArray(form.getFieldValue('logPath'))
      ? form.getFieldValue('logPath').filter((item: any) => item.path)
      : [];

    const uk =
      form.getFieldValue('preHost') ||
      form.getFieldValue('collectRanges')?.[0] ||
      '';
    let preHost;
    serverData.forEach((ele: any) => {
      if (ele.key === uk) {
        preHost = ele.title;
      }
    });

    const param = { logpath: logSouceArr?.[0]?.path };
    if (
      form.getFieldValue('typeFilter') === 'app' &&
      (_.isUndefined(form.getFieldValue('preHost')) ||
        form.getFieldValue('preHost') === '')
    ) {
      param['app'] = form.getFieldValue('collectRanges')?.[0];
    } else if (
      form.getFieldValue('typeFilter') === 'label' &&
      (_.isUndefined(form.getFieldValue('preHost')) ||
        form.getFieldValue('preHost') === '')
    ) {
      const labelMap = {};
      _.forEach(form.getFieldValue('collectRanges'), (item: any) => {
        labelMap[item.key] = item.value?.[0];
      });
      param['label'] = labelMap;
    } else {
      param['ip'] = preHost;
    }
    setLogReLoading(true);
    previewFile(param)
      .then((res) => {
        if (!res || !res.lines) {
          return;
        }
        const sampleLog = {
          value: res.lines.map((ele: any) => {
            return _reverseEscape(ele);
          }),
          selectValue: res?.lines?.[0] ? _reverseEscape(res?.lines?.[0]) : '',
        };
        form.setFieldsValue({ sampleLog });
        setLogType(1);
        resetSplitResult();
      })
      .finally(() => {
        setLogReLoading(false);
      });
  };

  // 列定义完成回调
  const handleOk = (obj: any) => {
    const {
      leftIndex,
      left,
      right,
      tableData,
      whiteFilters,
      operatorType,
      filterType,
    } = obj;
    if (!tableData.length && filterType !== 'CONTAINS') {
      message.warning(
        $i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.PleaseSplitTheContentCorrectly',
          dm: '请正确切分内容',
        }),
      );
      return;
    }
    // 1 LR切分 2 前置过滤
    if (sliType === 1 && columnType === 'CREATE') {
      handleSplit(true, obj);
    } else if (sliType === 1 && columnType === 'EDIT') {
      const list = [...lrTable];
      const li = [...list];
      li.splice(LRIndex, 1);
      for (let i = 0; i < li.length; i++) {
        if (
          li[i].left === left &&
          li[i].right === right &&
          li[i].leftIndex === leftIndex
        ) {
          message.warning(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheColumnAlreadyExists',
              dm: '列已经存在',
            }),
          );
          return;
        }
      }
      list[LRIndex] = {
        ...list[LRIndex],
        ...{
          left,
          right,
          leftIndex,
          columnValue: tableData[0].columnVal,
          pos: index1,
          hash: left + right + leftIndex,
        },
      };

      const cols = JSON.parse(JSON.stringify(form.getFieldValue('colsSplit')));
      cols.forEach((i: any) => {
        if (names?.length && names.includes(i.name)) {
          i.left = left;
          i.right = right;
          i.leftIndex = leftIndex;
        }
      });
      setLrTable(list);
      form.setFieldsValue({ colsSplit: cols });
      setShowLRModal(false);
    } else if (sliType === 2 && columnType === 'EDIT') {
      const list = form.getFieldValue('preFilterTable');
      const li = [...list];
      li.splice(LRIndex, 1);
      for (let i = 0; i < li.length; i++) {
        if (
          li[i].left === left &&
          li[i].right === right &&
          li[i].leftIndex === leftIndex
        ) {
          message.warning(
            $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.TheColumnAlreadyExists',
              dm: '列已经存在',
            }),
          );
          return;
        }
      }
      const obj1: any = {
        left,
        right,
        leftIndex,
        columnValue:
          filterType === 'CONTAINS'
            ? whiteFilters?.[0]
            : tableData[0].columnVal,
        hash: left + right + leftIndex,
        values: whiteFilters,
        operatorType,
      };

      list[LRIndex] = {
        ...list[LRIndex],
        ...obj1,
      };

      form.setFieldsValue({ preFilterTable: list });
      setShowLRModal(false);
    } else if (sliType === 2 && columnType === 'CREATE') {
      handleSplitPre(obj);
    }
  };

  // 获取切分表格数据
  const getColumnData = () => {
    if (form.getFieldValue('splitType') === 'SEP') {
      return splitTable;
    }
    if (form.getFieldValue('splitType') === 'REGEXP') {
      return splitTable;
    }
    if (form.getFieldValue('splitType') === 'LR') {
      return lrTable;
    }

    return lrTable;
  };

  // 获取切分表格数据 切分方法
  const getUpdateTable = () => {
    if (form.getFieldValue('splitType') === 'SEP') {
      return setSplitTable;
    }
    if (form.getFieldValue('splitType') === 'REGEXP') {
      return setSplitTable;
    }
    if (form.getFieldValue('splitType') === 'LR') {
      return setLrTable;
    }

    return setLrTable;
  };

  const filterField = (v: any) => {
    if (v === null) return 'null';
    return v?.toString().replace(/ /g, '\u0001');
  };

  // 前置过滤 切分表格 删除
  const handlePreConfigDelete = (index: number) => {
    const tables = form.getFieldValue('preFilterTable') || [];
    tables.splice(index, 1);
    form.setFieldsValue({ preFilterTable: tables });
    setCounter(counter + 1);
  };
  // 前置过滤 切分表格 列配置
  const preFilterColumns = [
    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnValue',
        dm: '列值',
      }),
      dataIndex: 'columnValue',
      key: 'columnValue',
      width: 280,
      render: (field: string, record: any, index: number) => {
        return (
          <div style={{ maxWidth: 200 }}>
            <span
              style={{
                cursor: 'pointer',
                color: '#1890ff',
                textDecoration: 'underline',
              }}
              onClick={() => {
                const {
                  leftIndex,
                  left,
                  right,
                  values,
                  operatorType,
                  filterType,
                } = record;
                const logs = form.getFieldValue('sampleLog').selectValue;
                const obj = {
                  leftIndex,
                  left,
                  right,
                  tableData: {
                    columnVal: field,
                    count: logs.split(field).length - 1 || 1,
                  },

                  values,
                  operatorType,
                  filterType,
                };

                setCurrentField(obj);
                setSliType(2);
                setColumnType('EDIT');
                setLRIndex(index);
                setShowLRModal(true);
              }}
            >
              {filterField(field)}
            </span>
          </div>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.FilterType',
        dm: '过滤类型',
      }),
      key: 'filterType',
      dataIndex: 'filterType',
      textAlign: 'right',
      width: 80,
      render: (text: any) => {
        if (text === 'LR') {
          return (
            <Tag style={{ marginBottom: 5 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.FromLeftToRight',
                dm: '左起右至',
              })}
            </Tag>
          );
        } else if (text === 'CONTAINS') {
          return (
            <Tag style={{ marginBottom: 5 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.KeywordMatching',
                dm: '关键字匹配',
              })}
            </Tag>
          );
        }
        return <Tag style={{ marginBottom: 5 }}></Tag>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Type',
        dm: '类型',
      }),
      key: 'operatorType',
      dataIndex: 'operatorType',
      textAlign: 'right',
      width: 80,
      render: (text: any) => {
        if (text === 'black') {
          return (
            <Tag style={{ marginBottom: 5 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Blacklist',
                dm: '黑名单',
              })}
            </Tag>
          );
        }
        return (
          <Tag style={{ marginBottom: 5 }}>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Whitelist',
              dm: '白名单',
            })}
          </Tag>
        );
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Value',
        dm: '值',
      }),
      key: 'values',
      dataIndex: 'values',
      // textAlign: 'right',
      width: 280,
      render: (text: any[]) => {
        return text?.map((item) => (
          <span
            style={{
              maxWidth: 200,
              cursor: 'pointer',
              color: '#60db4e',
            }}
          >
            {item}
          </span>
        ));
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Operation',
        dm: '操作',
      }),
      key: 'action',
      dataIndex: 'action',
      width: 80,
      render: (field: any, record: any, index: number) => {
        return (
          <Popconfirm
            title={$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AreYouSureYouWant',
              dm: '确定删除？',
            })}
            onConfirm={() => {
              handlePreConfigDelete(index);
            }}
          >
            <a>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Delete',
                dm: '删除',
              })}
            </a>
          </Popconfirm>
        );
      },
    },
  ];

  const splitColumns = [
    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnNumber',
        dm: '列号',
      }),
      dataIndex: 'pos',
      key: 'pos',
      width: 60,
      render: (text: any, record: any, index: number) => `${index}`,
    },

    {
      title: (
        <span>
          {$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnValue',
            dm: '列值',
          })}
        </span>
      ),
      dataIndex: 'columnValue',
      key: 'columnValue',
      width: 280,
      render: (field: string, record: any, index: number) => {
        if (form.getFieldValue('splitType') === 'LR') {
          return (
            <div style={{ maxWidth: 500 }}>
              <span
                style={{
                  cursor: 'pointer',
                  color: '#1890ff',
                  textDecoration: 'underline',
                }}
                onClick={() => {
                  const { leftIndex, left, right } = record;
                  const logs = form.getFieldValue('sampleLog').selectValue;
                  const obj = {
                    leftIndex,
                    left,
                    right,
                    tableData: {
                      columnVal: field,
                      count: logs.split(field).length - 1 || 1,
                    },
                  };

                  const newN = record.names.map((i: any) => i.name);
                  setCurrentField(obj);
                  setColumnType('EDIT');
                  setSliType(1);
                  // setNames(record.names);
                  setNames(newN);
                  setLRIndex(index);
                  setShowLRModal(true);
                }}
              >
                {filterField(field)}
              </span>
            </div>
          );
        }
        return <div style={{ maxWidth: 500 }}>{filterField(field)}</div>;
      },
    },

    {
      title: $i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ColumnConfiguration',
        dm: '列配置',
      }),
      key: 'names',
      dataIndex: 'names',
      textAlign: 'right',
      render: (field: any[], record: any, index: number) => {
        return (
          <>
            {field?.map((item) => (
              <Tag
                className={styles.logTag}
                key={item}
                closable
                closeIcon={
                  <Popconfirm
                    title={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AreYouSureYouWant',
                      dm: '确定删除？',
                    })}
                    onConfirm={() => {
                      handleClose(index, item.name);
                    }}
                    onCancel={(e: any) => {
                      e.preventDefault();
                    }}
                  >
                    <CloseOutlined
                    // style={{
                    //   color: item.nullable
                    //     ? '#52c41a'
                    //     : 'rgba(0, 0, 0, 0.45)',
                    // }}
                    />
                  </Popconfirm>
                }
                style={{
                  marginBottom: 5,
                }}
                color={item.nullable ? 'success' : 'default'}
              >
                <span
                  style={{
                    display: 'inline-block',
                    maxWidth: 100,
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    textOverflow: 'ellipsis',
                    wordBreak: 'break-all',
                    float: 'left',
                  }}
                  title={item.name}
                >
                  {item.name}
                </span>

                <EditOutlined
                  style={{
                    fontSize: 10,
                    marginLeft: 8,
                    // color: item.nullable ? '#52c41a' : 'rgba(0, 0, 0, 0.45)',
                  }}
                  onClick={() => {
                    handleAddOrEdit(index, item.name);
                  }}
                />
              </Tag>
            ))}

            <a
              onClick={() => {
                handleAddOrEdit(index);
              }}
            >
              <Tag>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Add',
                  dm: '+ 新增',
                })}
              </Tag>
            </a>
          </>
        );
      },
    },

    form.getFieldValue('splitType') === 'LR'
      ? {
          title: $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Operation',
            dm: '操作',
          }),
          key: 'action',
          dataIndex: 'action',
          width: 80,
          render: (field: any, record: any, index: number) => {
            return (
              <Popconfirm
                title={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AreYouSureYouWant',
                  dm: '确定删除？',
                })}
                onConfirm={() => {
                  handleDelete(index, record);
                }}
              >
                <a>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Delete',
                    dm: '删除',
                  })}
                </a>
              </Popconfirm>
            );
          },
        }
      : {},
    form.getFieldValue('splitType') === 'REGEXP'
      ? {
          title: $i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Operation',
            dm: '操作',
          }),
          key: 'action',
          dataIndex: 'action',
          width: 80,
          render: (field: any, record: any, index: number) => {
            return (
              <Popconfirm
                title={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AreYouSureYouWant',
                  dm: '确定删除？',
                })}
                onConfirm={() => {
                  handleDelete(index, record);
                }}
              >
                <a>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Delete',
                    dm: '删除',
                  })}
                </a>
              </Popconfirm>
            );
          },
        }
      : {},
  ];

  const textOpend = (textBlooean: boolean) => {
    settextOpen(textBlooean);
  };

  return (
    <>
      <Card
        title={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.LogExtraction',
          dm: '日志提取',
        })}
        style={{ marginTop: '20px' }}
      >
        <Row>
          <Col className={styles.colLeft} span={7}>
            <Card
              className={styles.logCard}
              title={
                logType
                  ? $i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.OnlineLogs',
                      dm: '线上日志',
                    })
                  : $i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ManualInput',
                      dm: '人工输入',
                    })
              }
              extra={
                <Space>
                  <Tooltip
                    placement="top"
                    title={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.PullOnlineLogsAgain',
                      dm: '重新拉取线上日志',
                    })}
                  >
                    <a>
                      <Spin spinning={logReLoading}>
                        <ReloadOutlined onClick={() => reloadLog()} />
                      </Spin>
                    </a>
                  </Tooltip>

                  <Tooltip
                    placement="top"
                    title={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.EditLogSource',
                      dm: '编辑日志来源',
                    })}
                  >
                    <a>
                      <EditOutlined
                        onClick={() => {
                          setVisible(true);
                        }}
                      />
                    </a>
                  </Tooltip>
                </Space>
              }
            >
              <Form.Item
                label={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.LogSampleSource',
                  dm: '日志样本来源',
                })}
                required
                name="sampleLog"
                labelCol={{ span: 24 }}
                tooltip={{
                  title: $i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.DragTheMouseToSelect',
                    dm: '鼠标拖动选择左起右至列值',
                  }),
                }}
              >
                {/* <>
                   <div style={{ wordBreak: 'break-word' }}>
                     {form.getFieldValue('sampleLog')}
                   </div>
                   <Input />
                   {form.getFieldValue('sampleLog') === '' && (
                     <div>暂无日志,请重新拉取或人工输入</div>
                   )}
                  </> */}

                <LogSource
                  setSampleLog={setSampleLog}
                  setBtnOpen={setBtnOpen}
                  textOpen={textOpen}
                />
              </Form.Item>
              {/* {btnOpen && form.getFieldValue('sampleLog').value.length > 3 ? (
                <>
                  <div style={{ display: 'flex', justifyContent: 'right' }}>
                    {textOpen ? (
                      <>
                        <Button type="link" onClick={() => textOpend(false)}>
                          {$i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.collapse',
                            dm: '收起',
                          })}
                        </Button>
                      </>
                    ) : (
                      <>
                        <Button type="link" onClick={() => textOpend(true)}>
                          {$i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Expand',
                            dm: '展开',
                          })}
                        </Button>
                      </>
                    )}
                  </div>
                </>
              ) : (
                <></>
              )} */}
            </Card>
          </Col>

          <Col span={17} className={styles.colRight}>
            <Form.Item hidden name="colsSplit" />

            <Form.Item hidden name="cols" />

            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.LogsAreDistributedInMultiple',
                dm: '日志分布在多行',
              })}
              {...layout}
              required
              name="multiLine"
            >
              <Radio.Group
                defaultValue={false}
                onChange={(e: any) => {
                  setMultiLine(e.target.value);
                }}
              >
                <Radio value={false}>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.No',
                    dm: '否',
                  })}
                </Radio>

                <Radio value={true}>
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Yes',
                    dm: '是',
                  })}
                </Radio>
              </Radio.Group>
            </Form.Item>

            {mulitLine && (
              <>
                <Form.Item
                  label={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.SpecifyLocation',
                    dm: '指定位置',
                  })}
                  {...layout}
                  required={form.getFieldValue('multiLine')}
                  name="lineType"
                >
                  <Radio.Group>
                    <Radio value="logHead">
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Head',
                        dm: '行头',
                      })}
                    </Radio>

                    <Radio value="logTail">
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.EndOfLine',
                        dm: '行尾',
                      })}
                    </Radio>
                  </Radio.Group>
                </Form.Item>

                <Form.Item
                  label={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.RegularExpression',
                    dm: '正则表达式',
                  })}
                  {...layout}
                  required={form.getFieldValue('multiLine')}
                  name="logRegexp"
                >
                  <Input />
                </Form.Item>
              </>
            )}

            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.LogPreFilter',
                dm: '日志前置过滤',
              })}
              {...layout}
              name="preFilterChecked"
            >
              <Switch
                checked={preFilterChecked}
                onChange={() => {
                  setPreFilterChecked(!preFilterChecked);
                }}
              />
            </Form.Item>

            {preFilterChecked && (
              <Form.Item
                // label={$i18n.get({
                //   id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.PreRule',
                //   dm: '前置规则',
                // })}
                // {...layout}
                name="preFilterTable"
              >
                <Col span={20} offset={2}>
                  <Button
                    onClick={() => {
                      setColumnType('CREATE');
                      setSliType(2);
                      setShowLRModal(true);
                    }}
                    type="primary"
                  >
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AddFilteringRules',
                      dm: '添加过滤规则',
                    })}
                  </Button>

                  <Table
                    scroll={{ x: true }}
                    columns={preFilterColumns}
                    dataSource={[
                      ...(form.getFieldValue('preFilterTable') || []),
                    ]}
                    pagination={false}
                    style={{ margin: '15px 0' }}
                  />
                </Col>
              </Form.Item>
            )}

            <Form.Item
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.SplittingMethod',
                dm: '切分方式',
              })}
              required
              name="splitType"
              {...layout}
            >
              <Radio.Group
                onChange={(e: any) => {
                  setSplitType(e.target.value);
                  setSplitTable([]);
                }}
              >
                <Radio value="SEP">
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.DelimiterSplitting',
                    dm: '分隔符切分',
                  })}
                </Radio>

                <Radio value="LR">
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.FromLeftToRight',
                    dm: '左起右至',
                  })}

                  <Tooltip
                    placement="top"
                    title={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.SelectTheSplitMethodFrom',
                      dm: '选择「左起右至」切分方式，通过鼠标选取任意一行日志样本中的列值，就能智能生成列值切分。',
                    })}
                  >
                    <InfoCircleOutlined className={styles.icon} />
                  </Tooltip>
                </Radio>

                <Radio value="REGEXP">
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.RegularSegmentation',
                    dm: '正则切分',
                  })}
                </Radio>

                <Radio value="NO">
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.NoNeedToSplit',
                    dm: '无需切分',
                  })}
                </Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              noStyle
              shouldUpdate={(pre, cur) => pre.splitType !== cur.splitType}
            >
              {({ getFieldValue }) => {
                if (getFieldValue('splitType') === 'NO') {
                  return <LogKnow form={form} />;
                } else {
                  return null;
                }
              }}
            </Form.Item>
            {splitType === 'SEP' && (
              <Row>
                <Col span={10} offset={2}>
                  <Form.Item
                    label={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Separator',
                      dm: '分隔符',
                    })}
                    // {...layout}
                    labelCol={{ span: 8 }}
                    wrapperCol={{ span: 16 }}
                    name="splitFlag"
                    tooltip={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ByDefaultItIsSplit',
                      dm: '1.默认按逗号进行切分，不可更改\n                    2.分隔符切分支持单个分隔符，输入一个分隔符后按回车可继续输入分隔符',
                    })}
                    rules={[
                      {
                        required: true,
                        message: $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.CannotBeEmpty',
                          dm: '不能为空',
                        }),
                      },
                      {
                        validator: (rule, value) => {
                          let flag = false;
                          if (value?.length) {
                            for (let i = 0; i < value?.length; i++) {
                              if (value[i].length >= 40) {
                                flag = true;
                                break;
                              }
                            }
                          }
                          if (flag)
                            return Promise.reject(
                              $i18n.get({
                                id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ASingleDelimiterCannotExceed',
                                dm: '单个切分符长度不能超过40字符',
                              }),
                            );
                          return Promise.resolve();
                        },
                      },
                    ]}
                  >
                    <Select
                      mode="tags"
                      placeholder={$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.EnterTheDelimiterAndPress',
                        dm: '请输入分隔符内容，按Enter确认',
                      })}
                      className={styles.select}
                      maxTagTextLength={20}
                      onChange={(e: any) => {
                        if (e.length > 1) {
                          e.splice(0, 1);
                        }
                        const arr = e.map((i: any) =>
                          i.replace(/ /g, '\u0001'),
                        );
                        setSplitVal(e);
                        form.setFieldsValue({
                          splitFlag: arr,
                        });
                      }}
                    />
                  </Form.Item>
                </Col>

                <Col span={6}>
                  <Button
                    disabled={!splitVal?.length}
                    onClick={() => {
                      handleSplit(true);
                    }}
                    type="primary"
                  >
                    {$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.ConfirmSegmentation',
                      dm: '确认切分',
                    })}
                  </Button>
                </Col>

                <Col span={20} offset={2}>
                  <Form.Item name="splitTable">
                    <Table
                      scroll={{ x: true }}
                      columns={splitColumns}
                      dataSource={splitTable}
                      pagination={false}
                    />
                  </Form.Item>
                </Col>
              </Row>
            )}

            {splitType === 'LR' && (
              <Col span={20} offset={2}>
                <Button
                  onClick={() => {
                    setColumnType('CREATE');
                    setSliType(1);
                    setShowLRModal(true);
                  }}
                  type="dashed"
                  icon={<PlusOutlined />}
                  style={{
                    borderColor: '#46a6ff',
                    width: '100%',
                    marginBottom: 15,
                  }}
                >
                  {$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.AddShardRules',
                    dm: '添加切分规则',
                  })}
                </Button>

                <Form.Item name="lrTable">
                  <Table
                    columns={splitColumns}
                    dataSource={lrTable}
                    pagination={false}
                  />
                </Form.Item>
              </Col>
            )}

            {splitType === 'REGEXP' && (
              <>
                <Form.Item
                  {...layout}
                  label={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.RegularExpression',
                    dm: '正则表达式',
                  })}
                >
                  <Input.Group compact>
                    <Form.Item
                      noStyle
                      name="splitRegexp"
                      rules={[
                        {
                          required: splitType === 'REGEXP',
                          message: $i18n.get({
                            id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.CannotBeEmpty',
                            dm: '不能为空',
                          }),
                        },
                      ]}
                    >
                      <Input
                        placeholder={$i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.EnterRegularContent',
                          dm: '请输入正则内容',
                        })}
                        style={{ width: 'calc(100% - 200px)' }}
                      />
                    </Form.Item>

                    <Button
                      onClick={() => {
                        handleSplit(true);
                      }}
                      type="primary"
                    >
                      {$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.Split',
                        dm: '切分',
                      })}
                    </Button>
                  </Input.Group>
                </Form.Item>

                <Col span={20} offset={2}>
                  <Table
                    columns={splitColumns}
                    dataSource={splitTable}
                    pagination={false}
                  />
                </Col>
              </>
            )}
          </Col>
        </Row>
      </Card>

      {/* 日志获取 */}

      <LogLoad
        visible={visible}
        form={form}
        serverData={serverData}
        setVisible={setVisible}
        logType={logType}
        setLogType={setLogType}
        setSampleLog={setSampleLog}
        clearSplitResult={resetSplitResult}
      />

      {/* 左起右至切分弹窗 */}

      <LRModal
        visible={showLRModal}
        setVisible={setShowLRModal}
        sampleLog={form.getFieldValue('sampleLog')?.selectValue}
        transferFunc={(params) =>
          handleLRColumnValue(
            params,
            form.getFieldValue('sampleLog').selectValue || '',
          )
        }
        handleOk={handleOk}
        columnType={columnType}
        type={sliType}
        field={currentField}
      />

      <ModelComponent
        open={open}
        form={form}
        edit={edit}
        setOpen={setOpen}
        columnData={getColumnData()}
        updateTable={getUpdateTable()}
        index={index1}
        name={PropName}
        splitType={form.getFieldValue('splitType')}
        LR_Params={LR_Params}
      />
    </>
  );
};

export default LogSegmentation;
