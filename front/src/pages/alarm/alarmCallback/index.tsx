import { useModel } from 'umi';
import AlarmCallback from './AlarmCallback';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmCallback initialState={initialState} {...props} />;
};
