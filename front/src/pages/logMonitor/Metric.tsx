import React, { useState, useEffect } from 'react';
import { Button, message, Dropdown, Space, Steps } from 'antd';
import AlarmHistory from '@/pages/alarm/alarmHistory';
import OpsHistory from '@/pages/admin/ops/OpsHistory';
import LogMonitor from './LogMonitorEdit';
import {
  EllipsisOutlined,
  EditOutlined,
  EyeOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';
import { queryById } from '@/services/customplugin/api';
import { getQueryPath } from '@/services/customplugin/folderApi';
import AlarmRuler from '@/pages/alarm/alarmSubscription';
import CommonBreadcrumb from '@/components/CommonBreadcrumb';
import classNames from 'classnames';
import LogDashboardNew from '@/components/Dashboard/logDashboardNew';
import styles from './index.less';
import { history } from 'umi';
import $i18n from '../../i18n';
import { getQueryString } from '@/utils/help';
import qs from 'query-string';
import UserInfo from '../dashboardMagi/userInfo';
import { ClearKey } from './ClearKey';

const { Step } = Steps;

const currentMap = {
  edit: 0,
  alarm: 1,
  alramHistory: 3,
  opsHistory: 4
}

const Dashboard: React.FC = (props: any) => {
  const id = props.match.params.id;
  const parentId = getQueryString('parentId');
  const logMonitor = getQueryString('logMonitor');
  const [breadConfig, setBreadConfig] = useState([]);
  function initialViewFun() {
    if (logMonitor === "alarm" || logMonitor === 'edit') {
      return 'logMonitor'

    } else {
      return logMonitor
    }

  }
  const [view, setView] = useState(initialViewFun() || 'chart');
  const [current, setCurrent] = useState(currentMap[logMonitor] || 0);
  const [metricArr, setMetricArr] = useState([]);
  const [title, setTitile] = useState('');
  const [periodNum, setPeriodNum] = useState<String>('');
  const [tagsNameArr, setTagsNameArr] = useState([])

  useEffect(() => {
    getQueryPath({
      requests: [
        {
          customPluginId: Number(id),
          includePluginName: true,
        },
      ],
    }).then((res) => {
      if (res) {
        const bread = res[0].paths || [];
        bread.reverse().unshift({
          name: $i18n.get({
            id: 'holoinsight.pages.logMonitor.Metric.RootDirectory',
            dm: '根目录',
          }),
          url: '/log/',
        });

        const newBreadConfig = bread.map((item:any) => {
          let newItem = {
            name: '',
            url: '',
          };

          newItem.name = item.name;
          newItem.url = `/log/${item.id || -1}`;
          return newItem;
        });
        setBreadConfig(newBreadConfig);
      }
    });
    queryById(props.match.params.id).then((res: any) => {
      let time: string
      if (res?.periodType === 'FIVE_SECOND') {
        time = '5s'
      } else if (res?.periodType === 'SECOND') {
        time = '1s'
      } else {
        time = '1m'
      }
      setPeriodNum(time);
      setTitile(res.name);
      setMetricArr(res?.conf?.collectMetrics || []);
      let tagsNameArr = res?.conf?.collectMetrics.map((item:any) => {
        return item.targetTable
      })
      setTagsNameArr(tagsNameArr)
    });
  }, []);

  function handleChangeDrop(key: any) {
    let newView = key === '3' ? 'alramHistory' : key === '4' ? 'opsHistory' : 'chart';
    history.push({
      pathname: `/log/metric/${id}`,
      search: qs.stringify({
        parentId: parentId,
        logMonitor: newView,
      }),
    });

    setView(newView);
  }



  const clearKey = () => {
    let newView = 'clearKey'
    history.push({
      pathname: `/log/metric/${id}`,
      search: qs.stringify({
        parentId: parentId,
        logMonitor: newView,
      }),
    });
    setView(newView);
  }
  function copyText(res) {
    navigator.clipboard.writeText(res).then(() => {
      message.success(
        '导出成功，JSON配置已复制到剪切板'
      );
    }).catch(() => {
      message.error(
        '导出失败'
      );
    })
  }
  const copyJson = async () => {
    const res:any = await queryById(id)
    res.id = res.parentFolderId = null
    copyText(JSON.stringify(res))
  }
  const items = [
    {
      key: '3',
      label: (
        <div onClick={() => handleChangeDrop('3')}> {$i18n.get({
          id: 'holoinsight.pages.logMonitor.Metric.EarlyWarningHistory',
          dm: '预警历史',
        })}</div>

      )
    },
    {
      key: '4',
      label: (
        <div onClick={() => handleChangeDrop('4')}> {$i18n.get({
          id: 'holoinsight.pages.logMonitor.Metric.OperationHistory',
          dm: '操作历史',
        })}</div>

      )
    },
    {
      key: '5',
      label: (
        <div onClick={clearKey}>清理key</div>

      )
    },
    {
      key: '6',
      label: (
        <div onClick={copyJson}>导出配置</div>
      )
    },


  ]

  return (
    <>
      <CommonBreadcrumb urlList={breadConfig} to="chart" />
      {location.search.includes("logMonitor=") ? <UserInfo /> : null}
      <div className={classNames(styles.topOperation, view === 'chart' ? '' : styles.mgb10)}>
        <Dropdown menu={{ items }}>
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              <EllipsisOutlined className={styles.ellipsisIcon} />
            </Space>
          </a>
        </Dropdown>

        {view !== 'logMonitor' ? (
          <>
            <Button
              onClick={() => {
                setView('logMonitor');
                history.push({
                  pathname: `/log/metric/${id}`,
                  search: qs.stringify({
                    parentId: parentId,
                    logMonitor: 'edit',
                  }),
                });
                setCurrent(0);
              }}
              icon={<EditOutlined />}
            >
              {$i18n.get({ id: 'holoinsight.pages.logMonitor.Metric.Edit', dm: '编辑' })}
            </Button>

            <Button
              className={styles.mgr10}
              onClick={() => {
                setView('logMonitor');
                history.push({
                  pathname: `/log/metric/${id}`,
                  search: qs.stringify({
                    parentId: parentId,
                    logMonitor: 'alarm',
                  }),
                });

                setCurrent(1);
              }}
              icon={<UnorderedListOutlined />}
            >
              {$i18n.get({ id: 'holoinsight.pages.logMonitor.Metric.AlertRules', dm: '告警规则' })}
            </Button>
          </>
        ) : null}

        {view === 'chart' ? null : (
          <Button
            className={styles.mgr10}
            icon={<EyeOutlined />}
            onClick={() => {
              history.push({
                pathname: `/log/metric/${id}`,
                search: qs.stringify({
                  parentId: parentId,
                }),
              });

              setView('chart');
            }}
          >
            {$i18n.get({ id: 'holoinsight.pages.logMonitor.Metric.ViewData', dm: '查看数据' })}
          </Button>
        )}
      </div>

      {view === 'chart' ? (
        <>
          <LogDashboardNew
            periodNum={periodNum}
            metricArr={metricArr}
            title={title}
            type={props.match.params.id}
            favoriteConfig={{
              url: `/log/metric/${props.match.params.id}?parentId=${parentId}`,
              type: 'logmonitor',
              relateId: props.match.params.id,
            }}
          />
        </>
      ) : view === 'alramHistory' ? (
        <AlarmHistory id={id} />
      ) : view === 'opsHistory' ? (
        <OpsHistory tableEntityId={id} />
      ) : view === 'logMonitor' ? (
        <>
          <Steps
            type="navigation"
            current={current}
            onChange={(value) => {
              history.push({
                pathname: `/log/metric/${id}`,
                search: qs.stringify({
                  parentId: parentId,
                  logMonitor: value === 0 ? 'edit' : 'alarm',
                }),
              });

              setCurrent(value);
            }}
            className={styles.steps}
          >
            <Step
              status={current === 1 ? 'finish' : 'process'}
              title={$i18n.get({
                id: 'holoinsight.pages.logMonitor.Metric.CollectionConfiguration',
                dm: '采集配置',
              })}
            />

            <Step
              status={current === 0 ? 'finish' : 'process'}
              title={$i18n.get({
                id: 'holoinsight.pages.logMonitor.Metric.AlertConfiguration',
                dm: '告警配置',
              })}
            />
          </Steps>

          {current ? (
            <AlarmRuler from="comp" id={id} type="log" />
          ) : (
            <LogMonitor action="edit" parentId={parentId} id={id} notBead={true} />
          )}
        </>
      ) : view === 'clearKey' ? (<ClearKey tagsNameArr={tagsNameArr} />) : null}
    </>
  );
};

export default Dashboard;
