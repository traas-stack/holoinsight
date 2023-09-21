import { useModel } from 'umi';
import AlarmHistory from './AlarmHistory';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmHistory initialState={initialState} {...props} />;
};
