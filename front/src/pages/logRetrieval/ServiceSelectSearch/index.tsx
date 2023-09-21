import { getTenantAppByCondition } from '@/services/infra/api';
import React from 'react';
import { useRequest } from 'ahooks';
import { Select } from 'antd';
import type { SelectProps } from 'antd/es/select';
import find from 'lodash/find';
import { useMemo } from 'react';
import type { ElementType } from '../SelectSearch';

interface IProps extends SelectProps {
  serviceId?: string;
  serviceInfos?: { id: string }[];
  onSetServiceId?: React.Dispatch<React.SetStateAction<string | undefined>>;
  serviceName?: string;
  onSetServiceName?: React.Dispatch<React.SetStateAction<string | undefined>>;
  onSetServiceInfos?: React.Dispatch<
    React.SetStateAction<{ id: string; name: string }[]>
  >;
  useMultiService?: boolean;
  initial?: boolean;
  showLabel?: boolean;
  style?: React.CSSProperties;
}

const ServiceSelectSearch: React.FC<IProps> = ({
  serviceId,
  serviceInfos = [],
  onSetServiceId,
  onSetServiceName,
  onSetServiceInfos,
  initial = true,
  showLabel = true,
  useMultiService = false,
  style,
  ...antdProps
}) => {
  const { data, loading } = useRequest(getTenantAppByCondition, {
    defaultParams: [{}],
  });
  const options = useMemo(() => {
    if (data?.length) {
      return data.map((item) => ({
        label: item.app,
        value: item.app,
      }));
    }
    return [];
  }, [data]);
  const handleMultiServiceInfo = (
    values: string[],
    elements?: ElementType[],
  ) => {
    const data = values.map((value) => {
      const item = find(elements, { value });
      return {
        id: value,
        name: item?.label || '',
      };
    });

    onSetServiceInfos?.(data);
  };

  const handleServiceInfo = (e: string, elements?: ElementType[]) => {
    onSetServiceInfos?.([
      {
        id: e,
        name: e,
      },
    ]);
  };

  return (
    <>
      <span>{showLabel && '应用：'}</span>
      <Select
        placeholder="请选择应用"
        loading={loading}
        style={style}
        value={
          useMultiService ? serviceInfos.map((info) => info.id) : serviceId
        }
        mode={useMultiService ? 'multiple' : undefined}
        onChange={useMultiService ? handleMultiServiceInfo : handleServiceInfo}
        options={options}
        {...antdProps}
      />
      {/*<SelectSearch*/}
      {/*  showSearch*/}
      {/*  placeholder="请选择服务"*/}
      {/*  style={style}*/}
      {/*  value={*/}
      {/*    useMultiService ? serviceInfos.map((info) => info.id) : serviceId*/}
      {/*  }*/}
      {/*  mode={useMultiService ? 'multiple' : undefined}*/}
      {/*  onSelectChange={useMultiService ? undefined : handleServiceInfo} // 单选事件*/}
      {/*  onMultilSelectChange={*/}
      {/*    useMultiService ? handleMultiServiceInfo : undefined*/}
      {/*  } // 多选事件*/}
      {/*  onFetchData={getServiceList}*/}
      {/*  onFirstLoad={initial ? handleServiceInfo : undefined}*/}
      {/*  {...antdProps}*/}
      {/*/>*/}
    </>
  );
};

export default ServiceSelectSearch;
