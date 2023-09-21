import { useModel } from 'umi';
import AlarmMonagement from './AlarmMonagement';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmMonagement initialState={initialState} {...props} />;
};
