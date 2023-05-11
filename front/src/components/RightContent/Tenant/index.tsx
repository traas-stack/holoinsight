import React, { useEffect, useState } from 'react';
import { Select } from 'antd';
import { useModel, history } from 'umi';
import qs from 'query-string';
import Cookies from 'js-cookie';
const Tenant: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const [tenantValue, setTenant] = useState(initialState);
  const tenantList = initialState?.tenants || [];
  const handleChange = async (value: string) => {
    initialState.currentTenant = value;
    Cookies.set('loginTenant', value);
    setInitialState({ ...initialState });
    setTenant(value);
    history.push({ pathname: '/', search: qs.stringify({ tenant: value }) });
  };
  useEffect(() => {
    setTenant(initialState?.currentTenant);
  }, [initialState?.currentTenant]);
  return (
    <div>
      <Select
        style={{ width: 200 }}
        onChange={handleChange}
        placeholder="租户"
        value={tenantValue}
      >
        {tenantList.map((item: any, index: number) => {
          return (
            <Select.Option value={item.code} key={index}>
              {item.name}
            </Select.Option>
          );
        })}
      </Select>
    </div>
  );
};

export default Tenant;
