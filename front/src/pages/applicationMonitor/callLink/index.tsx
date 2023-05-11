
import { getQueryString } from '../../../utils/help';
import { Card } from 'antd';
import $i18n from '../../../i18n';
import CallLinkTable from './CallLinkTable';
import styles from './index.less';

interface Iprops {
  type?: string;
  app?: string;
}

export default (props: Iprops) => {
  const app = props?.app || getQueryString('app');
  const type = props?.type || 'componentMonitor';
  return (
    <>
      <Card>
        <div className={styles.appContainer}>
          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.applicationMonitor.callLink.CallDetails',
              dm: '调用明细',
            })}
          </div>
        </div>
        <CallLinkTable app={app} type={type} />
      </Card>
    </>
  );
};
