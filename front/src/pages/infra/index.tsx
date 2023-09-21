import Infra from './infra';
import { useModel } from 'umi';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <Infra initialState={initialState} {...props} />;
};
