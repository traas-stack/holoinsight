import  { useEffect } from 'react';
import { Button, Form, Drawer, Input, Radio} from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import LogSource from '@/components/CommonLogSource';
import { previewFile } from '@/services/agent/api';
import _ from 'lodash';
import $i18n from '../../../i18n';

interface IProps {
  setVisible: (p: any) => void;
  visible: boolean;
  form: any;
  logType: number;
  setLogType: (p: any) => void;
  clearSplitResult: () => void;
  serverData: any,
  setSampleLog: any
}

const LogLoad = (props: IProps) => {
  const { visible, setVisible, form, logType = 1, setLogType, clearSplitResult, serverData, setSampleLog } = props;
  const [forms] = Form.useForm();
    // 反转义
  const _reverseEscape = (str: string) => {
      const arrEntities = { lt: '<', gt: '>', nbsp: ' ', amp: '&', quot: '"' };
      return str.replace(/&(lt|gt|nbsp|amp|quot);/gi, (all, t) => arrEntities[t]);
  };
  const handleClick = () => {
    const logSouceArr = form.getFieldValue('logPath').filter((item: any) => item.path);
    let preHost
    if (form.getFieldValue('preHost')) {
      preHost = form.getFieldValue('preHost');
    } else {
      const uk = form.getFieldValue('preHost') || form.getFieldValue('collectRanges')?.[0] || '';

      serverData.forEach((ele: any) => {
        if (ele.key === uk) {
          preHost = ele.title
        }
      });
    }

    let param = { logpath: logSouceArr?.[0].path };
    if (
      form.getFieldValue('typeFilter') === 'app' &&
      (_.isUndefined(form.getFieldValue('preHost')) || form.getFieldValue('preHost') === '')
    ) {
      param['app'] = form.getFieldValue('collectRanges')?.[0];
    } else if (
      form.getFieldValue('typeFilter') === 'label' &&
      (_.isUndefined(form.getFieldValue('preHost')) || form.getFieldValue('preHost') === '')
    ) {
      let labelMap = {};
      _.forEach(form.getFieldValue('collectRanges'), (item: any) => {
        labelMap[item.key] = item.value?.[0];
      });
      param['label'] = labelMap;
    } else {
      param['ip'] = preHost;
    }
    previewFile(param).then((res:any) => {
      if (!res || !res.lines) {
        // message.error(res);
        return;
      }
      const sampleLog = {
        value: res.lines.map((ele: any) => {
          return _reverseEscape(ele);
        }),
        selectValue: res?.lines?.[0] ? _reverseEscape(res?.lines?.[0]) : '',
      };
      form.setFieldsValue({ 
        sampleLog
      });
      forms.setFieldsValue(
        {
          logline: sampleLog
        }
      );
    });
  };

  const handleConfirm = () => {
    forms.validateFields().then((values) => {
      form.setFieldsValue({
        sampleLog: values.logType === 1 ? values.logline : {
          selectValue: values.content,
          value: [values.content],
        }
      });
      setLogType(values.logType);
      clearSplitResult();
      setVisible(false);
    });
  };

  const handleCancel = () => {
    forms.setFieldsValue({ logType });
    if (logType) {
      forms.setFieldsValue({
        content: '',
      });
    } else {
      forms.setFieldsValue({
        logline: '',
      });
    }
    setVisible(false);
  };

  useEffect(() => {
    if (logType === 1 && visible) {
      forms.setFieldsValue({
        logline: form.getFieldValue('sampleLog'),
        content: '',
      });
    } else {
      forms.setFieldsValue({
        logline: '',
        content: '',
      });
    }
  }, [logType, visible]);

  return (
    <Drawer
      title={$i18n.get({
        id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.LogSampleSource',
        dm: '日志样本来源',
      })}
      width={520}
      onClose={handleCancel}
      open={visible}
      bodyStyle={{ paddingBottom: 80 }}
      // getContainer={false}
      footer={
        <div
          style={{
            textAlign: 'right',
          }}
        >
          <Button onClick={handleCancel} style={{ marginRight: 8 }}>
            {$i18n.get({ id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.Cancel', dm: '取消' })}
          </Button>

          <Button onClick={handleConfirm} type="primary">
            {$i18n.get({ id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.Ok', dm: '确定' })}
          </Button>
        </div>
      }
    >
      <Form form={forms}>
        <Form.Item
          name="logType"
          colon={false}
          style={{ display: 'block' }}
          label={$i18n.get({
            id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.LogSource',
            dm: '日志来源',
          })}
          tooltip={{
            title: $i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PromptStatement',
              dm: '提示语句',
            }),
          }}
          initialValue={1}
        >
          <Radio.Group>
            <Radio value={1}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.OnlineLogs',
                dm: '线上日志',
              })}
            </Radio>

            <Radio value={0}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.ManualInput',
                dm: '人工输入',
              })}
            </Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item shouldUpdate={(prev, next) => prev.logType !== next.logType}>
          {(forms): any => {
            if (forms.getFieldValue('logType') === 1) {
              return (
                <>
                  {
                    <Form.Item
                      style={{ display: 'block' }}
                      label={$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.SpecifyTheServerIpAddress',
                        dm: '指定服务器IP',
                      })}
                      name="preHost"
                      tooltip={{
                        title: $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PromptStatement',
                          dm: '提示语句',
                        }),
                      }}
                    >
                      <Input
                        placeholder={$i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PleaseEnter',
                          dm: '请输入',
                        })}
                        onChange={(v: any) => {
                          form.setFieldsValue({ preHost: v.target.value?.trim() });
                        }}
                        suffix={
                          <a onClick={handleClick}>
                            <ReloadOutlined />
                            {$i18n.get({
                              id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PullAgain',
                              dm: '重新拉取',
                            })}
                          </a>
                        }
                      />
                    </Form.Item>
                  }

                  <Form.Item
                    colon={false}
                    label={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.LogSample',
                      dm: '日志样本',
                    })}
                    name="logline"
                    rules={[
                      {
                        required: true,
                        message: $i18n.get({
                          id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PullData',
                          dm: '请拉取数据',
                        }),
                      },
                    ]}
                    style={{ display: 'block' }}
                  >
                    {/* <Input.TextArea
                      className={styles.disabled}
                      style={{ padding: 10 }}
                      autoSize
                      bordered={false}
                      placeholder={$i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.NoLogsAreAvailablePlease',
                        dm: '暂无日志请重新拉取或人工输入',
                      })}
                      disabled
                    /> */}
                    <LogSource setSampleLog={setSampleLog} from="logDrawer" />
                  </Form.Item>
                </>
              );
            }
            return (
              <>
                <Form.Item
                  colon={false}
                  label={$i18n.get({
                    id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.LogInput',
                    dm: '日志输入',
                  })}
                  rules={[
                    {
                      required: true,
                      message: $i18n.get({
                        id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PleaseEnter',
                        dm: '请输入',
                      }),
                    },
                  ]}
                  style={{ display: 'block' }}
                  name="content"
                >
                  <Input.TextArea
                    style={{ minHeight: 200 }}
                    autoSize
                    placeholder={$i18n.get({
                      id: 'holoinsight.logMonitor.LogMonitorEdit.LogLoad.PleaseEnter',
                      dm: '请输入',
                    })}
                  />
                </Form.Item>
              </>
            );
          }}
        </Form.Item>
      </Form>
    </Drawer>
  );
};
export default LogLoad;
