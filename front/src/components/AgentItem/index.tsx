import React from 'react';
import { Alert } from 'antd';
import styles from './index.less';
import $i18n from '../../i18n';
import AlibabaCloud from '../../assets/AlibabaCloud.svg'
import JVM from '../../assets/JVM.svg'
import MySQL from '../../assets/MySQL.svg'
import SpringBoot from '../../assets/SpringBoot.png'
const AgentItem: React.FC<any> = (props) => {
  const { data } = props;
  const type = {
    JVM,
    MySQL,
    SpringBoot,
    AlibabaCloud
  }
  return (
    <div>
      <div className={styles.agentTop}>
        {!data.hasData ? (
          <Alert
            className={styles.error}
            message={$i18n.get({ id: 'holoinsight.components.AgentItem.NoData', dm: '无数据' })}
            type="warning"
            showIcon
          />
        ) : null}

        <img
          style={{ width: 200, height: 128 }}
          className={styles.img}
          src={type[data.productName]}
        />
      </div>

      <div>
        <h3 className={styles.title}>{data.productName}</h3>
      </div>
    </div>
  );
};
export default AgentItem;
