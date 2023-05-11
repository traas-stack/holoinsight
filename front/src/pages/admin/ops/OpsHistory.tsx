import { pageQuery } from '@/services/tenant/opsApi';
import ProTable from '@ant-design/pro-table';
import $i18n from '@/i18n';
import { TableListItem, columns } from './const';
export default function OpsHistory(props: any) {
  const { tableEntityId } = props?.match?.params || props;
  const initialState = props.initialState;
  return (
    <ProTable<TableListItem>
      columns={columns}
      request={async (params, sorter) => {
        let gmtObj: any = {};
        gmtObj.sortBy = Object.keys(sorter)[0];
        gmtObj.sortRule =
          sorter[Object.keys(sorter)[0]] === 'ascend' ? 'asc' : 'desc';
        const pageRequest = {
          ...gmtObj,
          pageNum: params.current,
          pageSize: params.pageSize || 10,
          target: {
            ...params,
            tenant: initialState?.currentTenant,
            tableEntityId,
            creater:
              window.location?.pathname === '/user/opsLog'
                ? initialState?.user?.loginName
                : null,
          },
        };
        const data: any = await pageQuery(pageRequest);
        data.items.forEach((item: any) => {
          item['key'] = item.id;
        });
        return {
          data: data.items,
          success: true,
          total: data.totalCount,
        };
      }}
      rowKey="key"
      pagination={{
        pageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
      }}
      search={{
        labelWidth: 'auto',
      }}
      dateFormatter="string"
      headerTitle={$i18n.get({
        id: 'holoinsight.admin.ops.OpsHistory.OperationHistory',
        dm: '操作历史',
      })}
    />
  );
}
