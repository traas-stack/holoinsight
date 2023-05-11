import $i18n from '@/i18n';
import type { ProColumns } from '@ant-design/pro-table';
import { Divider } from 'antd';
import { polishingUrl } from '../../../utils/help';
import { ContentCompare } from './ContentCompare';

export type TableListItem = {
  id: number;
  creator: string;
  opType: string;
  tableName: string;
  table_entity_id: string;
  gmtCreate: any;
};

export type Props = {
  record: any;
  children: any;
};

export function pasreOpContent(content: any) {
  try {
    return content
      ? typeof JSON.parse(content) === 'string'
        ? JSON.parse(JSON.parse(content))
        : JSON.parse(content)
      : {};
  } catch (e) {}
  return undefined;
}
export const columns: ProColumns<TableListItem>[] = [
  {
    title: $i18n.get({
      id: 'holoinsight.admin.ops.OpsHistory.CreationTime',
      dm: '创建时间',
    }),
    dataIndex: 'gmtCreate',
    key: 'gmtCreate',
    valueType: 'dateTime',
  },

  {
    title: $i18n.get({
      id: 'holoinsight.admin.ops.OpsHistory.Type',
      dm: '类型',
    }),
    dataIndex: 'opType',
    key: 'opType',
    valueEnum: {
      CREATE: {
        text: $i18n.get({
          id: 'holoinsight.admin.ops.OpsHistory.Create',
          dm: '新建',
        }),
        status: 'Default',
      },
      DELETE: {
        text: $i18n.get({
          id: 'holoinsight.admin.ops.OpsHistory.Delete',
          dm: '删除',
        }),
        status: 'Error',
      },
      UPDATE: {
        text: $i18n.get({
          id: 'holoinsight.admin.ops.OpsHistory.Modify',
          dm: '修改',
        }),
        status: 'Success',
      },
    },
  },
  {
    title: '操作对象',
    dataIndex: 'tableName',
    key: 'tableName',
    render: (text) => {
      switch (text) {
        case 'user_favorite':
          return '用户收藏';
        case 'custom_plugin':
          return '日志监控';
        case 'alarm_rule':
          return '告警规则';
        case 'dashboard':
          return '仪表盘';
        case 'folder':
          return '文件夹';
        case 'integration_plugin':
          return '集成组件';
        case 'marketplace_plugin':
          return '市场组件';
        default:
          return text;
      }
    },
  },
  {
    title: $i18n.get({
      id: 'holoinsight.admin.ops.OpsHistory.Comparison',
      dm: '对比',
    }),
    search: false,
    dataIndex: 'opBeforeContext',
    key: 'opBeforeContext',
    render: (text, record: any) => (
      <>
        <ContentCompare record={record}>
          <a>
            {$i18n.get({
              id: 'holoinsight.admin.ops.OpsHistory.View',
              dm: '查看',
            })}
          </a>
        </ContentCompare>
        <Divider type="vertical" />
        {record?.['tableName'] === 'custom_plugin' && (
          <a
            target="_blank"
            href={polishingUrl(`/log/metric/${record.tableEntityId}`)}
            rel="noreferrer"
          >
            {$i18n.get({
              id: 'holoinsight.admin.ops.OpsHistory.Jump',
              dm: '跳转',
            })}
          </a>
        )}
      </>
    ),
  },
];
