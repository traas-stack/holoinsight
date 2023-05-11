import {
  context,
  count,
  multiQuery,
  paths,
  query,
} from '@/services/logRetrieval/api';
import { TimePicker } from 'antd';
import { Alert, List, message, Typography } from 'antd';
import React, {
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { findIndex } from 'lodash';
import moment from 'moment';
import { TagsTwoTone } from '@ant-design/icons';
import { useBoolean, useDebounceFn, useRequest, useSetState } from 'ahooks';
import CopyToClipboard from 'react-copy-to-clipboard';
import styles from './Logger.less';
import LoggerContent from './LoggerContent';
import LoggerContext from './LoggerContext';
import LoggerSearchForm from './LoggerSearchForm';
import LoggerTagsDrawer from './LoggerTagsDrawer';
import { polyLogs } from './util';

export type TimePickerValue = {
  queryRange: string;
  starttime: number;
  endtime: number;
};

function getContext(queryRange: string, delay?: number): TimePickerValue {
  const { value, from, to } = TimePicker.parse(queryRange, delay);
  return {
    queryRange: value,
    starttime: from.unix(),
    endtime: to.unix(),
  };
}

const defaultLoggerPath = '/virtual/stdout/json.log';
interface ILoggerProps {
  servicSearchExtra?: any;
  serviceInfos: {
    id: string;
    name: string;
  }[];
  useMultiService?: boolean; // 开启多服务实例搜索
  inServer?: boolean; // 开启多服务实例搜索
}
const Logger: React.FC<ILoggerProps> = ({
  serviceInfos,
  servicSearchExtra,
  useMultiService,
  inServer,
}) => {
  const loggerContextRef = useRef({
    activePackIntoView: () => {},
    setScrollTop: (_: number) => _,
  });
  const timerRef = useRef<NodeJS.Timer | null>(null);

  const defaultSearchForm = {
    openAutoRefresh: false, // 自动刷新
    queryText: '', // 关键字查询
    queryRange: `1hour,${moment().format('YYYY-MM-DD HH:mm:ss')}`, // 时间范围
    queryLogPaths: [], // 选中的路径
    queryInstances: [], // 选中的路径
    streamMode: false, // 单行模式还是多行模式
  };
  const [searchForm, setSearchForm] =
    useSetState<ILogSearchForm>(defaultSearchForm);
  // 分页信息（单服务：用于后端分页）
  const [queryPagination, setQueryPagination] = useState({
    pageNum: 1,
    pageSize: 20,
  });
  // 分页信息（多服务：用于前端分页）
  const [multiServicePagination, setMultiServicePagination] = useState({
    pageNum: 1,
    pageSize: 20,
  });

  // 查询到的日志信息（用于渲染）
  const [loggerQuery, setLoggerQuery] = useSetState<ILoggerQuery>({
    logs: [],
    pagination: {
      pageNum: 1,
      pageSize: 20,
      total: 0,
    },
  });

  // 查询到的日志信息（源数据）
  const [loggerQueryDataSource, setLoggerQueryDataSource] =
    useState<ILoggerQuery>({
      logs: [],
      pagination: {
        pageNum: 1,
        pageSize: 20,
        total: 0,
      },
    });
  // 查询到的上下文查询信息
  const [contextQuery, setContextQuery] = useSetState<IContextQuery>({
    data: [],
    hasPrev: true,
    hasNext: true,
    prevLoading: false,
    nextLoading: false,
  });

  // 查询到的路径信息
  const [loggerPaths, setLoggerPaths] = useState<ILogPath[]>([]);

  // 查询到的实例信息
  const [instanceList, setInstanceList] = useState<IInstance[]>([]);

  // 查询到 packInfo 信息
  const [packInfo, setPackInfo] = useState<{
    packId: string;
    packMeta: string;
    contextServiceId: string;
  }>();

  // const logData = useRef<{ dataSource: any[]; polyedLogs: any[] }>({
  //   dataSource: [],
  //   polyedLogs: [],
  // }); // 缓存数据源

  const [tagsData, setTagsData] = useState<Record<string, string>>({});
  const [tagsVisible, { setTrue: openTagsDrawer, setFalse: closeTagsDrawer }] =
    useBoolean(false);

  const {
    data: contextData = [],
    hasPrev,
    hasNext,
    prevLoading,
    nextLoading,
  } = contextQuery;

  const rangeContext = useMemo(() => {
    return getContext(searchForm.queryRange);
  }, [searchForm.queryRange]);

  // 查询多服务日志
  const queryMultiLogger = async () => {
    const from = rangeContext.starttime;
    const to = rangeContext.endtime;
    const serviceQueryParams = serviceInfos.map((info) => {
      return {
        service: info.name,
        paths: searchForm.queryLogPaths
          .filter((logPath) => logPath.name === info.name)
          .map((logPath) => logPath.path),
        instances: searchForm.queryInstances
          .filter((instance) => instance.name === info.name)
          .map((instance) => instance.hostname),
      };
    });
    const requestParams = {
      from,
      to,
      query: searchForm.queryText,
      serviceQueryParams,
    };

    const resultLogger = await multiQuery(requestParams);

    if (resultLogger.success) {
      // 聚合日志数据：1.根据时间开头的数据聚合 2.根据tags中的hostname（实例）聚合
      const resLoggerData = resultLogger?.data?.logs || [];
      const polyedLogs = polyLogs(resLoggerData);
      const logs = searchForm.streamMode ? resLoggerData : polyedLogs; // 根据单行/多行模式选用不同数据结构
      setLoggerQueryDataSource({ logs: resLoggerData, pagination: null }); // 缓存源数据
      setLoggerQuery({
        logs,
        pagination: {
          total: logs.length ?? 0,
        },
      });
    }
  };
  // 查询单服务日志
  const querySingleLogger = async () => {
    if (!serviceInfos[0]?.name) {
      return;
    }
    const from = rangeContext.starttime;
    const to = rangeContext.endtime;
    const requestParams = {
      from,
      to,
      query: searchForm.queryText,
      app: serviceInfos[0]?.name,
      logPath: searchForm.queryLogPaths[0]?.path,
      instances: searchForm.queryInstances.map((instance) => instance.hostname),
      context: {
        serviceId: serviceInfos[0]?.name,
      },
    };

    const resultPageInfo = await count(requestParams);
    const resultLogger = await query({
      ...requestParams,
      pageNum: queryPagination.pageNum ?? 1,
      pageSize: queryPagination.pageSize ?? 20,
      count: resultPageInfo?.count ?? 0,
    });
    if (resultPageInfo) {
      // 聚合日志数据：1.根据时间开头的数据聚合 2.根据tags中的hostname（实例）聚合
      const resLoggerData = resultLogger?.logs || [];
      // const polyedLogs = polyLogs(resLoggerData);
      // const logs = searchForm.streamMode ? resLoggerData : polyedLogs; // 根据单行/多行模式选用不同数据结构
      setLoggerQueryDataSource({ logs: resLoggerData, pagination: null }); // 缓存源数据
      setLoggerQuery({
        logs: resLoggerData,
        pagination: {
          pageNum: queryPagination.pageNum ?? 1,
          pageSize: queryPagination.pageSize ?? 20,
          total: resultPageInfo?.count ?? 0,
        },
      });
    }
  };
  // 查询日志服务
  const queryLogger = async () => {
    if (useMultiService) {
      await queryMultiLogger();
    } else {
      await querySingleLogger();
    }
  };
  // 查询上下文
  const queryContext = async ({
    packID,
    packMeta,
    forwardLines,
    backLines,
    appendHead,
    appendTail,
    serviceId,
  }: CLOUDMONITOR_API.MiniProgramLogContextQueryRequest & {
    appendHead?: boolean;
    appendTail?: boolean;
  }) => {
    const ct = getParamsContext();
    const contextResust = await context({
      serviceId: ct.serviceId,
      packID,
      packMeta,
      backLines: backLines ?? 0,
      forwardLines: forwardLines ?? 0,
      context: {
        ...ct,
        packId: packID,
        packMeta,
      },
    });
    if (!contextResust) {
      return;
    }

    const logs = contextResust?.logs ?? [];

    if (appendHead) {
      setContextQuery({
        ...contextQuery,
        data: [...logs.slice(0, logs.length - 1), ...contextQuery.data], // 去除返回的最后一条数据
        hasPrev: logs.length ? true : false,
        prevLoading: false,
      });
      loggerContextRef.current.setScrollTop(logs.length);
      return;
    }

    if (appendTail) {
      setContextQuery({
        ...contextQuery,
        data: [...contextQuery.data, ...logs.slice(1, logs.length)], // 去除返回的第一条数据
        hasNext: logs.length ? true : false,
        nextLoading: false,
      });
      return;
    }

    setContextQuery({
      ...contextQuery,
      data: [...logs],
      hasPrev: true,
      hasNext: true,
    });
  };
  function getParamsContext() {
    return {
      serviceId: serviceInfos[0]?.name,
    };
  }
  // 查询服务下的路径信息
  const queryAllPaths = async ({ from, to }: { from: number; to: number }) => {
    if (serviceInfos.length === 0) {
      setLoggerPaths([]);
      setSearchForm({ queryLogPaths: [] });
      return;
    }

    const pathPromises = serviceInfos.map(async (info) => {
      try {
        const result = await paths({
          serviceId: info.name,
          from,
          to,
          context: getParamsContext(),
        });
        const data: ILogPath[] = ((result as Array) || []).map((path) => ({
          path,
          name: info.name,
        })); // 给每一个路径补上服务名
        return data;
      } catch (_) {
        return [];
      }
    });
    const pathInfoResults = await Promise.all(pathPromises);
    const loggerPaths = pathInfoResults.reduce(
      (allPaths, res) => allPaths.concat(res),
      [],
    );
    setLoggerPaths(loggerPaths);

    // 单服务情况下，需要设置默认值
    if (
      !useMultiService &&
      loggerPaths.length > 0 &&
      searchForm.queryLogPaths.length === 0
    ) {
      setSearchForm({
        queryLogPaths: [
          {
            path: defaultLoggerPath,
            name: serviceInfos[0].name,
          },
        ],
      });
    } else {
      // 多服务：更新设置 路径
      const newQueryLogPaths = searchForm.queryLogPaths.filter((info) => {
        const filterItems = loggerPaths.filter(
          (item) => item.name === info.name && item.path === info.path,
        );
        return !!filterItems.length;
      });
      setSearchForm({ queryLogPaths: newQueryLogPaths });
    }
  };

  // 查询服务下的实例列表
  // const queryInstanceList = async () => {
  //   if (serviceInfos.length === 0) {
  //     setInstanceList([]);
  //     setSearchForm({ queryInstances: [] });
  //     return;
  //   }
  //   const instancePromises = serviceInfos.map(async (info) => {
  //     try {
  //       const result = await listServers({ uuid: info?.id });
  //       const data: IInstance[] = (result?.data || []).map((server) => ({
  //         hostname: server.hostname || '',
  //         name: info.name,
  //         id: info.id,
  //       })); // 给每一个路径补上服务名
  //       return data;
  //     } catch (_) {
  //       return [];
  //     }
  //   });
  //
  //   const instanceResults = await Promise.all(instancePromises);
  //   const instances = instanceResults.reduce((allInstances, data) => {
  //     return allInstances.concat(data);
  //   }, []);
  //   setInstanceList(instances);
  //
  //   // 设置新的过滤搜索条件
  //   const newQueryInstances = searchForm.queryInstances.filter((info) => {
  //     const filterItems = instances.filter((item) => item.id === info.id);
  //     return !!filterItems.length;
  //   });
  //   setSearchForm({ queryInstances: newQueryInstances });
  // };

  //* 封装出来的请求
  const { loading: loggerLoading, run: runLogger } = useRequest(queryLogger, {
    refreshDeps: [serviceInfos, rangeContext, queryPagination, searchForm],
  });

  // 请求：查询上下文
  const { loading: contextLoading, run: runContext } = useRequest(
    queryContext,
    {
      manual: true,
    },
  );

  //* 一些实际aciton
  // 日志上下文：往前查
  const loadBackData = async () => {
    runContext({
      packID: contextData[0]?.packId,
      packMeta: contextData[0]?.packMeta,
      backLines: 29,
      appendHead: true,
      serviceId: packInfo?.contextServiceId,
    });
  };
  // 日志上下文：往后查
  const loadForwardData = async () => {
    const length = contextData.length - 1;
    runContext({
      packID: contextData[length]?.packId,
      packMeta: contextData[length]?.packMeta,
      forwardLines: 29,
      appendTail: true,
      serviceId: packInfo?.contextServiceId,
    });
  };

  // 展示日志上下文
  const showLogContext = (item: any) => {
    const { packId, packMeta, serviceId: contextServiceId } = item.tags;
    setPackInfo({
      packId,
      packMeta,
      contextServiceId,
    });
    setContextQuery({
      ...contextQuery,
      data: [],
    });

    runContext({
      packID: packId,
      packMeta,
      backLines: 29,
      forwardLines: 29,
      serviceId: item.serviceId,
    });
    setTimeout(() => {
      // 延迟滚动
      loggerContextRef.current?.activePackIntoView?.();
    }, 500);
  };

  const onContextClose = () => {
    setPackInfo(undefined);
  };

  const { run: handleContextScroll } = useDebounceFn(
    (e: React.UIEvent<HTMLDivElement>) => {
      const target = e.currentTarget || e.target;
      if (hasPrev && target.scrollTop === 0) {
        setContextQuery({
          ...contextQuery,
          prevLoading: true,
        });
        loadBackData();
      }

      if (
        hasNext &&
        target.scrollTop >= target.scrollHeight - target.offsetHeight - 20
      ) {
        setContextQuery({
          ...contextQuery,
          nextLoading: true,
        });
        loadForwardData();
      }
    },
    {
      wait: 300,
    },
  );

  const activePackIndex = findIndex(contextData, {
    packId: packInfo?.packId,
    packMeta: packInfo?.packMeta,
  });

  // 自动刷新: 目前固定 30s 自动刷新
  useEffect(() => {
    if (searchForm.openAutoRefresh) {
      timerRef.current = setInterval(() => {
        runLogger();
      }, 30000);
    } else {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    }

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [searchForm.openAutoRefresh]);

  // *初始 setting
  useEffect(() => {
    // 获取路径信息
    queryAllPaths({
      from: moment().subtract(3, 'days').unix(),
      to: moment().unix(),
    });
    // 获取实例信息
    // queryInstanceList();
  }, [serviceInfos]);

  const showLogTagsDrawer = (item: any) => {
    if (!item.tags) {
      message.warning('该条日志无标签');
      return;
    }
    setTagsData(item.tags);
    openTagsDrawer();
  };

  const getPagination = (): any => {
    if (useMultiService) {
      // 多服务，前端自行分页，采用默认配置
      return {
        onChange: (pageNum: number, pageSize: number) =>
          setMultiServicePagination({ pageNum, pageSize }),
        current: multiServicePagination.pageNum,
        total: loggerQuery.pagination?.total ?? 0,
        pageNum: multiServicePagination?.pageNum ?? 1,
        pageSize: multiServicePagination?.pageSize ?? 20,
        style: { margin: 24 },
      };
    } else {
      // 单服务，后端分页
      return loggerQuery?.pagination?.total
        ? {
            onChange: (pageNum: number, pageSize: number) =>
              setQueryPagination({ pageNum, pageSize }),
            current: loggerQuery.pagination?.pageNum,
            total: loggerQuery.pagination?.total,
            pageSize: loggerQuery.pagination?.pageSize,
            style: { margin: 24 },
          }
        : false;
    }
  };

  /**
   * 触发上面搜索表单
   * @param fields
   */
  const triggerSearchForm = (fields: ILogSearchForm) => {
    setSearchForm(fields);
    if (useMultiService) {
      setMultiServicePagination({ ...multiServicePagination, pageNum: 1 });
    }
  };

  return (
    <div id="logger">
      <LoggerSearchForm
        inServer={inServer}
        useMultiService={useMultiService}
        servicSearchExtra={servicSearchExtra}
        refresh={runLogger}
        loading={loggerLoading}
        instanceList={instanceList}
        loggerPaths={loggerPaths}
        onChange={triggerSearchForm}
        value={searchForm}
      />
      {/* 小黄条提示 */}
      {(loggerQueryDataSource.logs || []).length >= 500 && (
        <Alert
          type="warning"
          showIcon
          message="当前最多500行日志，若想查看其他数据请优化检索条件或缩小查询时间间隔！"
          closable
          style={{ marginBottom: 16 }}
        />
      )}
      <List
        className={styles.loggerList}
        loading={loggerLoading}
        itemLayout="horizontal"
        rowKey="packMeta"
        dataSource={loggerQuery.logs || []}
        renderItem={(item: any) => {
          const time = moment(item.logTime * 1000).format(
            'YYYY-MM-DD HH:mm:ss',
          );
          return (
            <List.Item
              actions={[
                <a key="context" onClick={() => showLogTagsDrawer(item)}>
                  <TagsTwoTone />
                </a>,
                <a key="context" onClick={() => showLogContext(item)}>
                  查看上下文
                </a>,
                <CopyToClipboard
                  key="copy"
                  text={`${time}${item.content}`}
                  onCopy={() => message.success('复制成功')}
                >
                  <a>复制</a>
                </CopyToClipboard>,
              ]}
            >
              <List.Item.Meta
                title={
                  <Typography.Paragraph
                    code
                    style={{
                      minWidth: 160,
                      textAlign: 'left',
                      fontWeight: 'bold',
                    }}
                  >
                    {time}
                  </Typography.Paragraph>
                }
                description={<LoggerContent content={item.content} />}
              />
            </List.Item>
          );
        }}
        pagination={getPagination()}
      />
      <LoggerTagsDrawer
        tags={tagsData}
        visible={tagsVisible}
        onClose={closeTagsDrawer}
      />
      <LoggerContext
        open={!!packInfo}
        onClose={onContextClose}
        handleScroll={handleContextScroll}
        prevLoading={prevLoading}
        nextLoading={nextLoading}
        loadForwardData={loadForwardData}
        contextData={contextData}
        hasNext={hasNext}
        contextLoading={contextLoading}
        activePackIndex={activePackIndex}
        ref={loggerContextRef}
      />
    </div>
  );
};

export default Logger;
