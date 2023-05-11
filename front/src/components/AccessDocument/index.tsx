import { queryTraceAgent } from '@/services/application/api';
import React, { useEffect, useState } from 'react';

import SkyWalkingContent from '@/pages/applicationMonitor/SkywalkingContent';
import { Tabs } from 'antd';

const AccessDocument: React.FC<any> = (props) => {
  const [apiKey, setApiKey] = useState('');
  const [host, setHost] = useState('');
  const [traceInstallDocument, setTraceInstallDocument] = useState('');
  useEffect(() => {
    queryTraceAgent({}).then((data: any) => {
      setHost(data.collectorHost || '');
      setApiKey(data.apiKey || '');
      setTraceInstallDocument(data.traceInstallDocument || '');
    });
  }, []);
  return (
    <div>
        <div dangerouslySetInnerHTML={{ __html: traceInstallDocument }}></div>
        <Tabs
          items={[
            {
              label: 'Java',
              key: 'java',
              children: (
                <Tabs
                  type="card"
                  items={[
                    {
                      label: 'Skywalking',
                      key: 'skywalking',
                      children: (
                        <SkyWalkingContent apiKey={apiKey} host={host} />
                      ),
                    },
                  ]}
                />
              ),
            },
          ]}
        />
    </div>
  );
};
export default AccessDocument;
