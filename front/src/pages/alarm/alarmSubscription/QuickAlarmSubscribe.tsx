import {
  alarmSubscribeQueryById,
  alarmSubscribeSubmit,
} from '@/services/alarm/api';
import { useRequest } from 'ahooks';
import { message, Modal, Spin } from 'antd';
import { useEffect, useState } from 'react';
import AlarmSubscribe from '../../../components/AlarmSubscribe';
import $i18n from '../../../i18n';
interface Props {
  open: boolean;
  onClose: () => void;
  id: string | null;
  type: string;
}
function QuickAlarmSubscribe({ open, onClose, id, type }: Props) {
  const [subscriberObj, setSubscriberObj] = useState<any>({
    userSub: [],
    dingGroupSub: [],
    userGroupSub: [],
  });
  const { run, loading } = useRequest(alarmSubscribeQueryById, {
    manual: true,
    onSuccess: (res) => {
      const back = res?.alarmSubscribe || [];
      const otherArr = back.filter((item: any) => {
        return item?.noticeType ? !item?.noticeType.includes('webhook') : true;
      });
      const alarmSubscribe: any = {};
      alarmSubscribe.userSub = otherArr.filter((item: any) => {
        return item?.noticeType
          ? !item?.noticeType.includes('dingDingRobot') && item.subscriber
          : false;
      });
      alarmSubscribe.dingGroupSub = otherArr.filter((item: any) => {
        return item?.noticeType
          ? item?.noticeType.includes('dingDingRobot')
          : false;
      });
      alarmSubscribe.userGroupSub = otherArr.filter((item: any) => {
        return !item.noticeType.includes('dingDingRobot') && !item.subscriber;
      });
      setSubscriberObj(alarmSubscribe);
    },
  });

  const submitAlarmSubscribe = useRequest(alarmSubscribeSubmit, {
    manual: true,
    onSuccess: (res) => {
      if (res) {
        message.success(
          `${
            id
              ? $i18n.get({
                  id: 'holoinsight.pages.alarm.QuickAlarmSubscribe.EditSuccess',
                  dm: '编辑成功',
                })
              : $i18n.get({
                  id: 'holoinsight.pages.alarm.QuickAlarmSubscribe.AddSuccess',
                  dm: '新增成功',
                })
          } `,
        );
        onClose();
      }
    },
  });
  function getData(data: any) {
    if (data.type === 'userSub') {
      subscriberObj.userSub = data.data;
    } else if (data.type === 'dingGroupSub') {
      subscriberObj.dingGroupSub = data.data;
    } else {
      subscriberObj.userGroupSub = data.data;
    }
    setSubscriberObj(JSON.parse(JSON.stringify(subscriberObj)));
  }

  useEffect(() => {
    if (open && id) {
      run(type === 'pql' ? `pql_${id}` : `rule_${id}`);
    }
  }, [id, open]);

  function commonSubscribeSubmit() {
    const newObj: any = {};
    newObj.alarmSubscribe = subscriberObj.userSub
      .concat(subscriberObj.dingGroupSub)
      .concat(subscriberObj.userGroupSub);
    newObj.uniqueId = type === 'pql' ? `pql_${id}` : `rule_${id}`;
    submitAlarmSubscribe.run(newObj);
  }

  return (
    <Modal
      title={$i18n.get({
        id: 'holoinsight.pages.alarm.QuickAlarmSubscribe.SubscribeToAlarmRules',
        dm: '订阅报警规则',
      })}
      open={open}
      onCancel={onClose}
      confirmLoading={submitAlarmSubscribe.loading}
      onOk={commonSubscribeSubmit}
      width={800}
      destroyOnClose
    >
      <Spin spinning={loading}>
        <AlarmSubscribe
          subscriberObj={subscriberObj}
          getData={getData}
          type={id ? 'edit' : 'create'}
        />
      </Spin>
    </Modal>
  );
}
export default QuickAlarmSubscribe;
