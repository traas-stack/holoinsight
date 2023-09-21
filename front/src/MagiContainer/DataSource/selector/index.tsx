import type { FormInstance } from 'antd';
import { Button, Form, message, Modal, Radio } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import $i18n from '../../../i18n';
import { TYPES } from '../../constants';
import CloudSelector from './cloud';
import { formatTarget } from './format';
import c from './index.less';
import LogSelector from './log';
import PrometheusSelector from './prometheus';
export interface SubTypeSelectorProps {
  form: FormInstance;
}
export interface TargetSource {
  pluginType: 'LOG_MONITOR' | 'PROMETHEUS' | 'CLOUD_MONITOR';
  id: string; 
  tables: {
    labels: any; 
    metrics: any; 
    multi?: boolean;
  };
  [key: string]: any; 
}

function isDsChange(cur: any, obj: any) {
  if (!cur || obj.type !== cur.type) return true;
  return obj.dsId !== cur.source.dsId;
}

const DsSelector: any = ({ visible, cur, onCancel, onOk }) => {
  const [form] = Form.useForm();
  const [btnLoading, setBtnLoading] = useState(false);
  const [treeReloadInitKeys, setTreeReloadInitKeys] = useState();
  const childdRef = useRef<any>();
  const save = async () => {
    setBtnLoading(true);

    form.validateFields().then(async()=>{
      const obj = form.getFieldsValue(true);
      if (!isDsChange(cur, obj)) {
        onOk(cur!);
        setBtnLoading(false);
        return;
      }
      const target = await formatTarget(obj);
      if (target) {
        onOk(target);
        form.resetFields();
      } else {
        message.error(
          $i18n.get({
            id: 'holoinsight.DataSource.selector.AbnormalIndicatorSelectionPleaseRe',
            dm: '指标选择异常 请重新选择指标',
          }),
        );
       
      }
    }).catch((e)=>{

    }).finally(()=>{
      setBtnLoading(false)
    });    
  };
  useEffect(() => {
    if (!cur) {
      form.resetFields();

      return;
    }
    const { metrics } = cur;
    const { dsId, position, pluginType, type } = cur.source;
    form.setFieldsValue({
      metrics: metrics?.[0],
      type: pluginType || type,
    });
    if (pluginType === 'CLOUD_MONITOR') {
      form.setFieldsValue({
        ...form.getFieldsValue(),
        metrics: dsId,
      });
      setTreeReloadInitKeys([]);
    }
    if (pluginType === 'LOG_MONITOR') {
      setTreeReloadInitKeys(position);
    }
    if (type === 'PROMETHEUS') {
      form.setFieldsValue({
        metrics: metrics?.[0],
      });
    }
  }, [cur]);
  return (
    <Modal
      open={visible}
      onCancel={onCancel}
      title={$i18n.get({
        id: 'holoinsight.DataSource.selector.SelectADataSource',
        dm: '选择数据源',
      })}
      onOk={save}
      destroyOnClose={true}
      className={c.selector}
      footer={[
        <Button key="back" onClick={onCancel}>
          {$i18n.get({
            id: 'holoinsight.DataSource.selector.Cancel',
            dm: '取消',
          })}
        </Button>,
        <Button key="submit" type="primary" loading={btnLoading} onClick={save}>
          {$i18n.get({
            id: 'holoinsight.DataSource.selector.Save',
            dm: '保存',
          })}
        </Button>,
      ]}
    >
      <Form form={form} initialValues={{ type: TYPES.cloud }}>
        <Form.Item name="type" noStyle>
          <Radio.Group
            className={c.radioSelect}
            onChange={(e) => {
              form.resetFields();
              form.setFieldsValue({
                type: e.target.value,
              });
            }}
          >
            <Radio.Button value={TYPES.cloud}>
              {$i18n.get({
                id: 'holoinsight.DataSource.selector.CloudMonitoring',
                dm: '云监控',
              })}
            </Radio.Button>
            <Radio.Button value={TYPES.log}>
              {$i18n.get({
                id: 'holoinsight.DataSource.selector.LogMonitoring',
                dm: '日志监控',
              })}
            </Radio.Button>
            <Radio.Button value={TYPES.prometheus}>
              {$i18n.get({
                id: 'holoinsight.DataSource.selector.PqlQuery',
                dm: 'PQL查询',
              })}
            </Radio.Button>
          </Radio.Group>
        </Form.Item>
        <Form.Item noStyle shouldUpdate={(pre, cur) => pre.type !== cur.type}>
          {({ getFieldValue }) => {
            switch (getFieldValue('type')) {
              case TYPES.cloud:
                return <CloudSelector form={form} />;
              case TYPES.log:
                return (
                  <LogSelector
                    form={form}
                    refss={childdRef}
                    treeReloadInitKeys={cur ? treeReloadInitKeys : []}
                  />
                );

              case TYPES.prometheus:
                return <PrometheusSelector form={form} />;
              default:
                return '';
            }
          }}
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DsSelector;
