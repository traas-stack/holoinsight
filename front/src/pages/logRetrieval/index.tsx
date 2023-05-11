import  { useEffect, useState } from 'react';
import Logger from './Detail/components/Logger/index';
import styles from './index.less';
import ServiceSelectSearch from './ServiceSelectSearch';
interface IProps {
  serverName?: string;
  inServer?: boolean;
}
export default ({ serverName, inServer }: IProps) => {
  const [serviceInfos, setServiceInfos] = useState<
    { id: string; name: string }[]
  >([]);
  useEffect(() => {
    if (serverName && inServer) {
      setServiceInfos([
        {
          id: serverName,
          name: serverName,
        },
      ]);
    }
  }, [serverName, inServer]);
  return (
    <>
      {!inServer && (
        <div
          style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '10px' }}
        >
          日志检索
        </div>
      )}
      <div className={styles.loggerContainer}>
        <Logger
          servicSearchExtra={
            // TODO 多服务组件未来可考虑重构
            <ServiceSelectSearch
              serviceInfos={serviceInfos}
              onSetServiceInfos={setServiceInfos}
              style={{ flex: 1 }}
              useMultiService={false}
              showArrow
              maxTagCount="responsive"
              showSearch={true}
            />
          }
          inServer={inServer}
          serviceInfos={serviceInfos}
          useMultiService={false}
        />
      </div>
    </>
  );
};
