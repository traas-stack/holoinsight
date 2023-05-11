/* eslint-disable */
// 该文件由 OneAPI 自动生成，请勿手动修改 :)
// schemaId:640584815a1f9d8fcbd5c1ba
declare namespace CLOUDMONITOR_API {
  enum TraceState {
    ALL = 'ALL',
    SUCCESS = 'SUCCESS',
    ERROR = 'ERROR',
  }

  enum QueryOrder {
    BY_START_TIME = 'BY_START_TIME',
    BY_DURATION = 'BY_DURATION',
  }

  enum RefType {
    CROSS_PROCESS = 'CROSS_PROCESS',
    CROSS_THREAD = 'CROSS_THREAD',
  }

  interface ComposedQueryTinyAppDataSource {
    tenant?: string;
    query?: string;
    dataSources?: QueryDataSource[];
    fillPolicy?: string;
    downsample?: string;
    /** 小程序 serviceId */
    serviceId?: string;
  }

  interface JsonResult_AlarmRuleDTO_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: AlarmRuleDTO;
  }

  interface JsonResult_AlarmSubscribeDTO_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: AlarmSubscribeDTO;
  }

  interface JsonResult_AntLogIndexConfig_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: AntLogIndexConfig;
  }

  interface JsonResult_KeyResult_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: KeyResult;
  }

  interface JsonResult_List_String__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: string[];
  }

  interface JsonResult_Map_String_List_String___ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: Record<string, any>;
  }

  interface JsonResult_Map_String_Object__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: Record<string, any>;
  }

  interface JsonResult_MiniProgramLogContextQueryResponse_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MiniProgramLogContextQueryResponse;
  }

  interface JsonResult_MiniProgramLogCountQueryResponse_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MiniProgramLogCountQueryResponse;
  }

  interface JsonResult_MiniProgramLogQueryResponse_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MiniProgramLogQueryResponse;
  }

  interface JsonResult_MiniProgramMultiLogQueryResponse_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MiniProgramMultiLogQueryResponse;
  }

  interface JsonResult_MonitorPageResult_AlarmGroupDTO__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MonitorPageResult_AlarmGroupDTO_;
  }

  interface JsonResult_MonitorPageResult_AlarmHistoryDTO__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MonitorPageResult_AlarmHistoryDTO_;
  }

  interface JsonResult_MonitorPageResult_AlarmHistoryDetailDTO__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MonitorPageResult_AlarmHistoryDetailDTO_;
  }

  interface JsonResult_MonitorPageResult_AlarmRuleDTO__ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MonitorPageResult_AlarmRuleDTO_;
  }

  interface JsonResult_MonitorPageResult_Map_String_Object___ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: MonitorPageResult_Map_String_Object__;
  }

  interface JsonResult_PositionBizRuleVO_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: PositionBizRuleVO;
  }

  interface JsonResult_Topology_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: Topology;
  }

  interface JsonResult_TraceBrief_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: TraceBrief;
  }

  interface JsonResult_Trace_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: Trace;
  }

  interface JsonResult_boolean_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: boolean;
  }

  interface JsonResult_long_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: number;
  }

  interface JsonResult_object_ {
    success?: boolean;
    /**
     * <p>
     * Getter for the field <code>message</code>.
     * </p>
     */
    message?: string;
    /**
     * <p>
     * Getter for the field <code>resultCode</code>.
     * </p>
     */
    resultCode?: string;
    /**
     * <p>
     * Getter for the field <code>data</code>.
     * </p>
     */
    data?: Record<string, any>;
  }

  interface KeyResult {
    metric?: string;
    tags?: string[];
  }

  interface MicroAppAlarmGroupDTO {
    id?: number;
    tenant?: string;
    groupName?: string;
    groupInfo?: GroupInfoDTO;
    creator?: string;
    modifier?: string;
    gmtCreate?: string;
    gmtModified?: string;
    emailAddress?: string;
    ddWebhook?: string;
    dyvmsPhone?: string;
    smsPhone?: string;
    /** 环境类型 */
    envType?: string;
    appId?: string;
    envId?: string;
    serviceId?: string;
  }

  interface MicroAppAlarmRuleDTO {
    /** id */
    id?: number;
    /** 创建时间 */
    gmtCreate?: string;
    /** 修改时间 */
    gmtModified?: string;
    /** 规则名称 */
    ruleName?: string;
    /** 规则类型（AI、RULE） */
    ruleType?: string;
    /** 创建者 */
    creator?: string;
    /** 修改者 */
    modifier?: string;
    /** 告警级别 */
    alarmLevel?: string;
    /** 规则描述 */
    ruleDescribe?: string;
    /** 规则是否生效 */
    status?: string;
    /** 合并是否开启 */
    isMerge?: string;
    /** 合并方式 */
    mergeType?: string;
    /** 恢复通知是否开启 */
    recover?: string;
    /** 通知方式 */
    noticeType?: string;
    /** 触发方式简述 */
    alarmContent?: string[];
    /** 租户id */
    tenant?: string;
    /** 告警规则 */
    rule?: Record<string, any>;
    /** 生效时间 */
    timeFilter?: Record<string, any>;
    /** 屏蔽id */
    blockId?: number;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 额外信息 */
    extra?: AlertRuleExtra;
    /** 环境类型 */
    envType?: string;
    /** pql */
    pql?: string;
    appId?: string;
    envId?: string;
    serviceId?: string;
  }

  interface MicroAppAlarmSubscribeDTO {
    uniqueId?: string;
    alarmSubscribe?: AlarmSubscribeInfo[];
    /** 环境类型 */
    envType?: string;
    appId?: string;
    envId?: string;
    serviceId?: string;
  }

  interface MiniProgramLogAgentConfig {
    serviceId?: string;
    /** 日志路径 */
    path?: string;
    /** 采集配置 */
    agentConfig?: AntLogAgentConfig;
  }

  interface MiniProgramLogContextQueryRequest {
    /** 服务id */
    serviceId?: string;
    /** packId */
    packID?: string;
    /** packMeta */
    packMeta?: string;
    /** 往后查多少行，最多50 */
    backLines?: number;
    /** 往前查多少行, 最多50 */
    forwardLines?: number;
    /** 日志路径 */
    logPath?: string;
    context?: any;
  }

  interface MiniProgramLogContextQueryResponse {
    /** 日志明细 */
    logs?: SlsLog[];
    /** 查询是否完成 */
    isCompleted?: boolean;
    /** 查询范围内日志总量 */
    count?: number;
    /** 往后查多少行，最多50 */
    backLines?: number;
    /** 往前查多少行, 最多50 */
    forwardLines?: number;
  }

  interface MiniProgramLogCountQueryRequest {
    /** 服务id */
    serviceId?: string;
    /** 请求 */
    query?: string;
    /** 实例 */
    instances?: string[];
    /** 日志路径 */
    logPath?: string;
    /** 查询开始时间:s */
    from?: number;
    /** 查询结束时间:s */
    to?: number;
  }

  interface MiniProgramLogCountQueryResponse {
    /** 日志总量 */
    count?: number;
    /** 租户 */
    appId?: string;
    /** namespace */
    envId?: string;
    /** 服务名 */
    serviceId?: string;
    /** 查询开始时间:s */
    start?: number;
    /** 查询结束时间:s */
    end?: number;
  }

  interface MiniProgramLogIndexConfig {
    serviceId?: string;
    /** 日志路径 */
    path?: string;
    /** 索引分隔符 */
    index?: string[];
  }

  interface MiniProgramLogQueryRequest {
    /** 服务id */
    serviceId?: string;
    /** 查询开始时间:s */
    from?: number;
    /** 查询结束时间:s */
    to?: number;
    /** 查询语句 */
    query?: string;
    /** 分页数 */
    pageNum?: number;
    /** 分页大小 */
    pageSize?: number;
    /** 当前时间范围内的日志总量 默认20防止出现空数据报错的情况 */
    count?: number;
    /** 是否倒序显示 */
    isReverse?: boolean;
    /** 实例名 */
    instances?: string[];
    /** 查询日志路径列表 */
    logPath?: string;
  }

  interface MiniProgramLogQueryResponse {
    /** 日志标签 */
    mKeys?: string[];
    /** 日志明细 */
    logs?: SlsLog[];
    /** 查询SQL,或者复杂查询条件 */
    aggQuery?: string;
    /** GREP过滤查询条件 */
    whereQuery?: string;
    /** 是否包含特殊处理 对应服务器日志查询里的awk 脚本 对应SLS查询里的select 脚本 */
    hasSQL?: boolean;
    /** 查询任务是否完成 */
    isCompleted?: boolean;
    /** 日志总量 */
    count?: number;
    /** 查询开始时间:s */
    start?: number;
    /** 查询结束时间:s */
    end?: number;
  }

  interface MiniProgramMultiLogQueryRequest {
    /** 服务与路径 */
    serviceQueryParams?: MiniProgramServiceQueryParam[];
    /** 查询开始时间:s */
    from?: number;
    /** 查询结束时间:s */
    to?: number;
    /** 查询语句 */
    query?: string;
    /** 默认多服务返回日志条数 */
    defaultLineCount?: number;
  }

  interface MiniProgramMultiLogQueryResponse {
    /** 日志明细 */
    logs?: SlsLog[];
    /** query */
    query?: string;
    /** 小程序云服务查询参数 */
    serviceQueryParams?: MiniProgramServiceQueryParam[];
    /** 日志总量 */
    count?: number;
    /** 查询开始时间:s */
    from?: number;
    /** 查询结束时间:s */
    to?: number;
  }

  interface MiniProgramServiceQueryParam {
    service?: string;
    paths?: string[];
    instances?: string[];
  }

  interface MonitorPageRequest_MicroAppAlarmGroupDTO_ {
    pageNum?: number;
    pageSize?: number;
    sortBy?: string;
    sortRule?: string;
    target?: MicroAppAlarmGroupDTO;
    from?: number;
    to?: number;
  }

  interface MonitorPageRequest_MicroAppAlarmHistoryDTO_ {
    pageNum?: number;
    pageSize?: number;
    sortBy?: string;
    sortRule?: string;
    target?: MicroAppAlarmHistoryDTO;
    from?: number;
    to?: number;
  }

  interface MonitorPageRequest_MicroAppAlarmHistoryDetailDTO_ {
    pageNum?: number;
    pageSize?: number;
    sortBy?: string;
    sortRule?: string;
    target?: MicroAppAlarmHistoryDetailDTO;
    from?: number;
    to?: number;
  }

  interface MonitorPageRequest_MicroAppAlarmRuleDTO_ {
    pageNum?: number;
    pageSize?: number;
    sortBy?: string;
    sortRule?: string;
    target?: MicroAppAlarmRuleDTO;
    from?: number;
    to?: number;
  }

  interface MonitorPageRequest_MicroAppAlarmSubscribeDTO_ {
    pageNum?: number;
    pageSize?: number;
    sortBy?: string;
    sortRule?: string;
    target?: MicroAppAlarmSubscribeDTO;
    from?: number;
    to?: number;
  }

  interface MonitorPageResult_AlarmGroupDTO_ {
    pageNum?: number;
    pageSize?: number;
    totalCount?: number;
    totalPage?: number;
    items?: AlarmGroupDTO[];
  }

  interface MonitorPageResult_AlarmHistoryDTO_ {
    pageNum?: number;
    pageSize?: number;
    totalCount?: number;
    totalPage?: number;
    items?: AlarmHistoryDTO[];
  }

  interface MonitorPageResult_AlarmHistoryDetailDTO_ {
    pageNum?: number;
    pageSize?: number;
    totalCount?: number;
    totalPage?: number;
    items?: AlarmHistoryDetailDTO[];
  }

  interface MonitorPageResult_AlarmRuleDTO_ {
    pageNum?: number;
    pageSize?: number;
    totalCount?: number;
    totalPage?: number;
    items?: AlarmRuleDTO[];
  }

  interface MonitorPageResult_Map_String_Object__ {
    pageNum?: number;
    pageSize?: number;
    totalCount?: number;
    totalPage?: number;
    items?: Record<string, any>[];
  }

  interface PositionBizRuleVO {
    /** 账号下的站点信息 */
    appId?: string;
    /** 环境 */
    envId?: string;
    /** 服务名 */
    appName?: string;
    /** 接口类型：这里分为Rpc类型和Http类型 */
    interfaceType?: string;
    /** 服务接口 */
    interfaceName?: string;
    /** 规则全局生效与否，T代表生效、F代表不生效 */
    globalOpen?: string;
    /** 返回值类型：只有Http服务类型才会用到该字段，其中Return表示直接从返回值中取，ModelMap表示从ModelMap中取 */
    responseType?: string;
    /** 从modelmap里取的属性，只有选择ModelMap才需要该字段 */
    responseProperty?: string;
    /** 业务结果是否失败,至少配置一个 */
    bizResult?: BizResultConfig[];
    /** 业务错误码相关配置 */
    errorCode?: string;
  }

  interface QueryDataSource {
    name?: string;
    start?: number;
    end?: number;
    metric?: string;
    aggregator?: string;
    fillPolicy?: string;
    filters?: QueryFilter[];
    slidingWindow?: SlidingWindow;
    downsample?: string;
    groupBy?: string[];
  }

  interface QueryFilter {
    type?: string;
    name?: string;
    value?: string;
  }

  interface QueryProtoQueryTopologyRequest {
    bitField0_?: number;
    tenant_?: Record<string, any>;
    start_?: number;
    end_?: number;
    serviceName_?: Record<string, any>;
    serviceInstanceName_?: Record<string, any>;
    endpointName_?: Record<string, any>;
    address_?: Record<string, any>;
    depth_?: number;
    category_?: Record<string, any>;
    termParams_?: MapField_String_String_;
    memoizedIsInitialized?: string;
  }

  interface QueryTinyAppDataSource {
    name?: string;
    start?: number;
    end?: number;
    metric?: string;
    aggregator?: string;
    fillPolicy?: string;
    filters?: QueryFilter[];
    slidingWindow?: SlidingWindow;
    downsample?: string;
    groupBy?: string[];
    /** 小程序 serviceId */
    serviceId?: string;
  }

  interface SlidingWindow {
    windowMs?: number;
    aggregator?: string;
  }

  interface SlsLog {
    /** 租户 */
    appId?: string;
    /** namespace */
    envId?: string;
    /** 服务名 */
    serviceId?: string;
    /** 日志路径，830版本中logPath填写默认值"stdout" */
    logPath?: string;
    /** version */
    version?: string;
    /** IP */
    source?: string;
    /** PodName */
    podName?: string;
    /** 日志内容 */
    content?: string;
    /** 日志时间 */
    time?: number;
    /** packId */
    packId?: string;
    /** packMeta */
    packMeta?: string;
    /** packShardId */
    packShardId?: number;
    /** packCursor */
    packCursor?: string;
    /** packNum */
    packNum?: number;
    /** packOffset */
    packOffset?: number;
    /** 标注标签 */
    tags?: Record<string, any>;
  }

  interface TinyAppQueryTraceRequest {
    /** 租户 */
    tenant?: string;
    /** 服务名称 */
    serviceName?: string;
    /** 服务实例 */
    serviceInstanceName?: string;
    /** trace_id 列表 */
    traceIds?: string[];
    /** 服务端点 */
    endpointName?: string;
    /** 查询区间 */
    duration?: Duration;
    /** 最小持续时间 */
    minTraceDuration?: number;
    /** 最大持续时间 */
    maxTraceDuration?: number;
    /** trace 状态 */
    traceState?: TraceState;
    /** 排序规则 */
    queryOrder?: QueryOrder;
    /** 分页规则 */
    paging?: Pagination;
    /** 查询条件 */
    tags?: Tag[];
    /** 小程序 serviceId */
    serviceId?: string;
  }

  interface Topology {
    nodes?: Node[];
    calls?: Call[];
  }

  interface Trace {
    /** span 列表 */
    spans?: Span[];
  }

  interface TraceBrief {
    traces?: BasicTrace[];
  }

  interface AlarmGroupDTO {
    id?: number;
    tenant?: string;
    groupName?: string;
    groupInfo?: GroupInfoDTO;
    creator?: string;
    modifier?: string;
    gmtCreate?: string;
    gmtModified?: string;
    emailAddress?: string;
    ddWebhook?: string;
    dyvmsPhone?: string;
    smsPhone?: string;
    /** 环境类型 */
    envType?: string;
  }

  interface AlarmHistoryDTO {
    id?: number;
    /** 创建时间 */
    gmtCreate?: string;
    /** 修改时间 */
    gmtModified?: string;
    /** 告警时间 */
    alarmTime?: string;
    /** 恢复时间 */
    recoverTime?: string;
    /** 持续时间 */
    duration?: number;
    /** 告警id */
    uniqueId?: string;
    /** 规则名称 */
    ruleName?: string;
    /** 告警级别 */
    alarmLevel?: string;
    /** 触发详情 */
    triggerContent?: string[];
    /** 租户id */
    tenant?: string;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 额外信息 */
    extra?: string;
    /** 环境类型 */
    envType?: string;
  }

  interface AlarmHistoryDetailDTO {
    /** id */
    id?: number;
    /** 告警时间 */
    alarmTime?: string;
    /** 告警id */
    uniqueId?: string;
    /** 告警历史id */
    historyId?: number;
    /** 租户id */
    tenant?: string;
    /** 触发方式简述 */
    alarmContent?: string;
    /** 数据源信息 */
    datasource?: string;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 环境类型 */
    envType?: string;
  }

  interface AlarmRuleDTO {
    /** id */
    id?: number;
    /** 创建时间 */
    gmtCreate?: string;
    /** 修改时间 */
    gmtModified?: string;
    /** 规则名称 */
    ruleName?: string;
    /** 规则类型（AI、RULE） */
    ruleType?: string;
    /** 创建者 */
    creator?: string;
    /** 修改者 */
    modifier?: string;
    /** 告警级别 */
    alarmLevel?: string;
    /** 规则描述 */
    ruleDescribe?: string;
    /** 规则是否生效 */
    status?: string;
    /** 合并是否开启 */
    isMerge?: string;
    /** 合并方式 */
    mergeType?: string;
    /** 恢复通知是否开启 */
    recover?: string;
    /** 通知方式 */
    noticeType?: string;
    /** 触发方式简述 */
    alarmContent?: string[];
    /** 租户id */
    tenant?: string;
    /** 告警规则 */
    rule?: Record<string, any>;
    /** 生效时间 */
    timeFilter?: Record<string, any>;
    /** 屏蔽id */
    blockId?: number;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 额外信息 */
    extra?: AlertRuleExtra;
    /** 环境类型 */
    envType?: string;
    /** pql */
    pql?: string;
  }

  interface AlarmSubscribeDTO {
    uniqueId?: string;
    alarmSubscribe?: AlarmSubscribeInfo[];
    /** 环境类型 */
    envType?: string;
  }

  interface AlarmSubscribeInfo {
    id?: number;
    /** 订阅者 */
    subscriber?: string;
    /** 订阅组id */
    groupId?: number;
    /** 告警id */
    uniqueId?: string;
    /** 通知方式 */
    noticeType?: string[];
    /** 通知是否生效 */
    status?: string;
    /** 租户id */
    tenant?: string;
  }

  interface AlertRuleExtra {
    notificationConfig?: NotificationConfig;
    sourceLink?: string;
    alertTags?: string[];
    tagAlias?: Record<string, any>;
  }

  interface AntLogAgentConfig {
    /** 分段符 */
    logBeginRegex?: string;
  }

  interface AntLogIndexConfig {
    line?: AntLogIndexConfigLine;
  }

  interface AntLogIndexConfigLine {
    /** 大小写敏感 */
    caseSensitive?: boolean;
    /** 是否包含中文 */
    chn?: boolean;
    /** 分隔符 */
    tokens?: string[];
  }

  interface BasicTrace {
    segmentId?: string;
    serviceNames?: string[];
    serviceInstanceNames?: string[];
    endpointNames?: string[];
    duration?: number;
    start?: number;
    isError?: boolean;
    traceIds?: string[];
  }

  interface BizResultConfig {
    /** 字段名 */
    fieldName?: string;
    /** 操作类型 */
    operateType?: string;
    /** 字段值 */
    fieldValue?: string;
    /** 字段类型 */
    fieldType?: string;
  }

  interface Call {
    id?: string;
    sourceId?: string;
    sourceName?: string;
    destId?: string;
    destName?: string;
    component?: string;
    metric?: ResponseMetric;
  }

  interface Duration {
    start?: number;
    end?: number;
    step?: string;
  }

  interface GroupInfoDTO {
    groupInfo?: Record<string, any>;
    personNum?: number;
  }

  interface MapField_String_String_ {}

  interface MicroAppAlarmHistoryDTO {
    id?: number;
    /** 创建时间 */
    gmtCreate?: string;
    /** 修改时间 */
    gmtModified?: string;
    /** 告警时间 */
    alarmTime?: string;
    /** 恢复时间 */
    recoverTime?: string;
    /** 持续时间 */
    duration?: number;
    /** 告警id */
    uniqueId?: string;
    /** 规则名称 */
    ruleName?: string;
    /** 告警级别 */
    alarmLevel?: string;
    /** 触发详情 */
    triggerContent?: string[];
    /** 租户id */
    tenant?: string;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 额外信息 */
    extra?: string;
    /** 环境类型 */
    envType?: string;
    appId?: string;
    envId?: string;
    serviceId?: string;
  }

  interface MicroAppAlarmHistoryDetailDTO {
    /** id */
    id?: number;
    /** 告警时间 */
    alarmTime?: string;
    /** 告警id */
    uniqueId?: string;
    /** 告警历史id */
    historyId?: number;
    /** 租户id */
    tenant?: string;
    /** 触发方式简述 */
    alarmContent?: string;
    /** 数据源信息 */
    datasource?: string;
    /** 来源类型 */
    sourceType?: string;
    /** 来源id */
    sourceId?: number;
    /** 环境类型 */
    envType?: string;
    appId?: string;
    envId?: string;
    serviceId?: string;
  }

  interface Node {
    id?: string;
    name?: string;
    type?: string;
    isReal?: boolean;
    metric?: ResponseMetric;
  }

  interface NotificationConfig {
    dingtalkTemplate?: NotificationTemplate;
    webhookTemplate?: NotificationTemplate;
  }

  interface NotificationTemplate {
    fieldMap?: LinkedHashMap_String_AlertTemplateField_;
  }

  interface Pagination {
    pageNum?: number;
    pageSize?: number;
  }

  interface ResponseMetric {
    avgLatency?: number;
    p95Latency?: number;
    p99Latency?: number;
    totalCount?: number;
    errorCount?: number;
    successRate?: number;
  }

  interface Span {
    traceId?: string;
    segmentId?: string;
    spanId?: string;
    parentSpanId?: string;
    refs?: Ref[];
    serviceCode?: string;
    serviceInstanceName?: string;
    startTime?: number;
    endTime?: number;
    endpointName?: string;
    type?: string;
    peer?: string;
    component?: string;
    isError?: boolean;
    layer?: string;
    tags?: KeyValue[];
    logs?: LogEntity[];
    isRoot?: boolean;
    segmentSpanId?: string;
    segmentParentSpanId?: string;
  }

  interface Tag {
    key?: string;
    value?: string;
  }

  interface KeyValue {
    key?: string;
    value?: string;
  }

  interface LinkedHashMap_String_AlertTemplateField_ {}

  interface LogEntity {
    time?: number;
    data?: KeyValue[];
  }

  interface Ref {
    traceId?: string;
    parentSegmentId?: string;
    parentSpanId?: string;
    type?: RefType;
  }
  interface ZSeagullResult_List_ServerDTO__ {
    errorCode?: ErrorCode;
    errorMessage?: string;
    hostname?: string;
    traceId?: string;
    success?: boolean;
    data?: ServerDTO[];
  }
}
