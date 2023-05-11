import { Select, Spin } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { useDebounceFn, useThrottleFn } from 'ahooks';
import type { SelectProps } from 'antd/es/select';

export interface ElementType {
  label?: string | JSX.Element;
  value?: string;
  disabled?: boolean;
}

interface IProps extends SelectProps {
  startFetch?: boolean;
  updateFetch?: string;
  onFetchData: (params: any) => Promise<ElementType[]>;
  onFirstLoad?: (e: string, elements?: ElementType[]) => void;
  onSelectChange?: (e: string, elements?: ElementType[]) => void;
  onMultilSelectChange?: (e: string[], elements?: ElementType[]) => void;
}

const { Option } = Select;

const SelectSearch: React.FC<IProps> = ({
  startFetch,
  updateFetch,
  onFetchData,
  onFirstLoad,
  onSelectChange,
  onMultilSelectChange,
  ...antdProps
}) => {
  const [loading, setLoading] = useState(false);
  const [isEnd, setIsEnd] = useState(false);
  const [elements, setElements] = useState<ElementType[]>([]);
  const initRef = useRef(true);

  const [pageInfo, setPageInfo] = useState({
    pageNo: 1,
    pageSize: 10,
    query: '',
  });

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    // 判断滑动到底部
    const { scrollTop, scrollHeight, clientHeight } = e.target as Element;
    return scrollHeight - scrollTop === clientHeight;
  };

  /**
   * 查询接口
   * @param giveupPrev 丢弃之前的结果
   */
  const getRunResult = async (giveupPrev?: boolean, pageNo?: number) => {
    setLoading(true);
    try {
      const data = await onFetchData({
        ...pageInfo,
        pageNo: pageNo ?? pageInfo.pageNo,
      });

      if (giveupPrev) {
        setElements([...data]);
      } else {
        setElements([...elements, ...data]);
      }

      if (initRef.current) {
        onFirstLoad?.(data?.[0].value ?? '', data);
      }
      initRef.current = false;

      if (data?.length < pageInfo.pageSize) {
        setIsEnd(true);
      }
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (startFetch !== false) {
      getRunResult();
    }
  }, [startFetch]);

  useEffect(() => {
    if (updateFetch === undefined || !startFetch) {
      return;
    }
    setPageInfo({ pageNo: 1, pageSize: 10, query: '' });
    getRunResult(true, 1);
  }, [updateFetch]);

  const handlePopupScroll = (e: React.UIEvent<HTMLDivElement>) => {
    // 加载
    if (handleScroll(e) && !loading && !isEnd) {
      const nextPage = pageInfo.pageNo + 1;
      setPageInfo({
        ...pageInfo,
        pageNo: nextPage,
      });
      getRunResult(false, nextPage);
    }
  };

  const { run: runPopupScroll } = useThrottleFn(
    (e) => {
      handlePopupScroll(e);
    },
    { wait: 200 },
  );

  const { run: runSearch } = useDebounceFn(
    () => {
      setIsEnd(false);
      getRunResult(true);
    },
    { wait: 200 },
  );
  return (
    <Select
      filterOption={() => true}
      loading={loading}
      onPopupScroll={runPopupScroll}
      onSelect={(e: string) => onSelectChange?.(e, elements)}
      onChange={(e: string[]) => onMultilSelectChange?.(e, elements)}
      {...antdProps}
      onSearch={
        antdProps.showSearch
          ? (e: string) => {
              setPageInfo({
                ...pageInfo,
                pageNo: 1,
                query: e,
              });
              runSearch();
            }
          : undefined
      }
    >
      {elements?.map((item) => (
        <Option key={item.value} value={item.value} disabled={item.disabled}>
          {item.label}
        </Option>
      ))}
      {loading && (
        <Option
          key="loading"
          value="loading"
          disabled
          style={{ textAlign: 'center' }}
        >
          <Spin />
        </Option>
      )}
    </Select>
  );
};
export default SelectSearch;
