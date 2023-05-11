import { useState, useEffect } from 'react';
import { Radio, Space } from 'antd';
import $i18n from '@/i18n/index';
export default function LogSource({
  value,
  onChange,
  setBtnOpen,
  from,
  setSampleLog,
}: any) {
  const [selectValue, setSelectValue] = useState(
    value ? value?.selectValue : '',
  );
  if (!from) {
    setBtnOpen(value && value?.value ? true : false);
  }
  const triggerChange = (changedValue: { selectValue?: number }) => {
    onChange?.({ selectValue, ...value, ...changedValue });
  };
  useEffect(() => {
    if (value && value?.selectValue) {
      setSelectValue(value.selectValue);
    }
  }, [value && value?.selectValue]);
  return (
    <>
      <div style={{ wordBreak: 'break-word' }}>
        {value && value?.value ? (
          <Radio.Group
            onChange={(e) => {
              const newValue = e.target.value;
              setSelectValue(newValue);
              setSampleLog(newValue);
              triggerChange({ selectValue: newValue });
            }}
            value={selectValue}
          >
            <Space direction="vertical">
              {(value?.value || []).map((item: any, index: number) => {
                return (
                  <Radio key={index} value={item}>
                    {item}
                  </Radio>
                );
              })}
            </Space>
          </Radio.Group>
        ) : (
          <div>
            {$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogSegmentation.NoLogsAreAvailablePlease',
              dm: '暂无日志,请重新拉取或人工输入',
            })}
          </div>
        )}
      </div>
    </>
  );
}
