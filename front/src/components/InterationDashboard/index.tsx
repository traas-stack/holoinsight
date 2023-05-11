import React, { useState } from 'react';
import { Drawer, Button, Table } from 'antd';
import { history } from '@umijs/max';
import ReactJson from 'react-json-view';

import _ from 'lodash';
import $i18n from '../../i18n';

export default (props: any) => {
  const { dataItem } = props;
  const dataSource = [
    {
      productName: dataItem.productName,
    },
  ];

  const [visible, setVisible] = useState(false);
  const tableHeader = [
    {
      title: $i18n.get({
        id: 'holoinsight.components.InterationDashboard.Name',
        dm: '名称',
      }),
      dataIndex: 'productName',
      key: 'productName'
    },
    {
      title: $i18n.get({
        id: 'holoinsight.components.InterationDashboard.Operation',
        dm: '操作',
      }),
      dataIndex: 'operate',
      key: 'operate',
      width: 200,
      render: () => {
        return (
          <>
            <Button
              type="link"
              onClick={() =>
                history.push(
                  `/integration/dashboard/${dataItem.installed? dataItem.installedId: dataItem.id}?product=${dataItem.productName}&installed=${dataItem.installed ? 'true': 'false'}`,
                )
              }
            >
              {$i18n.get({
                id: 'holoinsight.components.InterationDashboard.Preview',
                dm: '预览',
              })}
            </Button>

            <Button
              type="link"
              onClick={() => {
                setVisible(true);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.components.InterationDashboard.JsonConfiguration',
                dm: 'JSON配置',
              })}
            </Button>
          </>
        );
      },
    },
  ];

  return (
    <>
      <Table columns={tableHeader} dataSource={dataSource} pagination={false} />

      <Drawer
        title={$i18n.get({
          id: 'holoinsight.components.InterationDashboard.Indicators',
          dm: '指标',
        })}
        width={800}
        placement="right"
        onClose={() => setVisible(false)}
        open={visible}
      >
        <ReactJson
          src={dataItem.template}
          displayDataTypes={false}
          theme="monokai"
        />
      </Drawer>
    </>
  );
};
