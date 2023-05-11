import { Tag, Progress, Button, Radio } from 'antd';
import React, { useState, useEffect, useRef } from 'react';
import { Link, history } from 'umi';
import {
    getTenantServerByCondition,
    fuzzyQueryByTenantServer,
    metricDataQuery,
} from '@/services/infra/api';
import { SyncOutlined, MinusCircleOutlined } from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import moment from 'moment';
import _ from 'lodash';
import { getUrlQueryString } from '../../utils/help';
import MetaView from './MetaView';
import styles from './index.less';
import $i18n from '../../i18n';
import queryString from 'query-string';

let intervalId: any = null;
const InfrastructureList: React.FC = (props: any) => {
    const initialState = props.initialState;
    const workSpaceobj: any = props.getWorkSpace ? props.getWorkSpace() : []
    const { isCloudRun, appId, envId } = props.getCloudRun ? props.getCloudRun() : {}
    const actionRef = useRef();
    const [envValue, setEnvValue] = useState(
        isCloudRun ? '' : queryString.parse(location.search)?.workspace,
    );
    const [dimDatas, setDimDatas] = useState<any[]>([]);
    const [activeKey, setActiveKey] = useState('POD');
    const columns = [
        {
            title: 'HostName',
            dataIndex: 'hostname',
            key: 'hostname',
            width: 200,
            fixed: 'left',
            render: (text: any, record: any) => {
                return (
                    <Link
                        to={`/infra/metric/${record['_type'] ? record['_type'].toLowerCase() : record['_type']
                            }/${record['namespace'] ? record['namespace'] : '-'}/${record.hostname
                            }`}
                    >
                        {text}
                    </Link>
                );
            },
        },

        {
            title: 'IP',
            dataIndex: 'ip',
            key: 'ip',
            width: 150,
        },

        {
            title: $i18n.get({ id: 'holoinsight.pages.infra.Type', dm: '类型' }),
            dataIndex: '_type',
            hideInSearch: true,
            key: '_type',
            width: 100,
        },

        {
            title: $i18n.get({ id: 'holoinsight.pages.infra.Status', dm: '状态' }),
            dataIndex: '_status',
            key: '_status',
            width: 150,
            hideInSearch: true,
            render: (text: any) => {
                if (text === 'ONLINE') {
                    return (
                        <Tag icon={<SyncOutlined spin />} color="cyan">
                            ACTIVE
                        </Tag>
                    );
                }
                return (
                    <Tag icon={<MinusCircleOutlined />} color="default">
                        UNACTIVE
                    </Tag>
                );
            },
        },

        {
            title: $i18n.get({ id: 'holoinsight.pages.infra.Label', dm: '标签' }),
            dataIndex: 'labels',
            hideInSearch: true,
            key: 'labels',
            width: 400,
            render: (v: any) => {
                if (Object.prototype.toString.call(v) === '[object Object]') {
                    const keyArr = Object.keys(v);
                    return (
                        <div className={styles.tags}>
                            {keyArr.map((item, index) => {
                                return (
                                    <div key={index}>
                                        <Tag color="cyan">{`${item} = ${v[item]}`}</Tag>
                                    </div>
                                );
                            })}
                        </div>
                    );
                } else {
                    return (
                        <Tag color="blue">
                            {$i18n.get({
                                id: 'holoinsight.pages.infra.NoLabel',
                                dm: '暂无标签',
                            })}
                        </Tag>
                    );
                }
            },
        },

        {
            title: 'CPU',
            hideInSearch: true,
            dataIndex: 'cpu_util',
            key: 'cpu_util',
            width: 200,
            render: (text: any) => {
                if (text && text?.[0]?.[1]) {
                    return <Progress steps={3} percent={_.ceil(text?.[0]?.[1], 2)} />;
                }
                return text?.[0]?.[1];
            },
        },

        {
            title: 'MEM',
            dataIndex: 'mem_util',
            hideInSearch: true,
            key: 'mem_util',
            width: 200,
            render: (text: any) => {
                if (text && text?.[0]?.[1]) {
                    return <Progress steps={3} percent={_.ceil(text?.[0]?.[1], 2)} />;
                }
                return text?.[0]?.[1];
            },
        },

        {
            title: $i18n.get({
                id: 'holoinsight.pages.infra.UpdateTime',
                dm: '更新时间',
            }),
            dataIndex: '_modified',
            hideInSearch: true,
            key: '_modified',
            width: 300,
            render: (v: moment.MomentInput) => {
                return <span>{v && moment(v).format('YYYY-MM-DD HH:mm:ss')}</span>;
            },
        },

        {
            title: $i18n.get({ id: 'holoinsight.pages.infra.Operation', dm: '操作' }),
            valueType: 'option',
            width: 100,
            fixed: 'right',
            render: (text: any, record: any) => (
                <>
                    <MetaView
                        type="view"
                        data={record}
                        onShowDrawer={onShowDrawer}
                        onCloseDrawer={onCloseDrawer}
                    />
                </>
            ),
        },
    ];

    const onShowDrawer = () => {
        clearInterval(intervalId);
    };

    const onCloseDrawer = () => {
    };

    useEffect(() => {
        return () => clearInterval(intervalId);
    }, []);

    useEffect(() => {
        if (envValue) {
            actionRef?.current?.reload();
        }
    }, [envValue]);

    const metricDataQueryFunc = async (dataResult: any[]) => {
        let metricArr: any = [];
        let metricIpMap: any = {};
        let keyName = 'hostname';
        if (activeKey === 'VM') {
            metricArr = ['system_cpu_util', 'system_mem_util'];
        } else if (activeKey === 'POD') {
            metricArr = ['k8s_pod_cpu_util', 'k8s_pod_mem_util'];
            keyName = 'pod';
        } else if (activeKey === 'NODE') {
            metricArr = ['k8s_node_cpu_util', 'k8s_node_mem_util'];
            keyName = 'name';
        }
        const endTime = new Date().getTime() - 120000;
        const filters = isCloudRun
            ? [
                {
                    name: 'appId',
                    type: 'literal_or',
                    value: appId,
                },
                {
                    name: 'envId',
                    type: 'literal_or',
                    value: envId,
                },
            ]
            : [];
        const dataSource = [
            {
                metric: metricArr[0],
                aggregator: 'none',
                filters: filters,
                start: endTime - 180000,
                end: endTime,
            },
            {
                metric: metricArr[1],
                aggregator: 'none',
                filters: filters,
                start: endTime - 180000,
                end: endTime,
            },
        ];
        if (envValue) {
            dataSource.forEach((item: any) => {
                item.filters = [
                    {
                        name: 'workspace',
                        type: 'literal_or',
                        value: envValue,
                    },
                ];
            });
        }
        const res = await metricDataQuery({
            datasources: dataSource,
            currentTenant:initialState.currentTenant
        });

        if (res && res.results && res.results.length > 0) {
            _.forEach(res.results, (re: any) => {
                const { metric, tags, values } = re;
                if (!metricIpMap[tags?.[keyName]]) {
                    metricIpMap[tags?.[keyName]] = {};
                }

                let newMetric = metric;
                if (
                    metric === 'system_cpu_util' ||
                    metric === 'k8s_pod_cpu_util' ||
                    metric === 'k8s_node_cpu_util'
                ) {
                    newMetric = 'cpu_util';
                } else if (
                    metric === 'system_mem_util' ||
                    metric === 'k8s_pod_mem_util' ||
                    metric === 'k8s_node_mem_util'
                ) {
                    newMetric = 'mem_util';
                }
                metricIpMap[tags?.[keyName]][newMetric] = values;
            });
        }
        const ddata = _.orderBy(
            dataResult.map((data: any) => {
                if (data && data['name'] && metricIpMap[data['name']]) {
                    data = Object.assign({ ...data }, metricIpMap[data['name']]);
                }
                return data;
            }),
            ['_modified'],
            ['desc'],
        );

        return ddata;
    };
    function handleChangeEnv(e: any) {
        history.replace({
            pathname: getUrlQueryString(),
            search: queryString.stringify({
                ...queryString.parse(location.search),
                workspace: e.target.value,
            }),
        });
        setEnvValue(e.target.value);
    }

    const appSite = props.isCloudrunSite ? props.isCloudrunSite() ? true : false : false

    return (
        <div className={styles.infra}>
            <ProTable
                columns={columns}
                actionRef={actionRef}
                scroll={{ x: 1500 }}
                request={async (params, sorter, filter) => {
                    let dataSource = [];
                    if (params.ip || params.hostname) {
                        const condition: any = {
                            ip: params?.ip,
                            hostname: params?.hostname,
                            _type: activeKey,
                        };
                        if (envValue) {
                            condition['_workspace'] = envValue;
                        }
                        const data = await fuzzyQueryByTenantServer(condition);
                        if (data) {
                            dataSource = await metricDataQueryFunc(data);
                            setDimDatas(dataSource);
                        }
                    } else {
                        const conditions: any = {
                            _type: [activeKey],
                        };
                        if (envValue) {
                            conditions['_workspace'] = envValue;
                        }
                        const dataResult = await getTenantServerByCondition(conditions);
                        if (dataResult) {
                            const ddata = await metricDataQueryFunc(dataResult);

                            dataSource = ddata.filter((item) => {
                                return item._type === activeKey;
                            });
                            setDimDatas(dataSource);
                        }
                    }

                    return {
                        data: dataSource,
                        total: dataSource.length,
                        success: true,
                    };
                }}
                rowKey="key"
                pagination={{
                    pageSize: 10,
                    total: dimDatas?.length,
                    showQuickJumper: true,
                }}
                search={{
                    optionRender: (searchConfig, { form }, dom) => {
                        return [...dom];
                    },
                }}
                headerTitle={$i18n.get({
                    id: 'holoinsight.pages.infra.ListOfBasicResources',
                    dm: '基础资源列表',
                })}
                options={false}
                dateFormatter="string"
                toolBarRender={() => [
                    <Radio.Group
                        value={envValue}
                        optionType="button"
                        onChange={(e: any) => handleChangeEnv(e)}
                        options={(workSpaceobj?.workspaces || []).map((item: any) => {
                            return {
                                label: item.desc,
                                value: item.name,
                            };
                        })}
                    >
                        {' '}
                    </Radio.Group>,
                    !appSite ? (
                        <div className={styles['radio-box']}>
                            <Radio.Group
                                onChange={(e) => {
                                    setActiveKey(e.target.value);
                                    actionRef.current.reload();
                                }}
                                defaultValue="POD"
                            >
                                <Radio.Button value="VM">VM</Radio.Button>
                                <Radio.Button value="POD">POD</Radio.Button>
                                <Radio.Button value="NODE">NODE</Radio.Button>
                            </Radio.Group>
                        </div>
                    ) : (
                        <span></span>
                    ),
                    <Button
                        type="primary"
                        onClick={() => {
                            history.push('/integration/agent');
                        }}
                    >
                        {$i18n.get({
                            id: 'holoinsight.pages.infra.AgentInstallation',
                            dm: 'Agent 安装',
                        })}
                    </Button>,
                ]}
            />
        </div>
    );
};
export default InfrastructureList;
