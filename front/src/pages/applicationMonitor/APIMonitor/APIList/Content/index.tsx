import React, { useState, memo, useEffect } from 'react';
import MagiChart from '@/pages/dashboardMagi/commonDashboard';
import {
  getAPMTemplate
} from '@/services/infra/api'
import { getQueryString } from '@/utils/help';

export interface Props {
  title: string;
  type: string;
  endPoint: string;
  compType:string;
  time: any;
}
// @ts-ignore
const Content: React.FC<Props> = ({ title, endPoint,compType,time }) => {
    const id = getQueryString('id')
    const app = getQueryString('app') || '';
    const [ dashboardConfig, setDashboardConfig] = useState<any>(null)
    const [ compareDashBoardConfig, setCompareDashBoardConfig] = useState<any>(null)
    // const [mode, setMode] = useState('preview');

    const getDashBoardConfig = async (id: string) => {
        const res: any = await getAPMTemplate(id!)
        if (res && res?.config) {
          let config = JSON.stringify(res.config).replace(/\$\{app\}/g, app);
          setCompareDashBoardConfig(JSON.parse(config));
          if(compType === "api"){
            config = config.replace(/\$\{endpointName\}/g, endPoint)
          }
          if(compType === 'standAlone'){
            config = config.replace(/\$\{serviceInstanceName\}/g, endPoint)
          }
          return JSON.parse(config);
        }
    };
    useEffect(()=>{
        initDashboardConfig();
    },[])
    useEffect(()=>{
      if(dashboardConfig){
        let config = JSON.stringify(compareDashBoardConfig);
        if(compType === "api"){
          config = config.replace(/\$\{endpointName\}/g, endPoint)
        }
        if(compType === 'standAlone'){
          config = config.replace(/\$\{serviceInstanceName\}/g, endPoint)
        }
        setDashboardConfig(JSON.parse(config));
      }
    },[endPoint])
    async function initDashboardConfig(){
       const dashboardConfig =  await getDashBoardConfig(id!);
       setDashboardConfig(dashboardConfig)
    }
  return (
    <div>
     {
       dashboardConfig &&  <MagiChart
       start = {time?.starttime}
       end= {time?.endtime}
       hasTimePicker = {false}  //是否有时间选择器
       mode = "preview"   //大盘模式 previw / edit
       allowEdit = {false}  //是否允许编辑
       // onChangeMode = {onChangeMode}  //切换模式
       dashboardConfig = {dashboardConfig}  //大盘配置
       getTitle = {()=>null} //大盘标题
       // getChangeDashboardConfig = {getChangeDashboardConfig} //拿到大盘的数据
       hasFavirate = {false}
       updateFilter = {true}
         />
     }
    </div>
  );
};
export default memo(Content);
