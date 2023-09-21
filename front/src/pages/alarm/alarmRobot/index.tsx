import { useModel } from 'umi';
import AlarmRobot from './AlarmRobot';
export default (props:any) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmRobot initialState={initialState} {...props} />;
};
