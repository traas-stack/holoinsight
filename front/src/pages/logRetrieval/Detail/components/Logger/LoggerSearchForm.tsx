import { TimePicker } from 'antd';
import {
  Button,
  Checkbox,
  Input,
  Popover,
  Select,
  Switch,
  Typography,
} from 'antd';
import React, { useEffect, useState } from 'react';
import moment from 'moment';
import { QuestionCircleOutlined } from '@ant-design/icons';
import type { CheckboxChangeEvent } from 'antd/es/checkbox';
import styles from './Logger.less';

type TLoggerSearchForm = {
  value?: ILogSearchForm;
  servicSearchExtra: any;
  refresh: any;
  loggerPaths: ILogPath[];
  instanceList: IInstance[];
  loading: boolean;
  inServer?: boolean;
  onChange?: (val: ILogSearchForm) => void;
  useMultiService: boolean | undefined;
};

const LoggerSearchForm: React.FC<TLoggerSearchForm> = (props) => {
  const {
    value,
    onChange,
    useMultiService,
    servicSearchExtra,
    refresh,
    loading,
    inServer,
    loggerPaths,
  } = props;

  // 搜索文本
  const [queryText, setQueryText] = useState('');

  // 开启自动刷新
  const [openAutoRefresh, setOpenAutoRefresh] = useState(false);

  // 选中的时间范围
  const [queryRange, setQueryRange] = useState(
    `1hour,${moment().format('YYYY-MM-DD HH:mm:ss')}`,
  );

  const [streamMode, setStreamMode] = useState(false); // 单行模式还是多行模式

  // 选中的实例
  const [queryInstances, setQueryInstances] = useState<IInstance[]>([]);

  // 选中的路径
  const [queryLogPaths, setQueryLogPaths] = useState<ILogPath[]>([]);

  const triggerChange = (fields: any) => {
    onChange?.({
      queryText,
      openAutoRefresh,
      queryRange,
      queryLogPaths,
      queryInstances,
      streamMode,
      ...fields,
    });
  };

  // 这里时间查询需要改造
  const onTimeChange = (values: any) => {
    const { value } = values;
    setQueryRange(value);
    triggerChange({ queryRange: value });
  };

  /**
   * 切换单行模式或多行模式
   * @param checked
   */
  const changeStreamMode = (checked: boolean) => {
    setStreamMode(checked);
    triggerChange({ streamMode: checked });
  };

  useEffect(() => {
    if (value) {
      if (value.queryInstances) {
        setQueryInstances(value.queryInstances);
      }
      if (value.queryLogPaths) {
        setQueryLogPaths(value.queryLogPaths);
      }
    }
  }, [value]);

  return (
    <>
      <div className={styles.loggerHeader}>
        <Input.Search
          placeholder="输入关键字查询，可使用 and 或 or 组合查询，例如 GET and 200 and cn-shanghai"
          value={queryText}
          onChange={(e) => {
            setQueryText(e.target.value);
            triggerChange({ queryText: e.target.value });
          }}
          onSearch={refresh}
          style={{ flex: 1, marginRight: 16 }}
          suffix={
            <Popover
              title={'查询语法'}
              content={
                <>
                  <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <Typography.Text>
                      1、同时满足多个条件：
                      <span style={{ color: '#ff4d4f', fontWeight: 500 }}>
                        query1 and query2
                      </span>
                      ；
                    </Typography.Text>
                    <Typography.Text>
                      2、只需满足一个条件即可：
                      <span style={{ color: '#ff4d4f', fontWeight: 500 }}>
                        query1 or query2
                      </span>
                      ；
                    </Typography.Text>
                    <Typography.Text>
                      3、模糊查询关键词（前置匹配），用于替代0个或多个关键词，例如：
                      <span style={{ color: '#ff4d4f', fontWeight: 500 }}>
                        que*
                      </span>
                      ，并返回 que 开头的所有命中词
                    </Typography.Text>
                  </div>
                  {/* <div style={{ marginTop: '8px' }}>
                  <a>查询语法</a>
                </div> */}
                </>
              }
              trigger="hover"
            >
              <QuestionCircleOutlined
                style={{ color: 'rgba(0, 10, 26, 0.47)', cursor: 'pointer' }}
              />
            </Popover>
          }
        />
        <div className={styles.filterContainer}>
          <TimePicker
            value={queryRange}
            onChange={onTimeChange}
            refreshable={false}
            getPopupContainer={() => document.getElementById('logger')}
          />
          <Button className={styles.syncBtn}>
            <Checkbox
              style={{ marginRight: 8 }}
              checked={openAutoRefresh}
              onChange={(e: CheckboxChangeEvent) => {
                setOpenAutoRefresh(e.target.checked);
                triggerChange({ openAutoRefresh: e.target.checked });
              }}
            >
              自动刷新
            </Checkbox>
          </Button>
        </div>
      </div>
      {!inServer && (
        <div className={styles.loggerHeader}>
          {servicSearchExtra && (
            <div className={styles.extraComponent}>{servicSearchExtra}</div>
          )}
          {/*
              <div className={styles.filterContainer}>
                实例：
                <Select
                  placeholder={useMultiService ? '请根据服务选择实例' : '选择实例'}
                  mode="multiple"
                  maxTagCount="responsive"
                  showArrow
                  style={{ flex: 1 }}
                  value={queryInstances.map(
                    (item) => `${item.hostname}::${item.id}::${item.name}`,
                  )}
                  onChange={(value) => {
                    const instances = value.map((item) => {
                      const [hostname, id, name] = (item ?? '').split('::');
                      return { hostname, id, name };
                    });
                    setQueryInstances(instances);
                    triggerChange({ queryInstances: instances });
                  }}
                  options={instanceList.map((item) => ({
                    value: `${item.hostname}::${item.id}::${item.name}`,
                    label: item.hostname,
                  }))}
                />
              </div>
               */}
        </div>
      )}

      <div style={{ display: 'flex' }}>
        <div className={styles.loggerHeader} style={{ flex: 1 }}>
          路径：
          <Select
            placeholder="请选择日志路径"
            showArrow
            style={{ flex: 1 }}
            mode={useMultiService ? 'multiple' : undefined}
            maxTagCount="responsive"
            value={queryLogPaths.map((item) => `${item?.path}::${item?.name}`)}
            onChange={(value) => {
              const logPaths = (Array.isArray(value) ? value : [value]).map(
                (item) => {
                  const [path, name] = item.split('::');
                  return { path, name };
                },
              );
              setQueryLogPaths(logPaths);
              triggerChange({ queryLogPaths: logPaths });
            }}
            optionLabelProp="displayLabel"
            options={loggerPaths?.map((item) => ({
              value: `${item?.path}::${item?.name}`, // 组合路径和服务名
              label: (
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'row',
                    justifyContent: 'space-between',
                  }}
                >
                  <div>{item?.path}</div>
                  {useMultiService && <div>{item?.name}</div>}
                </div>
              ),
              displayLabel: (
                <div style={{ maxWidth: 280 }}>
                  {useMultiService && (
                    <span
                      style={{
                        fontWeight: 500,
                        color: 'rgba(0, 10, 26, 0.85)',
                      }}
                    >
                      {item?.name}：
                    </span>
                  )}
                  <span>{item?.path}</span>
                </div>
              ),
            }))}
          />
        </div>
        {useMultiService && (
          <div
            className={styles.loggerHeader}
            style={{ width: '150px', marginLeft: '16px' }}
          >
            stream模式：
            <Switch
              loading={loading}
              checkedChildren="开启"
              unCheckedChildren="关闭"
              checked={streamMode}
              onChange={changeStreamMode}
            />
          </div>
        )}
      </div>
    </>
  );
};

export default LoggerSearchForm;
