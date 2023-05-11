import { getQuery, getUrlQueryString } from '../../utils/help';
import { TimePicker } from '@/Magi';

import { history } from '@umijs/max';
import { Form, Select } from 'antd';
import React, { useEffect} from 'react';
import qs from 'query-string';
import $i18n from '../../i18n';
import styles from './index.less';
import Tip from './Tip';

import { OFFSET_OPTIONS } from './const';

interface XProps {
  refreshTime?: number;
  context: {
    range: string;
    env: string;
  };
  delay?: number;
  onChange?: (value: any) => void;
  id?: string
}

function getState(range: string, env: string, delay?: number) {
  const { value, from, to } = TimePicker.parse(range, delay);
  return {
    env,
    range: value,
    starttime: from.valueOf(),
    endtime: to.valueOf(),
  };
}

const MagiRefeshContainer: React.FC<any> = (props: XProps) => {
  const { context, onChange, refreshTime, delay } = props;
  const [refresh, setRefresh] = React.useState(refreshTime || 60);
  const { range, env = 'PROD' } = context;
  const [state, setState] = React.useState(getState(range, env));
  
  useEffect(() => {
    setState(getState(range, env));
  }, [range, env]);
  
  useEffect(() => {
    onChange && onChange(getState(range, env));
  }, []);

  useEffect(() => {
    setRefresh(refreshTime || 60);
  }, [refreshTime]);

  const onRangeChange = (values: any) => {
    const { from, to, value } = values;
    const search = getQuery();
    const rangeObj = {
      starttime: from.valueOf(),
      endtime: to.valueOf(),
      range: value,
    };
    if (onChange) {
      history.replace({
        pathname: getUrlQueryString(),
        search: qs.stringify({
          ...{
            range: value,
          },
          tenant: '',
          ...search,
        }),
      });
      if (!document.hidden || document.visibilityState === 'visible') {
        onChange({ ...state, ...rangeObj });
        setState({ ...state, ...rangeObj });
      }
    }
  };

  

  return (
    <div className={styles.magiTimeRefesh}>
      <Form layout="inline" name="am-ctrl-bar">
        <Form.Item>
          <TimePicker
            refreshTime={refresh}
            delay={delay}
            offsetOptions={OFFSET_OPTIONS}
            value={state.range}
            onChange={onRangeChange}
          />
        </Form.Item>
        {(!state.range || state.range.includes('now')) && (
          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.components.MagiRefeshContainer.RefreshFrequency',
              dm: '刷新频率',
            })}
          >
            <Select
              value={refresh}
              style={{ width: 100 }}
              onChange={(value: number) => {
                setRefresh(value);
              }}
            >
              <Select.Option value={5}>
                {$i18n.get({
                  id: 'holoinsight.components.MagiRefeshContainer.Seconds',
                  dm: '5秒钟',
                })}
              </Select.Option>
              <Select.Option value={60}>
                {$i18n.get({
                  id: 'holoinsight.components.MagiRefeshContainer.Minute',
                  dm: '1分钟',
                })}
              </Select.Option>
            </Select>
            <Tip id="refresh-time" />
          </Form.Item>
        )}
      </Form>
    </div>
  );
};

export default MagiRefeshContainer;
