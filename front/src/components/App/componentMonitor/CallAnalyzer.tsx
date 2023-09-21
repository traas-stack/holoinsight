import {useEffect,useState} from 'react';
import {
  Tooltip,
  message,
} from 'antd';
import MagiChart from '@/pages/dashboardMagi/commonDashboard';

import type { TableListItem as TableListSqlItem } from '@/components/App/componentMonitor/index';
import { ArrowLeftOutlined, CopyOutlined } from '@ant-design/icons';
import {
  getAPMTemplate
} from '@/services/infra/api'
import { getQueryString } from '../../../utils/help';
import copy from 'copy-to-clipboard';
import $i18n from '../../../i18n';
import styles from './index.less';

interface IProps {
  sqlRecord?: TableListSqlItem | null;
  setIsCallAnalyzer?: any;
}


function CallAnalyzer(props: IProps) {
  const { sqlRecord, setIsCallAnalyzer } = props;
  const [ dashboardConfig, setDashboardConfig] = useState<any>(null)
  const id = getQueryString('id');
  const getDashBoardConfig = async (id: string) => {
    const res: any = await getAPMTemplate(id!)
    if (res && res?.config) {
      let config = JSON.stringify(res.config).replace(/\$\{app\}/g, sqlRecord?.sourceName || '');
      return JSON.parse(config);
    }
};
async function initDashboardConfig(){
  const dashboardConfig =  await getDashBoardConfig(id!);
  setDashboardConfig(dashboardConfig)
}
useEffect(()=>{
    initDashboardConfig();
},[])

  return (
    <div>
    <div className={styles.callAnalyzer}>
      <div className={styles.callLinkTitle}>
        <ArrowLeftOutlined
          onClick={() => setIsCallAnalyzer && setIsCallAnalyzer(false)}
          style={{ marginRight: 10 }}
        />
        <Tooltip title={sqlRecord?.sourceName}>
          <span className={styles.callLinkText}>{sqlRecord?.sourceName}</span>
        </Tooltip>
        <CopyOutlined
          style={{ fontSize: 16, cursor: 'pointer', color: '#1890ff' }}
          onClick={() => {
            const copyText = sqlRecord?.sourceName || '';
            copy(copyText.toString());
            message.success(
              $i18n.get({
                id: 'holoinsight.admin.apiKey.CopySuccessfully',
                dm: '复制成功！',
              }),
            );
          }}
        />
      </div>
      <div>
        {
          dashboardConfig ?  <MagiChart
          hasTimePicker = {false} 
          mode = "preview" 
          allowEdit = {false}  
          dashboardConfig = {dashboardConfig} 
          getTitle = {()=>null} 
          hasFavirate = {false}
          updateFilter={true}
            /> : null
        }
       </div>
    </div>
    </div>
  );
}

export default CallAnalyzer;
