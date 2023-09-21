
import React,{useState, useEffect} from 'react';
import MagiChart from '@/pages/dashboardMagi/commonDashboard';
import { getQueryString } from '@/utils/help';
import { useParams } from 'umi';
import {
    getAPMTemplate
} from '@/services/infra/api'


const DemoDashboard:React.FC<any> = (props) => {
    const id  = getQueryString('id')
    const app = getQueryString('app') || '';
    const envWorkSapce = getQueryString('workspace') || ''

    const {template} = useParams();
    const [ dashboardConfig, setDashboardConfig] = useState<any>(null)
    const [workspace, setWorkSpace] = useState(envWorkSapce)

     
    const getDashBoardConfig = async (id: string) => {
        const res: any = await getAPMTemplate(id);
        if (res && res?.config) {
            let config = JSON.stringify(res.config).replace(/\$\{app\}/g, app);
            return JSON.parse(config);
        }
    };
    function getNewWorkSpace(value:string){
        setWorkSpace(value);
    }
    useEffect(()=>{
        initDashboardConfig();
    },[template])
    async function initDashboardConfig(){
       const dashboardConfig =  await getDashBoardConfig(id!);
       setDashboardConfig(dashboardConfig)
    }

    const getChangeDashboardConfig = (newDashboardConfig:any)=>{
         // 做更新操作
         setDashboardConfig(newDashboardConfig);
    }
    
    return (
        <div>
            {
                dashboardConfig &&  <MagiChart 
                hasTimePicker = {true}  //是否有时间选择器
                mode = {"preview"}   //大盘模式 previw / edit
                allowEdit = {false}  //是否允许编辑
                dashboardConfig = {dashboardConfig}  //大盘配置
                getTitle = {()=>null} //大盘标题
                id = {id}
                getChangeDashboardConfig = {getChangeDashboardConfig} //拿到大盘的数据
                updateFilter={true}
                hasWorkSpace = {true}
                workspace = {workspace}
                getNewWorkSpace = {getNewWorkSpace}
                />
            }
        </div>
    )
}

export default DemoDashboard;