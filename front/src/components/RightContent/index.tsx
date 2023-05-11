import React from 'react';
import { Space } from 'antd';
import Tenant from '@/components/RightContent/Tenant';
import Avatar from '@/components/RightContent/Avator';

const GlobalHeaderRight: React.FC = () => {
  return (
    <Space>
      <Tenant />
      <Avatar />
    </Space>
  );
};
export default GlobalHeaderRight;
