import AlarmPanel from '../alarmHistory/AlarmPanel';
import { alramhisotryDetailById } from '@/services/alarm/api';
import { PageHeader } from '@ant-design/pro-components';
import { Button, Card, DatePicker, Tabs } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import $i18n from '../../../i18n';
import PQlChart from '../alarmHistory/PQlChart';
import { HISTORY_INFO } from './const';
import styles from './index.less';
const { TabPane } = Tabs;

const HistoryDrawer: React.FC<any> = (props: any) => {
  const { selectItem, alarmTime, historyId } = props;
  const isPQL = selectItem.uniqueId.includes('pql') ? true : false;
  const [timelineArr, setTimeLineArr] = useState([]);
  const [hasMore, setHasMore] = useState({
    isShow: false,
    total: 0,
  });

  const [changeWay, setChangeWay] = useState(
    alarmTime && historyId ? 'time' : 'more',
  );
  const [queryParam, setQueryParam] = useState({
    pageNum: 1,
    pageSize: 10,
    target: {
      historyId: selectItem.id || Number(historyId),
      alarmTime: '' || alarmTime,
    },
  });
  function queryData() {
    let params = queryParam;
    if (!params.target.alarmTime) {
      delete params.target.alarmTime;
    }
    alramhisotryDetailById(params).then((res: any) => {
      const newHasMore = { ...hasMore };
      newHasMore.isShow =
        res.totalCount > 10 && res.pageNum < res.totalPage ? true : false;
      newHasMore.total = res.totalCount;
      setHasMore(newHasMore);
      setTimeLineArr(
        changeWay === 'more'
          ? timelineArr.concat(res.items || [])
          : res.items || [],
      );
    });
  }

  useEffect(() => {
    queryData();
  }, [queryParam]);

  function renderChart(datasource: any, index: number) {
    if (isPQL) {
      return <PQlChart pql={datasource} />;
    } else {
      return <AlarmPanel data={datasource} key={index} />;
    }
  }
  function renderTitle(item: any, index: any) {
    let datasource;
    if (typeof item.datasource === 'string') {
      datasource = item.datasource;
    } else {
      datasource = JSON.parse(item.datasource);
    }
    const infoData = Object.keys(HISTORY_INFO).map((m: string) => {
      switch (m) {
        case 'alarmTime':
          return {
            key: m,
            title: HISTORY_INFO[m],
            data: moment(item.alarmTime).format('YYYY-MM-DD HH:mm:ss'),
          };
        case 'alarmDes':
          if (isPQL) {
            return {
              key: m,
              title: HISTORY_INFO[m],
              data: [],
            };
          } else {
            return {
              key: m,
              title: HISTORY_INFO[m],
              data: JSON.parse(item.alarmContent),
            };
          }
        default:
          return {};
      }
    });
    return (
      <div key={index}>
        <div>
          {infoData.map((titem: any) => {
            if (titem.key === 'alarmDes') {
              return (
                <div className={styles.detailltem} key={index}>
                  <span className={styles.detailTitle}>{titem?.title}</span>
                  {(titem.data || []).map((m: any, key: number) => {
                    return (
                      <div className={styles.detailValue} key={key}>
                        <span>{m}</span>
                      </div>
                    );
                  })}
                </div>
              );
            } else {
              return (
                <div className={styles.detailltem} key={index}>
                  <span className={styles.detailTitle}>{titem?.title}</span>
                  <span className={styles.detailValue}>{titem?.data}</span>
                </div>
              );
            }
          })}
          {/* <Button type="link" size="small" onClick={() => handleOpenChart()}>
            {item.showChart
              ? $i18n.get({
                  id: 'holoinsight.alarm.historyDrawer.Hide',
                  dm: '隐藏',
                })
              : $i18n.get({
                  id: 'holoinsight.alarm.historyDrawer.Chatu',
                  dm: '查图',
                })}
          </Button> */}
        </div>
        {renderChart(datasource, index)}
        {/* <AlarmPanel data={datasource} key={index} /> */}
      </div>
    );
  }

  function handleLoadMore() {
    setChangeWay('more');
    queryParam.pageNum += 1;
    setQueryParam({ ...queryParam });
  }
  function handleChangeTime(v: any) {
    if (v) {
      setChangeWay('time');
      const time = v.format();
      queryParam.target.alarmTime = time;
      setQueryParam({ ...queryParam });
    } else {
      setChangeWay('more');
      queryParam.target.alarmTime = '';
      setQueryParam({ ...queryParam });
    }
  }
  return (
    <PageHeader
      title={$i18n.get(
        {
          id: 'holoinsight.alarm.historyDrawer.AlarmConfigurationNameSelectitemrulename',
          dm: '报警配置名称: {selectItemRuleName}',
        },

        { selectItemRuleName: selectItem.ruleName },
      )}
    >
      <Tabs
        defaultValue={'1'}
        tabBarExtraContent={
          queryParam.target.alarmTime ? (
            <DatePicker
              defaultValue={moment(queryParam.target.alarmTime)}
              showTime
              onChange={(v) => handleChangeTime(v)}
            />
          ) : (
            <DatePicker showTime onChange={(v) => handleChangeTime(v)} />
          )
        }
      >
        <TabPane
          tab={$i18n.get({
            id: 'holoinsight.alarm.historyDrawer.AlarmEvent',
            dm: '报警事件',
          })}
          key="1"
        >
          {timelineArr.map((item, index) => {
            return (
              <Card className={styles.cardInfo} key={index}>
                {renderTitle(item, index)}
              </Card>
            );
          })}
          {hasMore.isShow ? (
            <Button
              type="primary"
              style={{ marginTop: 16 }}
              onClick={() => handleLoadMore()}
            >
              {$i18n.get({
                id: 'holoinsight.alarm.historyDrawer.LoadMore',
                dm: '加载更多',
              })}
            </Button>
          ) : (
            <div>
              {$i18n.get({
                id: 'holoinsight.alarm.historyDrawer.NoMoreAlarmHistoryIs',
                dm: '暂无更多告警历史！',
              })}
            </div>
          )}
        </TabPane>
      </Tabs>
    </PageHeader>
  );
};

export default HistoryDrawer;
