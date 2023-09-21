import { queryMenu } from '@/services/app/api';
import { getQueryString, polishingUrl } from '@/utils/help';
import { history } from 'umi';
import qs from 'query-string';
import { StarOutlined } from '@ant-design/icons';
import { useEffect, useState } from 'react';
import { CUSTEM_TEMPLATE_MAGI } from './const';
import { ProLayout } from '@/Magi'
import {
  userFavoriteCreate,
  userFavoriteDeleteById,
  userFavoriteQueryById,
} from '@/services/tenant/favoriteApi';
import { message } from 'antd';
import $i18n from '../i18n';
import styles from './index.less';

export default (props: any) => {
  const [pathname, setPathname] = useState(
    window.location?.pathname || '/app/overview',
  );

  const [subMenu, setSubMenu] = useState([]);
  const [favorite, setFavorite] = useState(false);
  const app: any = getQueryString('app');

  useEffect(() => {
    getMenu();
    userFavoriteQueryById('app', app).then((res: any) => {
      if (res) {
        setFavorite(true);
      }
    });
  }, []);

  async function getMenu() {
    const menuObj: any = await queryMenu(app);
    function backMenu(data: any) {
      return data.map((item: any, index: number) => {
        if (Array.isArray(item.children) && item.children.length) {
          return {
            name: item.name,
            routes: backMenu(item.children),
          };
        } else {
          return {
            path: `${
              CUSTEM_TEMPLATE_MAGI.includes(item.url)
                ? '/app/' + item.url
                : '/app/dashboard/' + item.url
            }`,
            name: item.name,
            template: item.templateId,
            type: item.type,
          };
        }
      });
    }
    const finalMenuArr = backMenu(Array.isArray(menuObj) ? menuObj : []);
    finalMenuArr.push({
      name: $i18n.get({
        id: 'holoinsight.src.layout.apmLayout.AlertConfiguration',
        dm: '告警配置',
      }),
      path: '/app/rule',
      type: 'custom',
      template: '-1',
    });
    setSubMenu(finalMenuArr);
  }
  const themeFavorite: string =
    window?.holoTheme === 'holoLight' ? '#000' : '#fff';
  function renderFavirateTitle() {
    return (
      <div className={styles.titleRender}>
        <h3>{app}</h3>
        <StarOutlined
          onClick={changeFavorite}
          style={{ color: favorite ? 'gold' : themeFavorite }}
          className={styles.icon}
        />
      </div>
    );
  }
  function changeFavorite() {
    if (favorite) {
      userFavoriteDeleteById('app', app).then(() => {
        message.success(
          $i18n.get({
            id: 'holoinsight.src.layout.apmLayout.TheCollectionHasBeenCanceled',
            dm: '取消收藏成功！',
          }),
        );
        setFavorite(false);
      });
    } else {
      userFavoriteCreate({
        url: `/app/overview?app=${app}`,
        type: 'app',
        relateId: app,
        name: app,
      }).then((res) => {
        message.success(
          $i18n.get({
            id: 'holoinsight.src.layout.apmLayout.TheCollectionIsSuccessful',
            dm: '收藏成功！',
          }),
        );
        setFavorite(true);
      });
    }
  }
  return (
    <div className={styles.apmLayout}>
      <ProLayout
        headerRender={() => renderFavirateTitle()}
        title={false}
        headerContentRender={false}
        siderWidth={180}
        navTheme={window?.holoTheme === 'holoLight' ? 'light' : 'dark'}
        route={{
          path: '/app',
          routes: subMenu,
        }}
        location={{
          pathname,
        }}
        menuProps={{
          className: 'route-menu',
        }}
        menuHeaderRender={false}
        pageTitleRender={(props) => {
          return 'Holoinsight';
        }}
        menuItemRender={(item, dom) => (
          <a
            onClick={() => {
              setPathname(item.path || '/app/overview');
              const search = qs.stringify({
                ...qs.parse(location.search),
                id: item.template,
              });
              const path = polishingUrl(item.path || '/app/overview');
              history.push({
                pathname: path,
                search,
              });
            }}
          >
            {dom}
          </a>
        )}
      >
        {props.children}
      </ProLayout>
    </div>
  );
};
