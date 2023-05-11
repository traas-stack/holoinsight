import React, { useState, memo } from 'react';
import ExpandCard from '@/components/ExpandCard';
import Sider from './Sider';
import Content from './Content';
export interface Props {
  title: string;
  type: string;
  compType:string;
  changePointerName?: (value: string) => void;
  time: any;
}
const APIList: React.FC<Props> = ({ title, type,changePointerName,compType,time }) => {
  const [endPoint, setEndPoint] = useState('');
  return (
    <ExpandCard
      sider={
        <Sider
          title={title}
          type={type}
          time = {time}
          compType = {compType}
          endPoint={endPoint}
          setEndPoint={(v: string) => {
            setEndPoint(v);
            changePointerName && changePointerName(v);
          }}
        />
      }
      content={ endPoint ? <Content time = {time} compType = {compType} endPoint={endPoint} title={title} type={type} /> : null}
    />
  );
};
export default memo(APIList);
