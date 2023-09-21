import { Card, Form, Space } from 'antd';
import React, { useState } from 'react';
import $i18n from '../../../i18n';

const LogSelector: React.FC<any> = (props: any) => {
  const [selectedTarget] = useState('');


  return (
    <div>
      <Space> </Space>
      <Card style={{ marginRight: '10px' }}>
        {$i18n.get({
          id: 'holoinsight.DataSource.selector.log.CurrentlySelectedIndicator',
          dm: '当前选中指标：',
        })}
        {selectedTarget}
        <div style={{ height: '550px', overflow: 'auto', marginTop: '10px' }}>
          <Form.Item name="treeNode">
          </Form.Item>
        </div>
      </Card>
    </div>
  );
};
export default LogSelector;
