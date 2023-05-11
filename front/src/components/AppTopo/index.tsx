import LineTpChart from '@/components/Chart/linkTpChart';
import { getQueryString, getTreeData } from '@/utils/help';
import { useEffect, useState } from 'react';
import CardInfo from '../CardInfo';
import styles from './index.less';

export default function AppTopo(props: any) {
  const { deep, rangeTime: time, topoData } = props;
  let treeNode: any = {};
  const [chatData, setChartData] = useState(null);
  const [checkName, setCheckName] = useState('');
  const app = getQueryString('app') || '';
  useEffect(() => {
    if (topoData) {
      const name = getTreeData(topoData, treeNode, app);
      setCheckName(name);
      setChartData(treeNode);
    }
  }, [deep, JSON.stringify(time), topoData]);

  function handleChangeCheck(value: any) {
    setCheckName(value);
  }

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        {chatData ? (
          <LineTpChart
            handleChangeCheck={handleChangeCheck}
            fromType="app"
            treeNode={chatData}
          />
        ) : null}
      </div>
      <div className={styles.right}>
        {chatData ? (
          <CardInfo treeNode={chatData} checkName={checkName} />
        ) : null}
      </div>
    </div>
  );
}
