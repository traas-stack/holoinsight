import React, { useState, useEffect } from 'react';
import { Tabs, Card } from 'antd';
import { ClusterOutlined, ApartmentOutlined } from '@ant-design/icons';
import './agent.less';
import $i18n from '../../i18n';
import { getVMagent } from '@/services/agent/api';
import _ from 'lodash';

const { TabPane } = Tabs;

const Agent: React.FC = () => {
  const [vmInstallDocument, setVmInstallDocument] = useState('');
  const [k8sInstallDocument, setK8sInstallDocument] = useState('');
  useEffect(() => {
    getVMagent().then((res:any) => {
      const vmInstallDocument = _.replace(res.vmInstallDocument, /\$\{registryHost\}/g, res.registryHost).replace(/\$\{gatewayHost\}/g, res.gatewayHost).replace(/\$\{apikey\}/g, res.apiKey);
      const k8sInstallDocument = _.replace(res.k8sInstallDocument, /\$\{registryHost\}/g, res.registryHost).replace(/\$\{gatewayHost\}/g, res.gatewayHost).replace(/\$\{apikey\}/g, res.apiKey);
      setVmInstallDocument(vmInstallDocument);
      setK8sInstallDocument(k8sInstallDocument);
    });
  }, []);
  return (
    <Card
      title={$i18n.get({
        id: 'holoinsight.pages.integration.agent.AgentInstallation',
        dm: 'Agent 安装',
      })}
    >
      <h2>
        {$i18n.get({
          id: 'holoinsight.pages.integration.agent.SelectTheCorrespondingPlatformTo',
          dm: '选择对应的平台完成安装',
        })}
      </h2>

      <Tabs defaultActiveKey="1">
        <TabPane
          tab={
            <span>
              <ClusterOutlined />
              Centos/Ubuntu
            </span>
          }
          key="1"
        >
           <div dangerouslySetInnerHTML={{ __html: vmInstallDocument }}></div>
        </TabPane>

        <TabPane
          tab={
            <span>
              <ApartmentOutlined />
              Kubernates
            </span>
          }
          key="2"
        >
          <div dangerouslySetInnerHTML={{ __html: k8sInstallDocument }}></div>
        </TabPane>
      </Tabs>
    </Card>
  );
};

export default Agent;
