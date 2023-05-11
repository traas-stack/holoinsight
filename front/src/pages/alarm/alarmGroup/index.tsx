import { useModel } from 'umi';
import AlarmGroup from './AlarmGroup';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmGroup initialState={initialState} {...props} />;
};
