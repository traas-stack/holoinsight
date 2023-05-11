import { Form, Space } from 'antd';
import React from 'react';
import { SubTypeSelectorProps } from '.';
import $i18n from '../../../i18n';
const PrometheusSelector: React.FC<SubTypeSelectorProps> = ({ form }) => {
  return (
    <div>
      <Space> </Space>

      <Form form={form}>
        <Form.Item
          name="metrics"
          label={$i18n.get({
            id: 'holoinsight.DataSource.selector.prometheus.IndicatorExpression',
            dm: '指标表达式',
          })}
          rules={[{ required: true }]}
        >
        </Form.Item>
      </Form>
    </div>
  );
};
export default PrometheusSelector;
