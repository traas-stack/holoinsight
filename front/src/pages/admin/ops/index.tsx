import OpsHistory from '@/pages/admin/ops/OpsHistory';
import { useModel } from 'umi';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <OpsHistory initialState={initialState} {...props} />;
};
