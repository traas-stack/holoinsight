import { Typography } from 'antd';
import  { useEffect, useState } from 'react';
import $i18n from '../../../../../i18n';
import styles from './index.less';
const { Paragraph } = Typography;
interface ToopTopsIProps {
  data: any;
  x: any;
  y: any;
  show: boolean;
  allShow: any;
  setAllShow: any;
}
const ToolTips = ({
  data,
  x,
  y,
  show,
  allShow,
  setAllShow,
}: ToopTopsIProps) => {
  const [mesList, setMesList] = useState<any>([]);
  useEffect(() => {
    if (data.metric && Object.keys(data.metric).length) {
      const {
        totalCount = 0,
        errorCount = 0,
        successRate = 0,
        avgLatency = 0,
        p95Latency = 0,
        p99Latency = 0,
      } = data.metric;

      const errorGrowth = (errorCount / totalCount).toFixed(1);
      const messageList = [
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.AverageResponseTime',
            dm: '平均响应时间',
          }),
          value: avgLatency ? avgLatency.toFixed(1) : '-',
        },
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.PResponseTime',
            dm: 'P95响应时间',
          }),
          value: p95Latency ? p95Latency.toFixed(1) : '-',
        },
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.PResponseTime.1',
            dm: 'P99响应时间',
          }),
          value: p99Latency ? p99Latency.toFixed(1) : '-',
        },
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.TotalNumberOfTimes',
            dm: '总次数',
          }),
          value: totalCount ? totalCount : '-',
        },
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.NumberOfErrors',
            dm: '错误数',
          }),
          value:
            Number(errorGrowth) > 0.3 && errorCount > 10 ? (
              <>
                <span style={{ color: 'red' }}>
                  {errorCount ? errorCount : '-'}
                </span>
                {$i18n.get({
                  id: 'holoinsight.applicationTopo.ToolTips.Greater',
                  dm: '大于',
                })}
                <span style={{ color: 'red' }}>10</span>
              </>
            ) : (
              <span style={{ color: 'red' }}>
                {errorCount ? errorCount : '-'}
              </span>
            ),
        },
        {
          label: $i18n.get({
            id: 'holoinsight.applicationTopo.ToolTips.SuccessRate',
            dm: '成功率',
          }),
          value:
            successRate < 99 ? (
              <>
                <span style={{ color: 'red' }}>
                  {successRate ? Math.floor(successRate * 1000) / 1000 : '-'}%
                </span>
                {$i18n.get({
                  id: 'holoinsight.applicationTopo.ToolTips.Less',
                  dm: '小于',
                })}
                <span style={{ color: 'red' }}>99%</span>
              </>
            ) : (
              <span>{successRate ? Math.floor(successRate * 1000) / 1000 : '-'}%</span>
            ),
        },
      ];

      setMesList(messageList);
    } else {
      setMesList([]);
    }
  }, [data]);

  const onMouseLeave = () => {
    setTimeout(() => {
      setAllShow(false);
    });
  };
  const onMouseEnter = () => {
    setAllShow(true);
  };

  return (
    (allShow || show) && (
      <div
        className={styles.tooltip}
        style={{
          position: 'absolute',
          left: x + 'px',
          top: y + 'px',
        }}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      >
        {data['@type'] === 'call' ? (
          <>
            <div className={styles.box}>
              <div className={mesList.length > 0 ? styles.label : styles.name} >
                {$i18n.get({
                  id: 'holoinsight.applicationTopo.ToolTips.StartNode',
                  dm: '起始节点：',
                })}
              </div>
              <div className={styles.value}>
                <Paragraph copyable className={styles.nameValue}>
                  {data.sourceName}
                </Paragraph>
              </div>
            </div>
            <div className={styles.box}>
              <div className={mesList.length > 0 ? styles.label : styles.name} >
                {$i18n.get({
                  id: 'holoinsight.applicationTopo.ToolTips.EndNode',
                  dm: '结束节点：',
                })}
              </div>
              <div className={styles.value}>
                <Paragraph copyable className={styles.nameValue}>
                  {data.destName}
                </Paragraph>
              </div>
            </div>
          </>
        ) : (
          <div className={styles.box}>
            <div className={mesList.length > 0 ? styles.label : styles.name} >
              {$i18n.get({
                id: 'holoinsight.applicationTopo.ToolTips.ServiceName',
                dm: '服务名称：',
              })}
            </div>
            <div className={styles.value}>
              <Paragraph copyable className={styles.nameValue}>
                {data.name}
              </Paragraph>
            </div>
          </div>
        )}

        {
          mesList.length > 0
            ? mesList.map((p) => {
              return (
                <div className={styles.box}>
                  <div className={styles.label}>{p.label}：</div>
                  <div className={styles.value}>{p.value}</div>
                </div>
              );
            })
            : null
          //   <div className={styles.empty}>暂无metric数据</div>
        }
      </div>
    )
  );
};
export default ToolTips;
