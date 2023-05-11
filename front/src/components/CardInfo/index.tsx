import  { useEffect, useState } from 'react';
import $i18n from '../../i18n';
import styles from './index.less';

export default function CardInfo(props: any) {
  const { treeNode, checkName } = props;
  const [cardMap, setCardMap] = useState([]);
  useEffect(() => {
    formatterData();
  }, [checkName]);

  function formatterData() {
    function getItemData(data: any, name:any) {
      data.forEach((element: any) => {
        if (element.name === checkName) {
          const backData = Object.keys(element.data || {})
            .map((item) => {
              switch (item) {
                case 'avgLatency':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.AverageResponseTimeMs',
                      dm: '平均响应时间(ms)',
                    }),
                    value: (element?.data?.avgLatency).toFixed(1),
                  };
                case 'p95Latency':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.PResponseTimeMs',
                      dm: 'P95响应时间(ms)',
                    }),
                    value: (element?.data?.p95Latency).toFixed(1),
                  };
                case 'p99Latency':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.PResponseTimeMs.1',
                      dm: 'P99响应时间(ms)',
                    }),
                    value: (element?.data?.p99Latency).toFixed(1),
                  };
                case 'otalCount':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.TotalRequests',
                      dm: '总请求次数',
                    }),
                    value: element?.data?.totalCount,
                  };
                case 'errorCount':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.NumberOfErrorRequests',
                      dm: '错误请求次数',
                    }),
                    value: element?.data?.errorCount,
                  };
                case 'successRate':
                  return {
                    title: $i18n.get({
                      id: 'holoinsight.components.CardInfo.RequestSuccessRate',
                      dm: '请求成功率（%）',
                    }),
                    value: Math.floor(element?.data?.successRate * 1000) / 1000,
                  };
                default:
                  return null;
              }
            })
            .filter((item) => item !== null);
          setCardMap(backData);
        } else if (element.children.length) {
          getItemData(element.children, name);
        }
      });
    }
    getItemData([treeNode], checkName);
  }

  return (
    <>
      {cardMap.length
        ? cardMap.map((item: any, index: number) => {
            return (
              <div className={styles.item} key={index}>
                <p>{item.title}</p>
                <div className={styles.content}>
                  <span className={styles.title}>{item.value}</span>
                  <div className={styles.side}>
                    <div className={styles.desbox}>
                      <span>
                        {$i18n.get({
                          id: 'holoinsight.components.CardInfo.NotAvailable',
                          dm: '暂无',
                        })}
                      </span>
                      <span>
                        {$i18n.get({
                          id: 'holoinsight.components.CardInfo.DailyYoy',
                          dm: '日同比',
                        })}
                      </span>
                    </div>
                    <div className={styles.desbox}>
                      <span>
                        {$i18n.get({
                          id: 'holoinsight.components.CardInfo.NotAvailable',
                          dm: '暂无',
                        })}
                      </span>
                      <span>
                        {$i18n.get({
                          id: 'holoinsight.components.CardInfo.ZhouYoy',
                          dm: '周同比',
                        })}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            );
          })
        : $i18n.get({
            id: 'holoinsight.components.CardInfo.NoData',
            dm: '无数据',
          })}
    </>
  );
}
