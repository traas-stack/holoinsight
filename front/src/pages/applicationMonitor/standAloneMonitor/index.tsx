import MagiRefeshContainer from '@/MagiContainer/MagiRefeshContainer';
import APIList from '@/pages/applicationMonitor/APIMonitor/APIList';
import { getQueryString } from '@/utils/help';
import React, { useState } from 'react';

const StandAloneMonitor: React.FC<any> = (props) => {
  const app = getQueryString('app');
  const envWorkSapce = getQueryString('workspace')
  const range = decodeURIComponent(decodeURI(getQueryString('range') || ''));
  const [time, setTime] = useState<any>({});
  const [workspace, setWorkSpace] = useState(envWorkSapce)

  function onContextChange(value: any) {
    setTime(value);
  }
  function getNewWorkSpace(value:string){
    setWorkSpace(value);
  }

  return (
    <div>
      <MagiRefeshContainer
        hasWorkSpace = {true}
        context={{ range, env: 'prod' }}
        getNewWorkSpace = {getNewWorkSpace}
        workspace = {workspace}
        // delay={90}
        onChange={onContextChange}
      />
      <APIList
        time={time}
        title={app || ''}
        type={app || ''}
        compType="standAlone"
      />
    </div>
  );
};

export default StandAloneMonitor;
