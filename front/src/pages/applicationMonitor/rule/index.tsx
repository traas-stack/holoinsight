import React from 'react';
import AlarmMonagement from '@/pages/alarm/alarmSubscription';
import { useModel } from 'umi';

const Rule: React.FC<any> = (props) => {
  const { initialState } = useModel('@@initialState');
  return <AlarmMonagement initialState={initialState} {...props} />;
};
export default Rule;
