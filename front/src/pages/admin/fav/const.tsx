import type { ProColumns } from '@ant-design/pro-table';
import $i18n from '@/i18n';
import { polishingUrl } from '../../../utils/help';

export type TableListItem = {
  id: number;
  userLoginName: string;
  type: string;
  tenant: string;
  relateId: string;
  gmtCreate: any;
};

export const columns: ProColumns<TableListItem>[] = [
  {
    title: $i18n.get({
      id: 'holoinsight.admin.fav.UserFavorite.Object',
      dm: '对象',
    }),
    dataIndex: 'type',
    key: 'type',
    valueEnum: {
      dashboard: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.Dashboard',
          dm: '仪表盘',
        }),
      },
      logmonitor: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.LogMonitoring',
          dm: '日志监控',
        }),
      },
      app: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.ApplicationMonitoring',
          dm: '应用监控',
        }),
      },
      integration: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.IntegratedComponentLibrary',
          dm: '集成组件库',
        }),
      },
      folder: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.Folder',
          dm: '文件夹',
        }),
      },
      infra: {
        text: $i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.infra',
          dm: '基础设施',
        }),
      },
    },
  },
  {
    title: $i18n.get({
      id: 'holoinsight.admin.fav.UserFavorite.AssociatedInformation',
      dm: '关联信息',
    }),
    dataIndex: 'name',
    key: 'name',
  },

  {
    title: $i18n.get({
      id: 'holoinsight.admin.fav.UserFavorite.Link',
      dm: '链接',
    }),
    dataIndex: 'url',
    key: 'url',
    search: false,
    render: (text: any) => (
      <a href={polishingUrl(text)} target="_blank" rel="noreferrer">
        {$i18n.get({
          id: 'holoinsight.admin.fav.UserFavorite.Jump',
          dm: '跳转',
        })}
      </a>
    ),
  },

  {
    title: $i18n.get({
      id: 'holoinsight.admin.fav.UserFavorite.CreationTime',
      dm: '创建时间',
    }),
    dataIndex: 'gmtCreate',
    key: 'gmtCreate',
    valueType: 'dateTime',
    search: false,
  },
];
