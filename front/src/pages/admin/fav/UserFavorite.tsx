import ProTable from '@ant-design/pro-table';
import { pageQuery } from '@/services/tenant/favoriteApi';
import $i18n from '@/i18n';
import { type TableListItem, columns } from './const';

export default (props: any) => {
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
          pageSize: params.pageSize,
          target: {
            ...params,
            tenant: initialState?.currentTenant,
            userLoginName:
              window.location?.pathname === '/user/favorite'
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
        };
      }}
      rowKey="key"
      pagination={{
        showQuickJumper: true,
        showSizeChanger: true,
      }}
      search={{
        labelWidth: 'auto',
      }}
      dateFormatter="string"
      headerTitle={$i18n.get({
        id: 'holoinsight.admin.fav.UserFavorite.FavoritesList',
        dm: '收藏列表',
      })}
    />
  );
};
