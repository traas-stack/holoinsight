import {
  deleteById,
  userFavoriteQueryAll,
} from '@/services/tenant/favoriteApi';
import { Card, message, Row, Spin, Tabs } from 'antd';
import React, { useEffect, useState } from 'react';
import $i18n from '../../i18n';
import { polishingUrl } from '../../utils/help';
import styles from './index.less';
import TabContainer from './TabContainer';
type TypeItem = {
  id: number;
  url: string;
  description: string;
  name: string;
  relateId: string;
  [propName: string]: any;
};
const Favorite: React.FC = () => {
  const [baseInfo, setbaseInfo] = useState<any>([]);
  const [tabLoading, setTabLoading] = useState(true);

  const getFavouriteList = async () => {
    const data: any = await userFavoriteQueryAll();
    let dashboards: TypeItem[] = [];
    let logs: TypeItem[] = [];
    let infras: TypeItem[] = [];
    let integration: TypeItem[] = [];
    let app: TypeItem[] = [];
    if (data && data.length > 0) {
      data.forEach((d: any) => {
        if (d.type === 'dashboard') {
          dashboards.push({
            id: d.id,
            url: d.url,
            name: d.name,
            relateId: d.relateId,
            type: d.type,
            description: $i18n.get({
              id: 'holoinsight.pages.overview.Favorite.LargePlate',
              dm: '大盘',
            }),
          });
        } else if (d.type === 'logmonitor' || d.type === 'folder') {
          logs.push({
            id: d.id,
            url: d.url,
            name: d.name,
            relateId: d.relateId,
            type: d.type,
            description:
              d.type === 'folder'
                ? $i18n.get({
                    id: 'holoinsight.pages.overview.Favorite.Folder',
                    dm: '文件夹',
                  })
                : $i18n.get({
                    id: 'holoinsight.pages.overview.Favorite.Log',
                    dm: '日志',
                  }),
          });
        } else if (d.type === 'integration') {
          integration.push({
            id: d.id,
            url: d.url,
            name: d.name,
            relateId: d.relateId,
            type: d.type,
            description: $i18n.get({
              id: 'holoinsight.pages.overview.Favorite.Integration',
              dm: '集成',
            }),
          });
        } else if (d.type === 'infra') {
          infras.push({
            id: d.id,
            url: d.url,
            name: d.name,
            relateId: d.relateId,
            type: d.type,
            description: $i18n.get({
              id: 'holoinsight.pages.overview.Favorite.SingleMachine',
              dm: '单机',
            }),
          });
        } else {
          app.push({
            id: d.id,
            url: d.url,
            name: d.name,
            relateId: d.relateId,
            type: d.type,
            description: $i18n.get({
              id: 'holoinsight.pages.overview.Favorite.Application',
              dm: '应用',
            }),
          });
        }
      });
    }
    setbaseInfo([
      {
        key: 'dashboardMonitor',
        title: $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.LargePlateCollection',
          dm: '大盘收藏',
        }),
        i18nId: 'holoinsight.pages.overview.Favorite.LargePlateCollection',
        data: dashboards || [],
      },
      {
        key: 'logMonitor',
        title: $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.LogMonitoringCollection',
          dm: '日志监控收藏',
        }),
        i18nId: 'holoinsight.pages.overview.Favorite.LogMonitoringCollection',
        data: logs || [],
      },
      {
        key: 'infra',
        title: $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.InfrastructureCollection',
          dm: '基础设施收藏',
        }),
        i18nId: 'holoinsight.pages.overview.Favorite.InfrastructureCollection',
        data: infras || [],
      },
      {
        key: 'folder',
        title: $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.IntegratedComponentCollection',
          dm: '集成组件收藏',
        }),
        i18nId:
          'holoinsight.pages.overview.Favorite.IntegratedComponentCollection',
        data: integration || [],
      },
      {
        key: 'app',
        title: $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.AppCollection',
          dm: '应用收藏',
        }),
        i18nId: 'holoinsight.pages.overview.Favorite.AppCollection',
        data: app || [],
      },
    ]);

    return [];
  };

  const toggleFavourite = async (id: any) => {
    const res = await deleteById(id);
    if (res) {
      message.success(
        $i18n.get({
          id: 'holoinsight.pages.overview.Favorite.TheCollectionHasBeenCanceled',
          dm: '取消收藏成功！',
        }),
      );

      getFavouriteList();
    }
  };
  useEffect(() => {
    setTabLoading(true);
    getFavouriteList().then(() => {
      setTabLoading(false);
    });
  }, []);

  const getMoreMenu = (record: any) => {
    const menu = [];
    menu.push({
      key: 'edit',
      label: (
        <a
          target="_blank"
          rel="noopener noreferrer"
          href={polishingUrl(record.url)}
        >
          {$i18n.get({
            id: 'holoinsight.pages.overview.Favorite.NewWindowPreview',
            dm: '新窗口预览',
          })}
        </a>
      ),
    });

    menu.push({
      key: 'cancelFavourite',
      label: (
        <div onClick={() => toggleFavourite(record.id)}>
          {$i18n.get({
            id: 'holoinsight.pages.overview.Favorite.CancelCollection',
            dm: '取消收藏',
          })}
        </div>
      ),
    });

    return { items: menu };
  };

  const tabsItems = baseInfo.map((item: any) => {
    return {
      key: item.key,
      label: (
        <span>
          <span>{item.title}</span>
        </span>
      ),
      children: (
        <Spin spinning={tabLoading} style={{ minHeight: '100px' }}>
          <TabContainer
            data={item.data}
            getMoreMenu={getMoreMenu}
          ></TabContainer>
        </Spin>
      ),
    };
  });

  return (
    <div className={styles.container} style={{ overflow: 'inherit' }}>
      <div className={styles.collection} style={{ padding: 0 }}>
        <Card style={{ marginTop: '20px' }}>
          <Row
            style={{
              alignItems: 'center',
            }}
          >
            <Tabs items={tabsItems}></Tabs>
          </Row>
        </Card>
      </div>
    </div>
  );
};

export default Favorite;
