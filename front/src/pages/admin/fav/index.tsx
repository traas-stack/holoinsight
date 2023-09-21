import UserFavorite from '@/pages/admin/fav/UserFavorite';
import { useModel } from 'umi';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <UserFavorite initialState={initialState} {...props} />;
};
