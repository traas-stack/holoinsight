import { inspect } from '@/services/agent/api';
import { tableDataUpdate } from '@/services/infra/api';
import { Button, Drawer, message, Tabs } from 'antd';
import { useState } from 'react';
import ReactJson from 'react-json-view';
import $i18n from '../../i18n';
const { TabPane } = Tabs;

interface Props {
  data: any;
  onShowDrawer: any;
  onCloseDrawer: any;
  type: string;
  queryData?: any;
  tableInfo?: any;
}

export default function MetaView(props: Props) {
  const { data, onShowDrawer, onCloseDrawer, type, tableInfo, queryData } =
    props;
  const [listvisible, setListVisible] = useState(false);
  const [hostInfo, setHostInfo] = useState(false);
  const [editData, setEidtData] = useState(data);

  const inspectMeta = async (ip: any, hostname: any) => {
    if (ip) {
      const res = await inspect({
        ip: ip,
        hostname: hostname,
      });

      setHostInfo(res);
    }
  };
  const showDrawer = () => {
    if (type === 'view') {
      inspectMeta(data?.ip, data?.hostname);
    }
    setListVisible(true);
    onShowDrawer();
  };
  const onClose = () => {
    setListVisible(false);
    onCloseDrawer();
  };

  const ordered = {};
  if (type === 'view') {
    if (data && Object.keys(data).length > 0) {
      Object.keys(data)
        .sort((a, b) => b.localeCompare(a))
        .forEach(function (key) {
          if (key === 'cpu_util' || key === 'mem_util') return;
          ordered[key] = data[key];
        });
    }
  }

  const handleClickSave = () => {
    let arr = [];
    arr.push(editData);
    tableDataUpdate(tableInfo.name, arr).then((res) => {
      if (res) {
        message.success(
          $i18n.get({
            id: 'holoinsight.pages.infra.MetaView.UpdatedSuccessfully',
            dm: '更新成功',
          }),
        );
        setListVisible(false);
        queryData(tableInfo.name);
      }
    });
  };

  return (
    <>
      <a type="primary" onClick={showDrawer}>
        {type === 'view'
          ? $i18n.get({
              id: 'holoinsight.pages.infra.MetaView.View',
              dm: '查看',
            })
          : $i18n.get({
              id: 'holoinsight.pages.infra.MetaView.Edit',
              dm: '编辑',
            })}
      </a>

      <Drawer
        title={`${type === 'view' ? '机器信息' : '编辑'}`}
        width={1000}
        placement="right"
        onClose={onClose}
        open={listvisible}
        closable={false}
        footer={
          type === 'view' ? null : (
            <div>
              <Button
                type="primary"
                style={{ marginRight: 20 }}
                onClick={() => {
                  handleClickSave();
                }}
              >
                {$i18n.get({
                  id: 'holoinsight.pages.infra.MetaView.Save',
                  dm: '保存',
                })}
              </Button>

              <Button onClick={() => setListVisible(false)}>
                {$i18n.get({
                  id: 'holoinsight.pages.infra.MetaView.Cancel',
                  dm: '取消',
                })}
              </Button>
            </div>
          )
        }
      >
        {type === 'view' ? (
          <Tabs defaultActiveKey="2">
            <TabPane
              tab={$i18n.get({
                id: 'holoinsight.pages.infra.MetaView.BasicInformation',
                dm: '基本信息',
              })}
              key="2"
            >
              <ReactJson
                src={hostInfo?.['variable']?.['host']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane tab="CPU" key="3">
              <ReactJson
                src={hostInfo?.['variable']?.['cpu']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane tab="MEM" key="4">
              <ReactJson
                src={hostInfo?.['variable']?.['mem']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane tab="DISK" key="5">
              <ReactJson
                src={hostInfo?.['variable']?.['disk']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane tab="NET" key="6">
              <ReactJson
                src={hostInfo?.['variable']?.['net']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane
              tab={$i18n.get({
                id: 'holoinsight.pages.infra.MetaView.Metadata',
                dm: '元数据',
              })}
              key="7"
            >
              <ReactJson
                src={ordered}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>

            <TabPane
              tab={$i18n.get({
                id: 'holoinsight.pages.infra.MetaView.AgentInformation',
                dm: 'AGENT信息',
              })}
              key="1"
            >
              <ReactJson
                src={hostInfo?.['variable']?.['agent']}
                displayDataTypes={false}
                theme="monokai"
              />
            </TabPane>
          </Tabs>
        ) : (
          <ReactJson
            src={data}
            displayDataTypes={false}
            onDelete={(del) => {
              setEidtData(del.updated_src);
            }}
            onAdd={(add) => {
              setEidtData(add.updated_src);
            }}
            onEdit={(edit) => {
              setEidtData(edit.updated_src);
            }}
            theme="monokai"
          />
        )}
      </Drawer>
    </>
  );
}
