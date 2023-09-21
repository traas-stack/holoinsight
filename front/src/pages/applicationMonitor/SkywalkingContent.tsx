import { Alert, Card, Collapse, Steps } from 'antd';
import React, { useState } from 'react';
import $i18n from '../../i18n';
import styles from './index.less';

const { Panel } = Collapse;

const SkyWalkingContent: React.FC<any> = (props) => {
  const [type, setType] = useState('');
  const { apiKey, host } = props;

  const items = [
    {
      status: 'process',
      title:
        type === 'cantainer'
          ? $i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ApplyDockerfileModification',
              dm: '应用Dockerfile修改',
            })
          : $i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.DownloadAgent',
              dm: '下载Agent',
            }),
      description: (
        <Collapse defaultActiveKey="1">
          <Panel
            key="1"
            header={$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.AddAgentDownloadCommand',
              dm: '添加Agent下载命令',
            })}
          >
            {type === 'cantainer' ? (
              <>
                <section>
                  RUN wget
                  https://archive.apache.org/dist/skywalking/java-agent/8.11.0/apache-skywalking-java-agent-8.11.0.tgz
                </section>
                <section>
                  RUN tar zxvf apache-skywalking-java-agent-8.11.0.tgz
                </section>
              </>
            ) : (
              <>
                <section>
                  {$i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.GoToAnyWorkingDirectory',
                    dm: '进入任意工作目录下，下载解压Agent',
                  })}
                </section>
                <section>
                  wget
                  https://archive.apache.org/dist/skywalking/java-agent/8.11.0/apache-skywalking-java-agent-8.11.0.tgz
                </section>
                <section>
                  tar zxvf apache-skywalking-java-agent-8.11.0.tgz
                </section>
              </>
            )}
          </Panel>
        </Collapse>
      ),
    },
    {
      status: 'process',
      title: $i18n.get({
        id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ModifyConfigurationByEnvironmentVariable',
        dm: '环境变量方式修改配置',
      }),
      description: (
        <Collapse defaultActiveKey="2">
          <Panel
            key="2"
            header={
              type === 'cantainer'
                ? $i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.DockerfileAddEnvironmentVariables',
                    dm: 'dockerfile添加环境变量',
                  })
                : $i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ModifyTheFollowingContentIn',
                    dm: '修改 skywalking-agent/config 目录中 agent.config 文件中的以下内容',
                  })
            }
          >
            <section>
              {$i18n.get({
                id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ServiceNameYouMustSpecify',
                dm: '# 服务名称（需要自行指定）',
              })}
            </section>
            {type === 'cantainer' ? (
              <>
                <section>
                  {$i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.MakeSureThatTheService.1',
                    dm: '# 请确保服务名称和holoinsight daemonset采集的应用名称保持一致',
                  })}
                </section>
                {/* <section># apikey从holoinsight页面获取</section> */}
                <section>ENV SW_AGENT_NAME xxxx</section>
                <section>{`ENV SW_AGENT_AUTHENTICATION ${apiKey}`}</section>
                <section>
                  {$i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.RegisterServiceAndDataUpload',
                    dm: '# 注册服务和数据上传地址',
                  })}
                </section>
                <section>{`ENV SW_AGENT_COLLECTOR_BACKEND_SERVICES ${host}`}</section>
                <section>
                  {$i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.TlsIsRequiredForPublic',
                    dm: '# 公网域名需tls',
                  })}
                </section>
                <section>ENV SW_AGENT_FORCE_TLS true</section>
              </>
            ) : (
              <>
                <section>{'agent.service_name=${SW_AGENT_NAME:xxxx}'}</section>
                {/* <section># apikey从holoinsight页面获取</section> */}
                <section>
                  {'agent.authentication=${SW_AGENT_AUTHENTICATION:' +
                    apiKey +
                    '}'}
                </section>
                <section>
                  {$i18n.get({
                    id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.RegisterServiceAndDataUpload',
                    dm: '# 注册服务和数据上传地址',
                  })}
                </section>
                <section>
                  {'collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:' +
                    host +
                    '}'}
                </section>
                <section>
                  {'agent.force_tls=${SW_AGENT_FORCE_TLS:true}'}
                </section>
              </>
            )}
          </Panel>
        </Collapse>
      ),
    },
    {
      status: 'process',
      title: $i18n.get({
        id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.AddJavaStartupParameters',
        dm: '增加Java启动参数',
      }),
      description: (
        <Collapse defaultActiveKey="3">
          <Panel
            key="3"
            header={$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.AddStartupParametersToThe',
              dm: '应用启动脚本中添加启动参数(注：javaagent参数放在第一个参数位置)',
            })}
          >
            java -javaagent:xxxxx/skywalking-agent/agent/skywalking-agent.jar
          </Panel>
        </Collapse>
      ),
    },
  ];

  return (
    <div className={styles.skyContent}>
      <Alert
        message={$i18n.get({
          id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.MakeSureThatTheService',
          dm: '请确保服务名称和holoinsight daemonset采集的应用名称保持一致',
        })}
        type="info"
        showIcon
      />

      <div className={styles.skyContentCard}>
        <Card
          className={`${styles.skyContentCardItem} ${
            type === 'host' ? '' : styles.active
          } ${styles.mgr20}`}
          bodyStyle={{ padding: 12 }}
          onClick={() => {
            setType('cantainer');
          }}
        >
          <div className={styles.contentCardTitle}>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ContainerDeployment',
              dm: '容器部署',
            })}
          </div>
          <div className={styles.contentCardText}>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ApplicableToUsersWhoDeploy',
              dm: '适用于基于Kubernetes集群或者容器服务VKE部署服务的用户',
            })}
          </div>
        </Card>
        <Card
          className={`${styles.skyContentCardItem} ${
            type === 'host' ? styles.active : ''
          }`}
          bodyStyle={{ padding: 12 }}
          onClick={() => {
            setType('host');
          }}
        >
          <div className={styles.contentCardTitle}>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.HostDeployment',
              dm: '主机部署',
            })}
          </div>
          <div className={styles.contentCardText}>
            {$i18n.get({
              id: 'holoinsight.pages.applicationMonitor.SkywalkingContent.ApplicableToUsersWhoDeploy.1',
              dm: '适用于基于虚拟机或者物理机部署服务的用户',
            })}
          </div>
        </Card>
      </div>
      <Steps direction="vertical" items={items} />
    </div>
  );
};

export default SkyWalkingContent;
