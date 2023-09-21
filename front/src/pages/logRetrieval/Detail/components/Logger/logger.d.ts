interface ILoggerQuery {
  logs?: CLOUDMONITOR_API.MiniProgramLogQueryResponse['logs'];
  pagination: {
    pageNum?: number;
    pageSize?: number;
    total: number;
    style?: any;
  } | null;
}

interface IContextQuery {
  data: CLOUDMONITOR_API.SlsLog[];
  hasPrev: boolean;
  hasNext: boolean;
  prevLoading: boolean;
  nextLoading: boolean;
}

interface IInstance {
  hostname: string;
  name: string;
  id: string;
}

interface ILogPath {
  name: string;
  path: string;
}

interface ILogSearchForm {
  openAutoRefresh: boolean; // 自动刷新
  queryText: string; // 关键字查询
  queryRange: string; // 时间范围
  queryLogPaths: ILogPath[]; // 选中的路径
  queryInstances: IInstance[]; // 选中的路径
  streamMode: boolean; // 单行模式还是多行模式
}
