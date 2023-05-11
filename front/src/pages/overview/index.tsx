import { Button, Card } from 'antd';
import React from 'react';
import { history } from 'umi';
import $i18n from '../../i18n';
import Favorite from './Favorite';

const Overview: React.FC = () => {
  return (
    <>
      <Card>
        <div>
          <div style={{ fontSize: 40, fontWeight: 600 }}>HoloInsight 监控</div>

          <div style={{ marginTop: 16 }}>
            {$i18n.get({
              id: 'holoinsight.pages.overview.MakeDataTransparentAndWorry',
              dm: '让数据透明，使运维无忧；包含日志监控、应用监控、基础设施监控、云原生可观测等功能，提供丰富的预警规则计算和报警订阅能力，是业务研发、SRE等同学进行故障应急处理和日常问题分析的重要入口。',
            })}
          </div>

          <div style={{ fontWeight: 500, fontSize: 16, marginTop: 28 }}>
            <Button
              style={{ border: '1px solid #037AFF', color: '#037AFF' }}
              onClick={() => history.push('/integration/agent')}
            >
              {$i18n.get({
                id: 'holoinsight.pages.overview.AgentInstallation',
                dm: 'Agent 安装',
              })}
            </Button>
          </div>
        </div>
      </Card>

      <Favorite />
    </>
  );
};

export default Overview;
