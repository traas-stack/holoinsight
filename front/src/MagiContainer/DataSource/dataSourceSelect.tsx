import { Select } from 'antd';
import React from 'react';
import $i18n from '../../i18n';

const options = [
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.NonComplementaryValue',
      dm: '不补值',
    }),
    value: 'none',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.AddNan',
      dm: '补NAN',
    }),
    value: 'nan',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.Null',
      dm: '补null',
    }),
    value: 'null',
  },
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.Add',
      dm: '补0',
    }),
    value: 'zero',
  },
];

const pqlOption = [
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.Add',
      dm: '补0',
    }),
    value: true,
  },
  {
    label: $i18n.get({
      id: 'holoinsight.MagiContainer.DataSource.dataSourceSelect.NonComplementaryValue',
      dm: '不补值',
    }),
    value: false,
  },
   
]

const DownOptions = [
  {
    label: '1s',
    value: '1s',
  },
  {
    label: '5s',
    value: '5s',
  },
  {
    label: '10s',
    value: '10s',
  },
  {
    label: '1m',
    value: '1m',
  },
  {
    label: '1h',
    value: '1h',
  },
  {
    label: '1d',
    value: '1d',
  },
];

export const FillPolicySelect: React.FC<any> = (props:any) => {
  const {value,onChange} = props
  return (
    <Select
      value={value}
      onChange={(value) => {
        onChange(value);
      }}
      options={options}
      allowClear
    />
  );
};


export const FillPolicyPromuthus:React.FC<any> = (props:any) => {
  const {value = true,onChange} = props
  return (
    <Select
      value={value}
      onChange={(value) => {
        onChange(value);
      }}
      defaultValue = ""
      options={pqlOption}
    />
  );
};

export const DownsampleSelectPromuthus: React.FC<any> = ({ value = '1m', onChange }) => {
  return (
    <Select
      value={value}
      onChange={(value) => {
        onChange(value);
      }}
      options={DownOptions}
      allowClear
    />
  );
};

export const DownsampleSelect: React.FC<any> = ({ value, onChange }) => {
  return (
    <Select
      value={value}
      onChange={(value) => {
        onChange(value);
      }}
      options={DownOptions}
      allowClear
    />
  );
};
