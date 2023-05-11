import { queryMetricLike } from '@/services/tenant/api';
import { Form, Select, SelectProps, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import { debounce } from 'lodash';
import { SubTypeSelectorProps } from '.';
import $i18n from '../../../i18n';

const CloudSelector: React.FC<SubTypeSelectorProps> = () => {
  const [options, setOptions] = useState<SelectProps['options']>([]);
  const [loading, setLoading] = useState<boolean>();
  const onSearch = debounce((value: string) => {
    if (value.length > 0) {
      setLoading(true);
      queryMetricLike(value).then((res = []) => {
        setLoading(false);
        setOptions(
          (res as string[]).map((m: string) => ({
            value: m,
            label: m,
          })),
        );
      });
    } else {
      setOptions([]);
    }
  }, 1000);
  return (
    <div style={{ marginTop: '20px' }}>
      <Space />
      <Form.Item
        name="metrics"
        label={$i18n.get({
          id: 'holoinsight.DataSource.selector.cloud.Indicators',
          dm: '指标',
        })}
        rules={[{ required: true }]}
      >
        <Select
          allowClear
          showSearch
          placeholder={$i18n.get({
            id: 'holoinsight.DataSource.selector.cloud.EnterAMetric',
            dm: '请输入指标',
          })}
          onSearch={onSearch}
          loading={loading}
          options={options}
        />
      </Form.Item>
    </div>
  );
};
export default CloudSelector;
